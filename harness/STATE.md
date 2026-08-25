# STYLO HARNESS · STATE

**State date:** 2026-08-25
**Pilot status:** IN_PROGRESS
**Active feature:** H-002 Repository reconciliation and executable verification baseline

## Canonical status
`STATE.md` is the concise operational state. `PROJECT_STATE.md` contains the expanded narrative. If they disagree, reconcile them in the same change before proceeding.

## Current truth
- Repository: `djsergiostylo/StyloDSP`
- Canonical development branch: `main`
- Documentation reconciliation branch: `docs/ai-project-memory`
- The Android implementation in `main` contains a real PCM-oriented player/DSP path.
- Historical statements claiming `android/` is absent from `main` are obsolete.
- Historical/prototype branches and multiple documentation generations remain under audit.

## Verified repository evidence
- Android source exists in `main`.
- `PcmPlayerEngine.kt` implements MediaExtractor → MediaCodec → PCM → EQ → FFT → SafetyLimiter → AudioTrack.
- Android CI produced a release artifact from `main` at commit `890a97a1d05448ff0d858aa46bb84fda8b4a4853`.
- Redmi Note 9 Pro-specific UI/layout changes exist in the current Android work.
- Rust Core remains historical/experimental and is not canonical in `main`.

## Release verification status
Not release-validated. Build/artifact evidence exists, but latest APK runtime on the reference device is still pending. A previous APK test exposed a native-library packaging/runtime failure involving `libstylo_dsp_core.so`.

## Reconciliation status
- Documentation map created.
- Master AI context created and read back after commit.
- Persistence checklist created and committed on an audit branch.
- `docs/PROJECT_RECONCILIATION_PROTOCOL.md` confirmed as a pointer to the canonical Harness protocol.
- `docs/README.md` confirmed as an index.
- `docs/history/STYLO_DSP_EVOLUTION.md` confirmed as historical context.
- `harness/DECISION_LOG.md` confirmed as the durable decision log.
- Missing legacy document names (`docs/DECISIONS.md`, `docs/PRODUCTION_STATUS.md`, `docs/VERIFY.md`) are recorded as absent, not fabricated.

## Current blockers / inconsistencies
1. Full branch reconciliation is incomplete.
2. Full tree/file-size/cross-reference audit is incomplete.
3. `features.json` was previously inconsistent with H-002 history; it is now explicitly IN_PROGRESS and must remain evidence-driven.
4. `STATE.md` and `PROJECT_STATE.md` are intentionally parallel documents and must be kept synchronized.
5. Latest APK runtime and native-library packaging need device evidence.

## Next sequence
1. Complete full tree + file-size audit.
2. Complete cross-reference audit.
3. Audit all Harness protocols for stale paths.
4. Compare important branches against `main`.
5. Identify branch-only valuable code.
6. Reconstruct APK/runtime state.
7. Reconcile ChatGPT project history with repository evidence.
8. Update canonical documentation and roadmap.
9. Only then clean branches or modify production architecture.

## Rule
Do not mark a feature `PASSING` merely because it compiles or an old document says it works.
