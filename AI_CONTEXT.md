# STYLO DSP — AI CONTEXT

## Purpose

This is the canonical orientation document for AI agents entering the repository. It is intentionally concise and points to the operational documents that contain the detailed state, architecture, decisions, validation and roadmap.

## What StyloDSP is

StyloDSP is an audio DSP project evolving from an Android equalizer/player MVP toward a reusable, cross-platform DSP architecture. The project includes real-time audio playback, software EQ processing, FFT/spectrum analysis, UI work, Android packaging/CI, and a planned Rust DSP core for broader reuse.

## Current known state

- `main` contains the active Android implementation.
- The Android engine currently implements a MediaExtractor → MediaCodec → PCM → EQ → FFT → SafetyLimiter → AudioTrack path.
- EQ bands can be updated dynamically; bypass, volume, loop, seek and playback state are implemented in the current player engine.
- GitHub Actions has produced an Android release artifact from `main`.
- The repository has accumulated multiple historical/prototype branches.
- A Rust Core architecture exists in project history/branches, but the Rust core is not currently the canonical implementation in `main`.
- Previous APK testing exposed a native-library packaging/runtime issue. Therefore build success must not be treated as proof of runtime success.

## Sources of truth

Priority order:

1. Actual source code on the current target branch
2. Reproducible validation evidence and test results
3. Current `harness/PROJECT_STATE.md`
4. Current architecture and decision records
5. Current roadmap
6. Historical documentation and old conversations

## Operational documents

- `AI_START_HERE.md` — entrypoint
- `harness/INSTRUCTIONS.md` — rules for AI work
- `harness/PROJECT_STATE.md` — current known state
- `harness/ARCHITECTURE.md` — architecture actually present vs planned
- `harness/ROADMAP.md` — single forward plan
- `harness/DECISION_LOG.md` — important decisions recovered from project history
- `harness/RECONCILIATION_PROTOCOL.md` — periodic cleanup/reconciliation procedure
- `harness/AUDIT_PROTOCOL.md` — evidence-based audit procedure
- `harness/VALIDATION_PROTOCOL.md` — functional validation procedure
- `harness/SESSION_HANDOFF.md` — continuation context between AI sessions
- `harness/audits/` — dated audit records

## Mandatory behavior for new AI agents

Before coding, read the operational documents above and inspect the real repository. If state is inconsistent, perform reconciliation first. Never recreate existing functionality merely because an old document is incomplete.

## Current immediate priority

Complete the second deep repository radiography/reconciliation, then update the canonical Harness state from its findings. Only after that should code, branches or architecture be cleaned up.
