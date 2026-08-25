# STYLO HARNESS · SESSION HANDOFF

## Session
2026-08-25 · AI project memory and reconciliation setup

## Completed
- Performed first repository/state radiography of `djsergiostylo/StyloDSP`.
- Identified drift between current Android source, historical documentation, branches, CI evidence and Harness state.
- Added `AI_START_HERE.md` and `AI_CONTEXT.md` as the entry point for new AI agents.
- Added canonical AI-working, project-state, architecture, roadmap, decision, reconciliation, audit and validation documents under `harness/`.
- Added a dated reconciliation baseline under `harness/audits/`.
- Created branch `docs/ai-project-memory` to stage the documentation changes without modifying `main`.

## Current truth
- Current Android source is substantially beyond the original UI-only prototype.
- The known PCM path is `MediaExtractor → MediaCodec → PCM → EQ → FFT/spectrum → SafetyLimiter → AudioTrack`.
- GitHub Actions has produced an Android release artifact from `main`.
- Runtime validation is still incomplete.
- A previous APK test exposed a native-library packaging/runtime issue, so build success must not be treated as runtime success.
- Rust Core remains a future/experimental architecture rather than the canonical DSP implementation in `main`.
- Multiple historical/prototype branches still require reconciliation before cleanup.

## Active phase
**H-002 / second deep repository radiography and reconciliation**

## Next action
1. Audit `main` completely.
2. Compare important branches against `main`.
3. Identify branch-only valuable work.
4. Classify obsolete/contradictory documentation.
5. Reconstruct the real APK/runtime state.
6. Reconcile ChatGPT-derived decisions with repository evidence.
7. Update canonical Harness state.
8. Only then clean branches or change production architecture.

## Important constraints
- Do not delete branches or valuable code before identifying their contents and value.
- Do not claim runtime readiness without runtime evidence.
- Do not treat historical documentation as current truth.
- Do not begin broad architecture refactoring before reconciliation is complete.
- Keep the repository as the persistent source of project memory.
