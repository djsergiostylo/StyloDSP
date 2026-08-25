# STYLO DSP — PROJECT STATE

**State date:** 2026-08-25
**State type:** provisional, pending second deep reconciliation

## Canonical relationship
`STATE.md` is the concise operational snapshot. This file is the expanded project narrative. They must express the same verified state.

## Current position
StyloDSP is in repository reconciliation plus Android validation. The Android implementation is substantially beyond the original UI-only prototype, while the repository still contains historical branches and multiple documentation generations.

## Known implemented path on `main`

`MediaExtractor → MediaCodec → PCM → EQ → FFT/spectrum → SafetyLimiter → AudioTrack`

Known engine capabilities include:
- load audio URI;
- play/pause;
- seek;
- loop;
- bypass;
- volume;
- dynamic EQ-band updates;
- per-channel EQ processing;
- FFT/spectrum callback;
- safety limiting;
- playback state/duration reporting.

## Historical validated milestone

`android-v0.2.0-validated` contains documented **physical-device validation** of an earlier Android/Rust/Gain path using Oboe/AAudio plus native FFI. The validated APK was produced from `prototype/android-apk`. The milestone records an ELF dependency/SONAME problem, its correction, CI checks for runner paths, and `arm64-v8a` testing.

This is valid historical evidence, but it is **not evidence that the latest `main` APK uses the same architecture or is already runtime-validated**.

## Build evidence for current `main`

A GitHub Actions Android release artifact was produced from `main` at commit `890a97a1d05448ff0d858aa46bb84fda8b4a4853`.

Observed artifact:
`stylo-eq-release-890a97a1d05448ff0d858aa46bb84fda8b4a4853`

Approximate size: 2.23 MB.

## Runtime evidence

Not release-validated. Historical APK/runtime failures exist, but they belong to earlier milestones and must not automatically be attributed to the current `main` artifact. The latest artifact requires installation and current real-device testing before release readiness can be claimed.

Reference-device context includes Redmi Note 9 Pro-specific UI/layout work.

## Rust Core

Rust Core is a real historical engineering line, not merely an idea. `core-rust` contains a platform-independent Rust crate scaffold, an in-place `f32` Gain processor with unit tests, and a prototype README describing the initial PCM→Gain milestone. It is not canonical in `main` unless current source, build and runtime evidence prove integration.

## Repository condition

Historical/prototype branches and documentation generations remain under reconciliation. No branch should be deleted until its unique commits/files/functionality and validation evidence have been inventoried.

## Documentation reconciliation completed so far
- AI master context exists in `docs/AI_MASTER_CONTEXT.md`.
- AI documentation map exists in `docs/AI_DOCUMENTATION_MAP.md`.
- AI Project Memory entrypoint exists in `docs/ai-project-memory/README.md`.
- `docs/PROJECT_RECONCILIATION_PROTOCOL.md` is a pointer to the canonical Harness protocol.
- `docs/README.md` is an index.
- `docs/history/STYLO_DSP_EVOLUTION.md` is historical context.
- `harness/DECISION_LOG.md` is the durable decision source.
- Branch inventory exists at `harness/audits/BRANCH-INVENTORY-2026-08-25.md`.
- Missing legacy document names were recorded instead of fabricated.

## Current blockers
1. Full tree/file-size audit incomplete.
2. Cross-reference audit incomplete.
3. Remaining high-value historical branch inventory incomplete.
4. Latest APK/runtime evidence incomplete.
5. Full ChatGPT-history ↔ GitHub reconciliation incomplete.
6. Official V0.x.x historical version map not finalized.

## Current priority
1. Complete full tree + file-size audit.
2. Complete cross-reference audit.
3. Audit Harness protocols for stale references.
4. Compare remaining high-value branches with `main`.
5. Recover valuable historical work selectively.
6. Reconstruct latest APK/runtime status separately from historical V0.2.0 validation.
7. Reconcile historical AI context with repository evidence.
8. Finalize canonical documentation/version history.
9. Only then clean branches or change production architecture.

## Confidence
- High: current Android source/tree observations and recorded CI artifact.
- High: existence of the historical V0.2.0 physical-device validation record.
- Medium: historical architecture and decisions beyond directly documented evidence.
- Low/unknown: latest APK runtime behavior until device validation.
