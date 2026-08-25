# STYLO DSP — AI START HERE

This file is the entry point for any new AI agent working on StyloDSP.

## Mission

Understand the real repository state before changing code. StyloDSP has evolved through multiple Android, DSP, UI, CI, Rust and prototype iterations, so historical documentation may describe an older state.

## Mandatory reading order

1. `AI_CONTEXT.md`
2. `docs/AI_MASTER_CONTEXT.md`
3. `harness/INSTRUCTIONS.md`
4. `harness/PROJECT_STATE.md`
5. `harness/STATE.md`
6. `harness/ARCHITECTURE.md`
7. `harness/ROADMAP.md`
8. `harness/DECISION_LOG.md`
9. `harness/audits/BRANCH-INVENTORY-2026-08-25.md`
10. Latest other audit under `harness/audits/`
11. `harness/VALIDATION_PROTOCOL.md`
12. Inspect the actual source tree and relevant Git history

## Before coding

Do not assume a feature is missing because an old document says so.
Do not assume a feature works because documentation says so.
Treat source code and validation evidence as higher-confidence than historical prose.
Distinguish historical validated milestones from the latest `main` runtime state.

If the repository appears inconsistent, stop feature development and execute:

`harness/RECONCILIATION_PROTOCOL.md`

## Current strategic position

The known current Android implementation contains a real PCM playback path with MediaExtractor → MediaCodec → PCM processing → EQ → FFT/metering → SafetyLimiter → AudioTrack. The Android build has produced a GitHub Actions release artifact.

There is also a **historical physical-device-validated V0.2.0 Android/Rust/Gain path** documented in `android-v0.2.0-validated`. That historical milestone must not be confused with the runtime status of the latest `main` build.

The Rust Core architecture exists as a direction/branch of development but is not currently established as the canonical DSP implementation on `main`.

The next priority is repository reconciliation and evidence-based validation, not another feature expansion.

## Golden rule

**Understand → compare → classify → document → validate → then modify.**
