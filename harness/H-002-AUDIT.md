# H-002 · Repository audit and executable verification baseline

**Date:** 2026-08-24
**Branch:** `v0.2.1-stylo-eq-mvp`
**Status:** IN_PROGRESS

## Findings

### 1. Web foundation exists
The Web project contains `package.json`, TypeScript configuration, Vitest configuration, source directories for analysis, audio-engine, DSP, export and validation, plus existing tests.

### 2. Reusable DSP/analysis assets exist
The current Web source contains `EQModule.ts`, `GainModule.ts`, `AudioAnalyzer.ts`, `AudioEngine.ts`, `ParameterController.ts` and related tests. These are candidates for reference/parity work, not automatic proof of Android readiness.

### 3. Android/Rust path is unresolved
The repository's Android workflow references `core/Cargo.toml` and Android build paths, but the current branch tree inspected through GitHub exposes `.github`, `docs`, `harness` and `web` at the root and does not expose `core/` or `android/`.

A repository search for `Cargo.toml` currently returns the Android workflow reference rather than an actual Core manifest. Therefore Android/Core readiness remains BLOCKED until the missing source is located in another ref/commit or intentionally reconstructed.

## Consequence
Do NOT start by writing a large Android UI or pretending the Rust Core exists. First establish the actual mobile build path.

## Next actions
1. Identify whether `core/` and `android/` exist in another branch/tag/commit accessible from the repository history.
2. If found, recover them into the MVP branch with a documented provenance.
3. If not found, create a minimal Android/Rust foundation only after documenting the reconstruction decision.
4. Run the Web test and typecheck commands in an executable CI environment.
5. Use the existing Web `AudioAnalyzer` and `EQModule` as behavioral references for parity.
6. Build the first mobile vertical slice: audio I/O -> FFT -> spectrum -> one EQ band -> EQ curve -> touch.

## Evidence
- Web tree: `web/src/analysis`, `web/src/audio-engine`, `web/src/dsp`, `web/src/export`, `web/src/validation`.
- DSP tree includes `EQModule.ts` and tests for other DSP modules.
- Android workflow exists at `.github/workflows/android-apk.yml` and references the unresolved Core/Android paths.

## Verification rule
A feature becomes `PASSING` only after executable evidence exists. Structural inspection alone is never sufficient.
