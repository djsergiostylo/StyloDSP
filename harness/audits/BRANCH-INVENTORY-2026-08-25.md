# STYLO DSP — BRANCH INVENTORY / SECOND RADIOGRAPHY

**Date:** 2026-08-25  
**Scope:** historical branch classification with focused inspection of Android/Rust/EQ/Harness lines.  
**Authority:** audit evidence only. This document does not override canonical STATE, ARCHITECTURE, ROADMAP or VALIDATION_PROTOCOL.

## Critical generation distinction

The repository contains a historical, physically device-validated Android/Rust milestone that must not be confused with the current `main` runtime state.

`android-v0.2.0-validated` documents physical-device validation of an APK produced from `prototype/android-apk`. Its architecture was:

`Android → Oboe/AAudio → Native bridge/FFI → STYLO DSP Core (Rust) → Gain (f32)`.

The historical validation records an `UnsatisfiedLinkError` caused by an absolute CI-runner ELF dependency and a subsequent SONAME/DT_NEEDED correction. It records build commit `85f4bdbf6030cbc3d83903874c0e01e033bb80fb`, Actions run `31965884079`, and `arm64-v8a`. This is V0.2.0 historical validation evidence, not proof that the latest `main` APK has the same runtime path or is already validated.

## High-value technical/product branches already inspected

### `core-rust`

**Disposition:** `KEEP / RECOVER SELECTIVELY`  
**Comparison:** 3 commits ahead of shared merge base, 68 behind current `main`.

Unique work:
- Rust DSP Core scaffold (`core/Cargo.toml`, `rlib` + `cdylib`).
- In-place `f32` Gain processor with unit tests.
- Prototype README defining PCM → Core → Gain → PCM and pending platform adapters.

**Action:** preserve; later evaluate selective recovery after current-main validation and API-boundary review.

### `android-v0.2.0-validated`

**Disposition:** `KEEP AS HISTORICAL VALIDATION REFERENCE / RECOVER SELECTIVELY`  
**Comparison:** 24 commits ahead of shared merge base, 68 behind current `main`.

Contains Android native bridge, CMake, Java UI, Rust Core and explicit physical-device validation evidence including the ELF/SONAME correction.

**Action:** preserve as historical reference; extract proven build/runtime lessons and compare unique native/Rust code before recovery.

### `android-gain-realtime`

**Disposition:** `KEEP / INVESTIGATE DEEPLY`  
**Comparison:** 33 commits ahead of shared merge base, 68 behind current `main`.

Contains a substantial Android/Rust/native slice, dedicated workflow, native bridge, Rust Core, validation documentation, dependency audit and realtime gain gate.

**Action:** high-priority source for recovering integration lessons. Do not merge blindly.

### `prod/full-eq-v1`

**Disposition:** `ARCHIVE / EXTRACT SELECTIVELY`  
**Comparison:** 16 commits ahead of shared merge base, 49 behind current `main`.

Contains older `AudioEqProcessor.kt`, `EqModel.kt`, `FastFft.kt` and a different `MainActivity.kt` generation.

**Action:** preserve historical EQ/UI/FFT knowledge; do not mix directly into the current PCM path.

### `v0.2.1-stylo-eq-mvp`

**Disposition:** `KEEP / AUDIT AS PRODUCT-HISTORY SOURCE`  
**Comparison:** 19 commits ahead of shared merge base, 57 behind current `main`.

Represents a product-definition/MVP generation with spectrum EQ vertical-slice intent, product vision, UI specification, production planning and earlier Harness/version snapshots.

**Action:** preserve product/spec decisions as history; recover implementation only after source-level comparison.

### `v0.2.2-first-apk-mvp`

**Disposition:** `KEEP / AUDIT AS APK-HISTORY SOURCE`  
**Comparison:** 28 commits ahead of shared merge base, 57 behind current `main`.

Contains earlier Android APK application, native CMake bridge, `native-lib.cpp`, early Rust Core and product/Harness material.

**Action:** preserve APK/build/native evidence separately; compare with `core-rust`, `android-gain-realtime` and current Android before recovery.

## Additional branches now compared

### `prototype/android-apk`

**Disposition:** `KEEP AS HISTORICAL PARENT / VALIDATION SOURCE`  
**Comparison:** 23 commits ahead, 68 behind current `main`.

Unique tree includes Android Gradle/manifest, CMake/native bridge, Java `MainActivity`, Android workflow, Rust Core and Cargo configuration. This is the parent/reference line associated with the historical V0.2.0 validated APK.

**Action:** preserve. Do not duplicate its implementation into `main`; use it as historical source for the validated native/Rust path.

### `prototype/android-apk-v2`

**Disposition:** `KEEP / COMPARE WITH V0.2.0`  
**Comparison:** 21 commits ahead, 68 behind current `main`.

Unique tree includes Android Gradle/manifest, CMake/native bridge, Kotlin `MainActivity`, workflow and an early Rust Core with README.

**Action:** preserve while comparing differences against `prototype/android-apk` and `android-v0.2.0-validated`. No automatic merge.

### `ci/android-build-verification`

**Disposition:** `ARCHIVE AFTER CI AUDIT`  
**Comparison:** 2 commits ahead, 49 behind current `main`.

Unique changes are limited to an Android workflow adjustment and `harness/CI_BUILD_CHECK.md`.

**Action:** inspect whether the CI check remains useful; otherwise archive after evidence is recorded.

### `pre-final-build-2026-08-24`

**Disposition:** `ARCHIVE / CHECKPOINT`  
**Comparison:** 0 unique commits against current `main`; it is 19 commits behind.

**Action:** preserve as a historical pointer/checkpoint; no code recovery indicated by the comparison.

### `v0.2-harness-pilot`

**Disposition:** `ARCHIVE AS HARNESS HISTORY / EXTRACT DECISIONS`  
**Comparison:** 9 commits ahead, 57 behind current `main`.

Its unique work is primarily early AI/Harness/product documentation: `docs/AI_CONTEXT.md`, product vision/UI spec/reference patterns, early Harness context/state/roadmap/features.

**Action:** preserve decisions as historical evidence. Do not restore duplicate Harness documents as competing canonical sources.

## Branches not yet source-compared in this pass

These remain explicitly **PENDING**, not classified by guesswork:

| Branch | Current disposition | Next action |
|---|---|---|
| `v0.2-harness-pilot-checkpoint` | PENDING | Compare against `v0.2-harness-pilot` |
| `v0.2-harness-pilot-rollback` | PENDING | Compare against pilot/checkpoint |
| `audit/second-radiography` | PENDING / audit history | Inspect only for unique evidence before archive |
| `audit/persistence-check-2026-08-25` | PENDING / audit history | Compare with newer persistence checkpoint |
| `audit/persistence-check-2026-08-25b` | PENDING / audit history | Compare with prior persistence checkpoint |
| `audit/branch-inventory-2026-08-25` | PENDING / audit workspace | Preserve until this audit is finalized |

No branch in this section should be deleted or renamed yet.

## Complete branch inventory

Current repository branch list at the time of this audit:

1. `android-gain-realtime`
2. `android-v0.2.0-validated`
3. `audit/branch-inventory-2026-08-25`
4. `audit/persistence-check-2026-08-25b`
5. `audit/persistence-check-2026-08-25`
6. `audit/second-radiography`
7. `checkpoint/pre-harness-next-step-2026-08-24`
8. `ci/android-build-verification`
9. `core-rust`
10. `docs/ai-project-memory`
11. `main`
12. `pre-final-build-2026-08-24`
13. `prod/full-eq-v1`
14. `prototype/android-apk`
15. `prototype/android-apk-v2`
16. `v0.2-harness-pilot`
17. `v0.2-harness-pilot-checkpoint`
18. `v0.2-harness-pilot-rollback`
19. `v0.2.1-stylo-eq-mvp`
20. `v0.2.2-first-apk-mvp`

## Version nomenclature decision

A branch name containing `v0.2.x` represents a historical development line unless a tag/release explicitly declares an official product version.

Future official product releases should use tags/releases such as `V0.x.x`. Development branches should use `feature/`, `experiment/` or `archive/` semantics.

## Current conclusion

The second radiography now establishes four major generations plus a Harness-history layer:

1. **Historical V0.2.0 Android/Rust/Gain:** physically validated, with native ELF packaging fix.
2. **Historical mobile spectrum EQ MVP/APK:** V0.2.1 and V0.2.2 product/APK evolution.
3. **Historical full-EQ Android:** older EQ/FFT/UI generation.
4. **Current `main` Kotlin/PCM/DSP:** newer active path, latest runtime still pending.
5. **Harness-history branches:** valuable for decisions but must not override the current canonical Harness.

The goal is not to merge generations wholesale. It is to recover proven components, decisions and lessons while maintaining one canonical current path.
