# STYLO DSP — BRANCH INVENTORY / SECOND RADIOGRAPHY

**Date:** 2026-08-25  
**Scope:** historical branch classification with focused inspection of the highest-value Android/Rust/EQ lines.

## Critical correction

The repository contains a **historical, physically device-validated Android/Rust milestone** that must not be confused with the current `main` runtime state.

`android-v0.2.0-validated` documents a physical-device validation of an APK produced from `prototype/android-apk`. The architecture was:

`Android → Oboe/AAudio → Native bridge/FFI → STYLO DSP Core (Rust) → Gain (f32)`.

The historical validation records an `UnsatisfiedLinkError` caused by an absolute CI-runner ELF dependency and a subsequent SONAME/DT_NEEDED correction. It records build commit `85f4bdbf6030cbc3d83903874c0e01e033bb80fb`, Actions run `31965884079`, and `arm64-v8a`. This is V0.2.0 historical validation evidence, not proof that the latest `main` APK has the same runtime path or is already validated. fileciteturn134file0L2-L2

## `core-rust`

**Disposition:** `KEEP / RECOVER SELECTIVELY`

Comparison against `main`: 3 commits ahead of the shared merge base and 68 commits behind current `main`. fileciteturn122file0L2-L2

### Exclusive commits

1. `23ce61d5...` — `core: scaffold Rust DSP core`
   - adds `core/Cargo.toml`;
   - package `stylo-dsp-core`;
   - `rlib` + `cdylib` outputs;
   - establishes the platform-independent Core crate. fileciteturn127file0L3-L12

2. `e3357e4e...` — `core: add realtime-safe gain processor`
   - adds `core/src/lib.rs`;
   - `Gain` uses `f32`;
   - in-place buffer processing;
   - unit tests for unity, expected samples and finite values. fileciteturn129file0L3-L12

3. `3badc0f9...` — `core: document first Rust prototype`
   - adds `core/README.md`;
   - defines PCM → Core → Gain → PCM as the first target;
   - records Android/Web/VST3 adapters as pending;
   - records TypeScript as reference until parity. fileciteturn132file0L3-L12

### Assessment

Real engineering work worth preserving, but only a first Core/Gain slice, not a complete replacement for the current Android DSP path.

**Action:** keep branch; later evaluate cherry-picking Core/Gain after current-main validation and API-boundary review.

## `android-v0.2.0-validated`

**Disposition:** `KEEP AS HISTORICAL VALIDATION REFERENCE / RECOVER SELECTIVELY`

Comparison against `main`: 24 commits ahead of the merge base and 68 commits behind current `main`. fileciteturn123file0L2-L2

The branch contains:
- Android native C++ bridge;
- CMake/native build configuration;
- Java Android UI;
- Rust Core;
- validation documentation.

Its `docs/ANDROID_V0.2.0_VALIDATED.md` provides explicit physical-device validation evidence and the ELF/SONAME fix. fileciteturn133file0L2-L2 fileciteturn134file0L2-L2

### Assessment

This proves the project previously achieved a **device-validated Android/Rust/Gain path**. It is therefore not merely a hypothetical prototype. Its age and divergence mean it is not automatically the current canonical implementation.

**Action:** preserve as historical reference; extract build/runtime lessons and compare unique native/Rust code before any cleanup.

## `android-gain-realtime`

**Disposition:** `KEEP / INVESTIGATE DEEPLY`

Comparison against `main`: 33 commits ahead of the merge base and 68 behind current `main`. The branch contains a substantial Android/Rust/native slice including:

- `.cargo/config.toml`;
- dedicated Android workflow;
- Android Gradle/CMake/native bridge;
- Java `MainActivity`;
- Rust `core/Cargo.toml` and `core/src/lib.rs`;
- `docs/ANDROID_V0.2.0_VALIDATED.md`;
- `docs/DSP_DEPENDENCY_AUDIT.md`;
- `docs/REALTIME_GAIN_GATE.md`. 

The compared files include 98 lines of Rust core code and 139 lines of native C++ bridge code, plus a dedicated Android workflow. 

**Action:** high priority. This is likely the richest historical source for understanding the validated Android/Rust Gain integration. Do not merge blindly.

## `prod/full-eq-v1`

**Disposition:** `ARCHIVE / EXTRACT SELECTIVELY`

Comparison against `main`: 16 commits ahead of the merge base and 49 behind current `main`.

Unique files include:
- `AudioEqProcessor.kt`;
- `EqModel.kt`;
- `FastFft.kt`;
- a different `MainActivity.kt` generation;
- workflow changes.

This is valuable historical EQ/UI/FFT work but belongs to an older Android generation and should not be mixed into the current PCM pipeline without evidence.

## Other important branches

| Branch | Classification | Immediate action |
|---|---|---|
| `v0.2.1-stylo-eq-mvp` | Historical MVP | Audit later |
| `v0.2.2-first-apk-mvp` | Historical APK line | Audit later |
| `prototype/android-apk` | Parent/reference for historical V0.2.0 | Preserve |
| `prototype/android-apk-v2` | Historical prototype | Audit later |
| `ci/android-build-verification` | Superseded CI line | Preserve until final classification |
| `pre-final-build-2026-08-24` | Historical checkpoint | Preserve |
| Harness/checkpoint branches | Historical process state | Audit after feature branches |

## Version nomenclature decision

The existence of a branch named `android-v0.2.0-validated` does not mean that every subsequent Android state is V0.2.0. It represents a historical milestone.

Future official product releases should use tags/releases such as `V0.x.x`. Development branches should use `feature/`, `experiment/` or `archive/` semantics.

## Current conclusion

The second radiography has now established at least **three distinct technical generations**:

1. **Historical V0.2.0 Android/Rust/Gain path:** physically validated, with native ELF packaging fix.
2. **Historical full-EQ Android generation:** system/software EQ + FFT + older UI implementation.
3. **Current `main` Kotlin/PCM/DSP generation:** more recent and more feature-rich, but latest runtime still needs current-device validation.

The next goal is not to merge these generations. It is to recover the best proven components and lessons while keeping one canonical current path.
