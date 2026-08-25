# STYLO HARNESS · STATE

**State date:** 2026-08-25
**Pilot status:** IN_PROGRESS
**Active feature:** H-002 Repository reconciliation and executable verification baseline

## Current truth
- Repository: `djsergiostylo/StyloDSP`
- Canonical development branch remains `main`.
- Documentation reconciliation is being staged on `docs/ai-project-memory`.
- The Android implementation in `main` contains a real PCM-oriented player/DSP path.
- Historical Harness statements claiming that `android/` is absent from `main` are obsolete and must not be used as current truth.
- The repository contains historical/prototype branches and multiple documentation generations.

## Verified from repository evidence
- Android source exists in `main`.
- `PcmPlayerEngine.kt` implements MediaExtractor → MediaCodec → PCM → EQ → FFT → SafetyLimiter → AudioTrack.
- Android CI has produced a release artifact from `main` at commit `890a97a1d05448ff0d858aa46bb84fda8b4a4853`.
- The reference-device UI has received Redmi Note 9 Pro-specific layout changes.
- The Rust Core is present as historical/experimental direction, not as the canonical implementation in `main`.

## Not yet verified to release standard
- Full real-device runtime validation of the latest APK.
- Complete branch reconciliation.
- Complete ChatGPT-history ↔ GitHub reconciliation.
- Final canonical documentation state.
- Cross-platform parity.
- Rust Core build and integration.
- Realtime allocation-safety proof.

## Current blockers / inconsistencies
1. Historical documentation and older Harness state do not fully match current `main`.
2. The previous APK testing record includes a native-library packaging/runtime failure involving `libstylo_dsp_core.so`; the latest successful CI artifact still requires device validation.
3. Several branches may contain unique work that has not yet been classified.

## Next sequence
1. Audit `main` completely.
2. Compare important branches against `main`.
3. Identify valuable branch-only code.
4. Classify obsolete documentation.
5. Reconstruct real APK/runtime state.
6. Reconcile AI conversation knowledge with repository evidence.
7. Update canonical Harness/README/project state.
8. Establish one roadmap and validation matrix.
9. Only then clean branches or modify production architecture.

## Rule
Do not mark a feature `PASSING` merely because it compiles or an old document says it works.
