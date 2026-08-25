# STYLO HARNESS · STATE

**State date:** 2026-08-25
**Pilot status:** IN_PROGRESS
**Active feature:** EQ-031 / PLAYER-001 current Android APK implementation and device verification

## Canonical status
`STATE.md` is the concise operational state. `PROJECT_STATE.md` contains the expanded narrative. Source-of-truth and synchronization rules are defined in `harness/DOCUMENT_CONSISTENCY_PROTOCOL.md`.

## Current truth
- Repository: `djsergiostylo/StyloDSP`
- Canonical development branch: `main`
- Documentation/reconciliation branch: `docs/ai-project-memory`
- Current `main` contains a real PCM-oriented Android player/DSP path.
- Historical/prototype branches have been classified; none should be merged wholesale.
- The repository reconciliation baseline is now sufficient to resume product engineering.

## Verified repository evidence
- Android source exists in `main`.
- `PcmPlayerEngine.kt` implements MediaExtractor → MediaCodec → PCM → EQ → FFT → SafetyLimiter → AudioTrack.
- `DspEngine.kt` contains radix-2 FFT, EqBand/Biquad/EqBank and SafetyLimiter.
- `MainActivity.kt` exposes file selection, transport, seek, loop, volume, 31-band/8-band modes, bypass, A/B and presets.
- Android CI builds debug APK, release APK and release AAB and runs the declared unit-test task.
- `android/app/build.gradle.kts` identifies the current product as versionName `0.3.0` / versionCode `3`.
- Historical V0.2.0 Android/Rust/Gain validation is real device evidence, but it belongs to that historical generation and must not be presented as validation of current `main`.

## Release verification status
**Current `main`: NOT release-validated.** Build/workflow evidence exists, but current APK runtime on the reference device is still pending.

**Historical V0.2.0:** physically validated Android/Rust/Gain milestone exists in `android-v0.2.0-validated`, including the documented ELF SONAME/DT_NEEDED packaging correction. This is historical evidence only.

## Reconciliation status
- Canonical document hierarchy established.
- Document consistency/source-of-truth protocol established.
- Harness instructions, STATE, PROJECT_STATE, roadmap, decisions, validation and audit protocols synchronized.
- Historical branch inventory completed for all 20 listed branches.
- V0.2.0, V0.2.1, V0.2.2, Rust, Full EQ, Android prototypes and Harness/audit branches classified.
- Full cross-reference pass completed for the current product/Harness paths inspected during reconciliation.
- Current Android baseline frozen in `harness/audits/CURRENT-ANDROID-BASELINE-2026-08-25.md` on the reconciliation branch.
- `main` feature tracking corrected so H-002 is `IN_PROGRESS`, not `PASSING`, until current-main runtime evidence exists.

## Current blockers
1. Current APK must be built from current `main` and installed on the reference Android device.
2. Audio playback and transport must be verified.
3. 31-band audible EQ must be verified.
4. 8-band parametric frequency/gain/Q/type behavior must be verified.
5. Spectrum performance and correctness must be verified.
6. Preset persistence across restart must be verified.
7. Only evidence-backed defects should be fixed before release gating.

## Product execution sequence
1. Clean build and unit-test current `main`.
2. Produce debug APK.
3. Install on reference Android device.
4. Verify file open → decode → PCM → playback/seek/loop/volume.
5. Verify 31-band EQ, bypass, flat/reset and audible gain.
6. Verify 8-band parametric EQ, frequency/gain/Q/filter types.
7. Verify FFT spectrum and UI smoothness.
8. Verify presets across restart.
9. Fix defects found by those gates.
10. Rebuild and repeat gates.
11. Record evidence and only then advance version/release status.

## Rule
Do not mark a feature `PASSING` merely because it compiles or an old document says it works.
