# STYLO DSP — ARCHITECTURE MAP

## Rule

Separate the architecture that exists from the architecture that is planned.

## Current canonical Android path

`MediaExtractor`
→ `MediaCodec`
→ decoded PCM 16-bit
→ per-channel `EqBank`
→ `SafetyLimiter`
→ `AudioTrack`

FFT/spectrum analysis runs from the decoded/processed PCM path when enabled.

The player engine also owns transport state such as play/pause, seek, loop, bypass and volume.

## Current implementation status

### Android/Kotlin
Canonical active implementation on `main`.

### DSP
Software EQ processing exists in the Android playback path.

### FFT
Implemented in the current player engine as an optional spectrum callback path.

### Rust
Planned/experimental architecture. Do not treat the Rust Core as the current Android DSP implementation until it is integrated, built and runtime-validated.

### WASM/VST3
Future platform targets, not current release-critical implementation.

## Architectural principle

Do not perform a Rust migration merely because a Rust architecture exists in historical documentation. First validate the current Android path and establish a migration boundary and API contract.

## Future target

The long-term direction is a reusable DSP core that can support Android and other targets such as WASM/VST3 while keeping real-time processing deterministic and allocation-safe.
