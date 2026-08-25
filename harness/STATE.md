# STYLO HARNESS · STATE

**State date:** 2026-08-25
**Pilot status:** IN_PROGRESS
**Active feature:** H-002 Repository reconciliation and executable verification baseline

## Canonical status
`STATE.md` is the concise operational state. `PROJECT_STATE.md` contains the expanded narrative. If they disagree, reconcile them in the same change before proceeding.

## Current truth
- Repository: `djsergiostylo/StyloDSP`
- Canonical development branch: `main`
- Documentation/reconciliation branch: `docs/ai-project-memory`
- Current `main` contains a real PCM-oriented Android player/DSP path.
- Historical statements claiming `android/` is absent from `main` are obsolete.
- Historical/prototype branches and multiple documentation generations remain under audit.

## Verified repository evidence
- Android source exists in `main`.
- `PcmPlayerEngine.kt` implements MediaExtractor → MediaCodec → PCM → EQ → FFT → SafetyLimiter → AudioTrack.
- Android CI produced a release artifact from `main` at commit `890a97a1d05448ff0d858aa46bb84fda8b4a4853`.
- Redmi Note 9 Pro-specific UI/layout changes exist in the current Android work.
- Rust Core exists in historical branches and is not yet canonical in current `main`.
- Historical V0.2.0 Android/Rust/Gain validation is real device evidence, but it belongs to that historical generation and must not be presented as validation of current `main`.

## Release verification status
**Current `main`: NOT release-validated.** Build/artifact evidence exists, but latest APK runtime on the reference device is still pending.

**Historical V0.2.0:** physically validated Android/Rust/Gain milestone exists in `android-v0.2.0-validated`, including a documented ELF SONAME/DT_NEEDED packaging correction. This is historical evidence, not current-main validation.

## Reconciliation status
- Documentation map created.
- Master AI context created and read back after commit.
- Persistence checklist created and committed on an audit branch.
- `docs/PROJECT_RECONCILIATION_PROTOCOL.md` confirmed as a pointer to the canonical Harness protocol.
- `docs/README.md` confirmed as an index.
- `docs/history/STYLO_DSP_EVOLUTION.md` confirmed as historical context.
- `harness/DECISION_LOG.md` confirmed as the durable decision log.
- Missing legacy document names (`docs/DECISIONS.md`, `docs/PRODUCTION_STATUS.md`, `docs/VERIFY.md`) are recorded as absent, not fabricated.
- High-value historical branches inventoried: `core-rust`, `android-v0.2.0-validated`, `android-gain-realtime`, `prod/full-eq-v1`.

## Current blockers / inconsistencies
1. Full branch reconciliation is incomplete.
2. Full tree/file-size/cross-reference audit is incomplete.
3. `features.json` is evidence-driven and keeps H-002 `IN_PROGRESS`.
4. `STATE.md` and `PROJECT_STATE.md` are intentionally parallel documents and must be kept synchronized.
5. Current `main` APK/runtime needs device evidence.
6. Remaining historical branches still need classification before cleanup.

## Next sequence
1. Complete full tree + file-size audit.
2. Complete cross-reference audit.
3. Audit all Harness protocols for stale paths.
4. Compare remaining important branches against `main`.
5. Identify branch-only valuable code and decisions.
6. Reconstruct current APK/runtime state.
7. Reconcile available ChatGPT project history with repository evidence.
8. Finalize official V0.x.x genealogy.
9. Update canonical documentation and roadmap.
10. Only then clean branches or modify production architecture.

## Rule
Do not mark a feature `PASSING` merely because it compiles or an old document says it works.
