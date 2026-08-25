# STYLO DSP — AI CONTEXT

## Purpose

This is the canonical orientation document for AI agents entering the repository. It is intentionally concise and points to the operational documents that contain the detailed state, architecture, decisions, validation and roadmap.

## What StyloDSP is

StyloDSP is one audio DSP project evolving through multiple generations, not a collection of unrelated applications. Its history includes Android EQ/player implementations, native/Rust experiments, Web/TypeScript DSP reference code, CI/APK checkpoints and Harness Engineering.

## Current known state

- `main` contains the active Android implementation.
- The current Android engine implements a MediaExtractor → MediaCodec → PCM → EQ → FFT → SafetyLimiter → AudioTrack path.
- EQ bands can be updated dynamically; bypass, volume, loop, seek and playback state are implemented in the current player engine.
- GitHub Actions has produced an Android release artifact from `main`.
- The repository has accumulated multiple historical/prototype branches.
- A Rust Core architecture exists in project history/branches, but the Rust core is not currently the canonical implementation in `main`.
- **Historical validation correction:** `android-v0.2.0-validated` documents a real physical-device Android/Rust/Gain validation milestone using Oboe/AAudio + native FFI. It also documents an ELF/SONAME dependency problem that was fixed. This is historical V0.2.0 evidence, not proof that the latest `main` APK is runtime-validated.
- The latest `main` APK/runtime still requires explicit device validation.

## Sources of truth

Priority order:

1. Actual source code on the current target branch
2. Reproducible validation evidence and test results
3. Current `harness/PROJECT_STATE.md`
4. Current architecture and decision records
5. Current roadmap
6. Historical commits/branches and historical documentation
7. Old AI conversations

## Important historical evidence

Read `harness/audits/BRANCH-INVENTORY-2026-08-25.md` before making assumptions about Rust or the Android V0.2.0 line.

## Operational documents

- `AI_START_HERE.md` — entrypoint
- `docs/AI_MASTER_CONTEXT.md` — master project DNA/context
- `harness/INSTRUCTIONS.md` — rules for AI work
- `harness/PROJECT_STATE.md` — current known state
- `harness/STATE.md` — concise operational state
- `harness/ARCHITECTURE.md` — architecture actually present vs planned
- `harness/ROADMAP.md` — single forward plan
- `harness/DECISION_LOG.md` — durable project decisions
- `harness/RECONCILIATION_PROTOCOL.md` — periodic cleanup/reconciliation procedure
- `harness/AUDIT_PROTOCOL.md` — evidence-based audit procedure
- `harness/VALIDATION_PROTOCOL.md` — functional validation procedure
- `harness/SESSION_HANDOFF.md` — continuation context between AI sessions
- `harness/audits/` — dated audit records

## Mandatory behavior for new AI agents

Before coding, read the operational documents above and inspect the real repository. If state is inconsistent, perform reconciliation first. Never recreate existing functionality merely because an old document is incomplete. Never merge historical Rust/Android lines blindly; compare them against `main` and record what is actually recoverable.

## Current immediate priority

Complete the second deep repository radiography/reconciliation, beginning with branch inventory and historical recovery. Only after evidence is recorded should branches be cleaned, versions renamed or production architecture changed.
