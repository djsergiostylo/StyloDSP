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

## Build evidence
A GitHub Actions Android release artifact was produced from `main` at commit `890a97a1d05448ff0d858aa46bb84fda8b4a4853`.

Observed artifact:
`stylo-eq-release-890a97a1d05448ff0d858aa46bb84fda8b4a4853`

Approximate size: 2.23 MB.

## Runtime evidence
Not release-validated. A previous APK test exposed a native-library packaging/runtime issue involving `libstylo_dsp_core.so`. The latest artifact therefore requires installation and real-device testing before release readiness can be claimed.

Reference-device context includes Redmi Note 9 Pro-specific UI/layout work.

## Rust Core
Rust Core is a historical/experimental direction documented in architecture materials and branches. It is not canonical in `main` unless current source, build and runtime evidence prove integration.

## Repository condition
Historical/prototype branches and documentation generations remain under reconciliation. No branch should be deleted until its unique commits/files/functionality have been inventoried.

## Documentation reconciliation completed so far
- AI master context exists in `docs/AI_MASTER_CONTEXT.md`.
- AI documentation map exists in `docs/AI_DOCUMENTATION_MAP.md`.
- AI Project Memory entrypoint exists in `docs/ai-project-memory/README.md`.
- `docs/PROJECT_RECONCILIATION_PROTOCOL.md` is a pointer to the canonical Harness protocol.
- `docs/README.md` is an index.
- `docs/history/STYLO_DSP_EVOLUTION.md` is historical context.
- `harness/DECISION_LOG.md` is the durable decision source.
- Missing legacy document names were recorded instead of fabricated.

## Current blockers
1. Full tree/file-size audit incomplete.
2. Cross-reference audit incomplete.
3. High-value historical branch inventory incomplete.
4. APK/runtime evidence incomplete.
5. Full ChatGPT-history ↔ GitHub reconciliation incomplete.
6. Official V0.x.x historical version map not finalized.

## Current priority
1. Complete full tree + file-size audit.
2. Complete cross-reference audit.
3. Audit Harness protocols for stale references.
4. Compare high-value branches with `main`.
5. Recover valuable historical work selectively.
6. Reconstruct APK/runtime status.
7. Reconcile historical AI context with repository evidence.
8. Finalize canonical documentation/version history.
9. Only then clean branches or change production architecture.

## Confidence
- High: current Android source/tree observations and recorded CI artifact.
- Medium: historical architecture and decisions.
- Low/unknown: latest APK runtime behavior until device validation.
