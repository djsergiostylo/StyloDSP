# STYLO DSP — AI START HERE

This file is the entry point for any new AI agent working on StyloDSP.

## Mission

Understand the real repository state before changing code. StyloDSP has evolved through multiple Android, DSP, UI, CI, Rust and prototype iterations, so historical documentation may describe an older state.

## Mandatory reading order

1. `AI_CONTEXT.md`
2. `harness/INSTRUCTIONS.md`
3. `harness/PROJECT_STATE.md`
4. `harness/ARCHITECTURE.md`
5. `harness/ROADMAP.md`
6. `harness/DECISION_LOG.md`
7. Latest audit under `harness/audits/`
8. `harness/VALIDATION_PROTOCOL.md`
9. Inspect the actual source tree and relevant Git history

## Before coding

Do not assume a feature is missing because an old document says so.
Do not assume a feature works because documentation says so.
Treat source code and validation evidence as higher-confidence than historical prose.

If the repository appears inconsistent, stop feature development and execute:

`harness/RECONCILIATION_PROTOCOL.md`

## Current strategic position

The known current Android implementation contains a real PCM playback path with MediaExtractor → MediaCodec → PCM processing → EQ → FFT/metering → SafetyLimiter → AudioTrack. The Android build has produced a GitHub Actions release artifact.

The Rust Core architecture exists as a direction/branch of development but is not currently established as the canonical DSP implementation on `main`.

The next priority is repository reconciliation and evidence-based validation, not another feature expansion.

## Golden rule

**Understand → compare → document → validate → then modify.**
