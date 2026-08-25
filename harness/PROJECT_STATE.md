# STYLO DSP — PROJECT STATE

**State date:** 2026-08-25
**State type:** controlled reconciliation baseline

## Canonical relationship
`STATE.md` is the concise operational snapshot. This file is the expanded project narrative. They must express the same verified state. The source-of-truth rules are defined in `harness/DOCUMENT_CONSISTENCY_PROTOCOL.md`.

## Current position
StyloDSP is in repository reconciliation plus Android validation. The Android implementation is substantially beyond the original UI-only prototype, while the repository still contains historical branches and multiple documentation generations.

## Known implemented path on `main`
`MediaExtractor → MediaCodec → PCM → per-channel EqBank → SafetyLimiter → AudioTrack`

FFT/spectrum analysis is available from the decoded/processed PCM path. The player engine owns transport state such as play/pause, seek, loop, bypass and volume.

## Historical validated milestone
`android-v0.2.0-validated` contains documented physical-device validation of an earlier Android/Rust/Gain path using Oboe/AAudio plus native FFI. The validated APK was produced from `prototype/android-apk`. The milestone records an ELF dependency/SONAME problem, its correction, CI checks for runner paths, and arm64-v8a testing.

This is historical evidence only. It does not prove that current `main` uses the same architecture or is runtime-validated.

## Build evidence for current `main`
A GitHub Actions Android release artifact was produced from `main` at commit `890a97a1d05448ff0d858aa46bb84fda8b4a4853`.

Observed artifact: `stylo-eq-release-890a97a1d05448ff0d858aa46bb84fda8b4a4853`.

Approximate size: 2.23 MB.

## Runtime evidence
**Current `main`: not release-validated.** The latest artifact requires installation and current real-device testing before release readiness can be claimed.

Reference-device context includes Redmi Note 9 Pro-specific UI/layout work.

## Rust Core
Rust Core is a real historical engineering line, not merely an idea. `core-rust` contains a platform-independent Rust crate scaffold, an in-place `f32` Gain processor with unit tests, and a prototype README describing the initial PCM→Gain milestone. It is experimental/historical until integrated, built and runtime-validated in the canonical path.

## Repository condition
Historical/prototype branches and documentation generations remain under reconciliation. No branch should be deleted until its unique commits/files/functionality and validation evidence have been inventoried.

## Documentation state
Canonical documentation roles are defined by `harness/DOCUMENT_CONSISTENCY_PROTOCOL.md`. The master AI context, documentation map, project-memory entrypoint, branch inventory, Harness protocols and session handoff are present. Historical documents remain historical and are not allowed to override current evidence.

## Current blockers
1. Full tree/file-size audit incomplete.
2. Cross-reference audit incomplete.
3. Remaining high-value historical branch inventory incomplete.
4. Latest APK/runtime evidence incomplete.
5. Full accessible ChatGPT-history ↔ GitHub reconciliation incomplete.
6. Official V0.x.x historical version map not finalized.

## Current priority
1. Complete full tree + file-size audit.
2. Complete cross-reference audit.
3. Finish remaining branch inventory.
4. Recover valuable historical work selectively.
5. Reconstruct latest APK/runtime status separately from historical V0.2.0 validation.
6. Reconcile accessible AI history with repository evidence.
7. Finalize canonical documentation/version history.
8. Only then clean branches or change production architecture.

## Confidence
- High: current Android source/tree observations and recorded CI artifact.
- High: existence of the historical V0.2.0 physical-device validation record.
- Medium: historical architecture and decisions beyond directly documented evidence.
- Low/unknown: latest APK runtime behavior until current device validation.
