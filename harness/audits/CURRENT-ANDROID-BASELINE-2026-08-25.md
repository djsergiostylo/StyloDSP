# STYLO DSP — CURRENT ANDROID BASELINE

**Date:** 2026-08-25
**Ref:** `main` at `4f25f97a9996ab1f1f75bead51bd57819b13897a`
**Purpose:** freeze the audited starting point before product implementation resumes.

## Product objective
Build the canonical Android APK for STYLO DSP: an audio player with a usable 31-band graphic EQ, 8-band parametric EQ, realtime spectrum analyzer, transport controls, presets and mobile-oriented interaction.

## Current Android architecture

`MediaExtractor → MediaCodec → PCM → EqBank/Biquad → SafetyLimiter → AudioTrack`

Spectrum path:

`PCM → FFT (2048 radix-2) → throttled UI spectrum`

The canonical player implementation is `android/app/src/main/java/com/stylo/dsp/PcmPlayerEngine.kt`. It decodes audio to PCM, applies the current EQ banks, applies a limiter/clamp, writes PCM to `AudioTrack`, and optionally generates spectrum data. fileciteturn253file0

DSP primitives are currently in `android/app/src/main/java/com/stylo/dsp/DspEngine.kt`, including radix-2 FFT, `EqBand`, `Biquad`, `EqBank`, and `SafetyLimiter`. fileciteturn255file0

The UI is currently implemented in `android/app/src/main/java/com/stylo/dsp/MainActivity.kt`, with file selection, transport, seek, loop, volume, 31-band/8-band modes, bypass, A/B, preset save/load and direct graph interaction. fileciteturn256file0

## Build baseline

`android/app/build.gradle.kts` currently declares:

- applicationId `com.stylo.dsp`
- compileSdk 35
- minSdk 26
- targetSdk 35
- versionCode 3
- versionName `0.3.0`
- JVM toolchain 17. fileciteturn257file0

The repository contains a GitHub Actions Android workflow that installs Android platform/build tools 35, uses JDK 17 and Gradle 8.13, runs Kotlin compilation and unit tests, then builds debug APK, release APK and release AAB and checks that the artifacts exist. fileciteturn262file0

## Verification truth

### Confirmed from source
- Android source exists in `main`.
- Current player path is PCM-based and includes EQ + FFT + limiter.
- 31-band and 8-band models are wired from the UI into the player.
- Build workflow exists and requests unit tests.
- Historical V0.2.0 Android/Rust/Gain physical-device validation exists on a historical branch.

### Not yet confirmed for current `main`
- Current APK installed and launched on a physical device.
- Current audio playback through the current PCM path on a physical device.
- Current 31-band EQ is audibly correct on device.
- Current parametric Q/filter-type behavior is audibly correct on device.
- Current spectrum remains smooth under real playback.
- Preset persistence survives process restart on device.
- No audio glitches, clipping, excessive latency or lifecycle leaks under realistic use.
- Production release gate.

## Historical evidence boundary

The V0.2.0 validated line used Android + Oboe/AAudio + native bridge/FFI + Rust DSP Core + Gain. That historical physical validation must not be reused as evidence that the current Kotlin/PCM `main` path is runtime-validated.

## Audit conclusion

The repository reconciliation phase is sufficiently complete to resume product engineering. No historical branch should be merged wholesale. Recover proven components only through explicit comparison and tests.

## First product-engineering sequence

1. Build current `main` from a clean checkout.
2. Run unit tests and inspect APK artifact.
3. Install current debug APK on the reference Android device.
4. Validate file open → decode → PCM output → play/pause/seek/loop/volume.
5. Validate 31-band audible response, bypass and flat/reset.
6. Validate 8-band frequency/gain/Q/type behavior.
7. Validate spectrum correctness and UI frame stability.
8. Validate presets across process restart.
9. Fix only evidence-backed defects.
10. Re-run all gates and record the device evidence before changing version status.
