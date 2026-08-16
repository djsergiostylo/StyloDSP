# STYLO DSP Architecture

## 1. Architectural objective

STYLO DSP is designed as a **platform-independent DSP core** that can be hosted by Web, Android and VST3 without duplicating the DSP algorithms.

```text
                         STYLO DSP CORE
                              │
             ┌────────────────┼────────────────┐
             │                │                │
          WEB ADAPTER     ANDROID ADAPTER   VST3 ADAPTER
             │                │                │
          WASM/JS         Native/FFI        Native/VST3
             │                │                │
          AudioWorklet     Oboe/AAudio          DAW
             │                │                │
             ▼                ▼                ▼
            WEB              APK              VST3
```

The Core must not depend on React, Kotlin, Android framework APIs, Web Audio API or VST SDK APIs.

## 2. Target technology

### DSP Core

**Rust** is the target implementation language for the portable DSP core.

Reasons:

- one implementation of DSP algorithms;
- native compilation for Android and desktop;
- WebAssembly target for Web;
- explicit memory and threading model;
- suitable control over realtime audio constraints;
- clear FFI boundary for platform adapters.

### Web

```text
Rust DSP Core
    ↓
WebAssembly
    ↓
AudioWorklet
    ↓
Web Audio API
```

### Android

```text
Kotlin / Compose UI
    ↓
Android adapter
    ↓
FFI
    ↓
Rust DSP Core
    ↓
Oboe / AAudio
```

The Android audio path should prefer AAudio/Oboe low-latency operation when the device and route support it, with a safe fallback when the optimal mode is unavailable.

### VST3

```text
DAW
 ↓
VST3 Adapter
 ↓
Rust DSP Core
```

The VST3 adapter owns host-specific lifecycle, parameter automation and audio-buffer integration. DSP algorithms remain in the Core.

## 3. DSP numerical policy

### General processing: f32

`f32` is the default representation for realtime audio processing.

This reduces memory bandwidth and is well suited to ARM NEON and other SIMD execution paths.

### Selective f64

`f64` may be used only where numerical analysis demonstrates a meaningful benefit, for example selected filter structures or numerically sensitive calculations.

The project must not default to an all-`f64` DSP pipeline.

## 4. SIMD policy

DSP algorithms should be written against a SIMD-capable abstraction rather than embedding Android-specific NEON code throughout the algorithms.

Target execution paths:

```text
DSP algorithm
     ↓
SIMD abstraction
 ┌───┼───────────┐
 ↓   ↓           ↓
NEON WASM SIMD  Scalar fallback
```

ARM NEON is an optimization target for Android/ARM devices. A scalar fallback remains mandatory for portability and correctness.

SIMD and scalar implementations must produce results within documented numerical tolerances.

## 5. Realtime audio rules

The realtime audio callback is a hard realtime boundary.

Inside the audio callback:

- no heap allocation;
- no deallocation;
- no blocking locks;
- no filesystem I/O;
- no network I/O;
- no logging that can block;
- no UI calls;
- no garbage collection dependency.

All buffers required by the processing path must be preallocated before realtime processing begins.

### Zero-allocation runtime

The engine should preallocate:

- input/output buffers;
- intermediate module buffers;
- delay/lookahead buffers;
- analyzer working memory;
- automation queues;
- meter state.

Dynamic allocation is permitted during configuration/initialization but not during realtime processing.

## 6. Lock-free parameter transport

UI parameter changes must not block the audio thread.

Target model:

```text
UI thread
   │
   ├── atomic scalar parameters
   │
   └── SPSC ring buffer for queued events
                 │
                 ▼
            audio thread
```

Atomic values are appropriate for simple current-state parameters. An SPSC ring buffer is preferred for ordered parameter/automation events.

The exact atomic representation must be validated for the target Rust/platform implementation rather than assuming a generic `AtomicF32` exists everywhere.

## 7. Android audio bridge

The Android adapter should isolate Android audio APIs from the DSP implementation.

Preferred direction:

```text
Android
  ↓
Oboe / AAudio callback
  ↓
thin native bridge
  ↓
Rust DSP Core
```

The native bridge must remain thin. JNI should not sit inside the realtime processing loop.

Where practical, Rust should expose a stable C-compatible ABI (`extern "C"`) for the native bridge. The exact JNI/FFI arrangement will be validated in the first Android prototype.

### Low latency

The Android adapter should request:

- AAudio/Oboe;
- low-latency performance mode;
- suitable sample rate and buffer size;
- exclusive mode only when supported and beneficial.

Exclusive mode is **not** a universal requirement. The implementation must provide a fallback for devices/routes that do not support it.

## 8. Denormal/subnormal protection

The DSP runtime must protect against excessive CPU cost caused by subnormal floating-point values.

The preferred approach is platform-appropriate FTZ/DAZ handling where safe and available.

Dither/noise injection must not be used as a blanket substitute for correct denormal handling. If noise is used, its spectral and perceptual consequences must be explicitly tested.

## 9. Metering architecture

Meters are calculated in the DSP layer but consumed asynchronously by the UI.

```text
Audio callback
     ↓
Meter state
     ↓
atomic/snapshot state
     ↓
UI polling
     ↓
30/60 FPS
```

The DSP must not push a UI update for every audio frame.

The UI may poll snapshots at approximately 30 FPS for ordinary controls and up to 60 FPS for visual meters when required.

## 10. Core module model

The portable Core owns:

- module identity;
- module type;
- editable module name;
- enabled/bypass state;
- parameters;
- validation;
- DSP processing;
- reset;
- serialization;
- parameter metadata.

`ChainManager` remains a Core/Engine concern and must not depend on the UI.

Supported chain operations include:

```text
add
remove
duplicate
move/reorder
rename
enable/disable
validate
serialize
restore
```

## 11. Presets and state

The preset format must be platform-independent.

Example conceptual structure:

```json
{
  "format": "stylo-preset",
  "version": 1,
  "name": "Techno Master 01",
  "sampleRate": 48000,
  "modules": [
    {
      "type": "gain",
      "name": "Input Trim",
      "enabled": true,
      "params": {}
    }
  ]
}
```

The same preset should be restorable on Web, Android and VST3, subject to documented host-specific capabilities.

## 12. First Android proof of architecture

The first Android branch must be intentionally small.

### Prototype goal

Prove this path:

```text
WAV / generated PCM
      ↓
Android
      ↓
Oboe / AAudio
      ↓
Rust FFI
      ↓
STYLO DSP CORE
      ↓
Gain module
      ↓
Audio output
```

The first APK does **not** need the final UI or all DSP modules.

### First acceptance tests

1. APK builds reproducibly in CI.
2. Rust Core builds for the Android target.
3. Android can instantiate the Core.
4. Audio buffers reach the Core.
5. Gain processing produces expected numerical output.
6. Audio output remains finite and stable.
7. No allocation occurs in the realtime processing callback.
8. Parameter changes do not block the audio thread.
9. The app has a safe fallback when low-latency/exclusive configuration is unavailable.

## 13. Cross-platform parity tests

A reference PCM test vector should be processed through:

```text
Rust scalar reference
Rust SIMD
Web/WASM
Android
VST3
```

Outputs must remain within explicitly documented tolerances.

This is more important than merely checking that each platform "runs".

## 14. Repository direction

Target structure:

```text
StyloDSP/
├── core/
│   ├── dsp/
│   ├── engine/
│   ├── modules/
│   ├── parameters/
│   ├── state/
│   └── presets/
│
├── adapters/
│   ├── web/
│   ├── android/
│   └── vst3/
│
├── apps/
│   ├── web/
│   └── android/
│
├── plugins/
│   └── vst3/
│
├── tests/
└── docs/
```

Migration must be incremental. Existing TypeScript DSP code and tests remain available as a reference until each module has an equivalent Core implementation and passing parity tests.

## 15. Development gates

Do not advance to the next platform layer if the previous gate is not green.

```text
Gate 1  Rust Core builds
Gate 2  Gain DSP tests pass
Gate 3  realtime safety tests pass
Gate 4  Android APK prototype works
Gate 5  Web/WASM parity passes
Gate 6  additional DSP modules migrate
Gate 7  VST3 adapter prototype
Gate 8  cross-platform preset/parity validation
```

The first implementation target is **Core + Gain + Android APK prototype**, not the complete application UI.
