# STYLO DSP — BRANCH INVENTORY / SECOND RADIOGRAPHY

**Date:** 2026-08-25  
**Scope:** historical branch classification, with deep inspection of `core-rust` and `android-v0.2.0-validated`.

## Critical correction

The repository contains a **historical, physically device-validated Android/Rust milestone** that must not be confused with the current `main` runtime state.

`android-v0.2.0-validated` documents a physical-device validation of an APK produced from `prototype/android-apk`. The documented architecture was:

`Android → Oboe/AAudio → Native bridge/FFI → STYLO DSP Core (Rust) → Gain (f32)`.

The historical validation also records an `UnsatisfiedLinkError` caused by an absolute CI-runner ELF dependency and a subsequent SONAME/DT_NEEDED correction. It records build commit `85f4bdbf6030cbc3d83903874c0e01e033bb80fb`, Actions run `31965884079`, and `arm64-v8a`. This is historical evidence from the V0.2.0 validation milestone, not proof that the latest `main` APK has the same runtime path or is already validated. fileciteturn134file0L2-L2

## `core-rust`

**Disposition:** `KEEP / RECOVER SELECTIVELY`

Comparison against `main` shows the branch is 3 commits ahead of the shared merge base and 68 commits behind the current `main` tip. fileciteturn122file0L2-L2

### Exclusive historical commits

1. `23ce61d5...` — `core: scaffold Rust DSP core`
   - Adds `core/Cargo.toml`.
   - Defines package `stylo-dsp-core`.
   - Defines `rlib` + `cdylib` outputs.
   - Starts the platform-independent Rust Core. fileciteturn127file0L3-L12

2. `e3357e4e...` — `core: add realtime-safe gain processor`
   - Adds `core/src/lib.rs`.
   - Implements `Gain` using `f32`.
   - Processes existing PCM buffers in-place.
   - Includes unit tests for unity gain, expected sample processing and finite outputs. fileciteturn129file0L3-L12

3. `3badc0f9...` — `core: document first Rust prototype`
   - Adds `core/README.md`.
   - Explicitly defines the first target as PCM buffer → Core → Gain → PCM buffer.
   - States Android/Web/VST3 adapters were still pending at that milestone.
   - States TypeScript should remain a reference until parity testing. fileciteturn132file0L3-L12

### Assessment

This is **real engineering work worth preserving**, but it is a small first Core/Gain slice, not a complete replacement for the current Android DSP path.

**Action:** preserve branch; later evaluate cherry-picking the Core/Gain work after current-main validation and API-boundary review.

## `android-v0.2.0-validated`

**Disposition:** `KEEP AS HISTORICAL VALIDATION REFERENCE / RECOVER SELECTIVELY`

The branch is 24 commits ahead of the merge base and 68 commits behind current `main`. fileciteturn123file0L2-L2

The branch tree contains:

- Android native C++ bridge;
- `CMakeLists.txt`;
- `native-lib.cpp`;
- Java Android UI (`MainActivity.java`);
- Rust `core/` crate;
- native build configuration;
- validation documentation.

The branch's `docs/ANDROID_V0.2.0_VALIDATED.md` provides explicit physical-device validation evidence and the ELF/SONAME fix described above. fileciteturn133file0L2-L2 fileciteturn134file0L2-L2

### Why this matters

This branch proves that the project previously achieved a **device-validated Android/Rust Gain path**. It therefore must not be described as merely a hypothetical prototype.

However, its age and divergence mean it is **not automatically the current canonical architecture**.

## Version nomenclature decision

The branch name `android-v0.2.0-validated` should be treated as historical milestone naming, not as proof that all present `main` code belongs to V0.2.0.

Future product versions should be represented by tags/releases such as `V0.x.x`; branch names should describe development lines (`feature/`, `experiment/`, `archive/`).

## Current branch inventory status

| Branch | Current classification | Immediate action |
|---|---|---|
| `core-rust` | Valuable historical Core/Gain | Keep; later cherry-pick evaluation |
| `android-v0.2.0-validated` | Historical physical-device validation reference | Keep; later extract validation/build lessons |
| `android-gain-realtime` | Needs deep compare | Audit next |
| `prod/full-eq-v1` | Historical Android/system-EQ generation | Audit next |
| `v0.2.1-stylo-eq-mvp` | Historical MVP line | Audit later |
| `v0.2.2-first-apk-mvp` | Historical APK line | Audit later |
| `prototype/android-apk` | Parent/reference for V0.2.0 validation | Preserve until genealogy complete |
| `prototype/android-apk-v2` | Historical prototype | Audit later |
| `ci/android-build-verification` | Superseded CI line | Preserve until final classification |
| checkpoint branches | Historical checkpoints | Preserve until genealogy complete |

## Conclusion

The second radiography has now established two distinct Android generations:

1. **Historical V0.2.0 validated Android/Rust Gain path**, physically tested and repaired for ELF packaging.
2. **Current `main` Kotlin/PCM/DSP path**, more recent and more feature-rich, but whose latest runtime status still needs explicit device validation.

These generations must be documented separately. The correct next task is not to merge them blindly, but to recover proven ideas/lessons from the historical generation while validating the current one.
