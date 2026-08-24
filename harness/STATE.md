# STYLO HARNESS · STATE

**Pilot date:** 2026-08-24
**Version:** v0.2-harness-pilot
**Pilot status:** PRODUCT GOAL DEFINED / H-002 IN_PROGRESS
**Active technical feature:** H-002 Repository audit and executable verification baseline

## Product north star
Android mobile audio APK with a realtime frequency spectrum and an interactive parametric/graphic EQ directly over the spectrum. The user must be able to see the spectrum move, see the EQ response curve, and manipulate EQ nodes by touch.

## Current truth
- Harness baseline is installed.
- Product goal has now been explicitly defined as a mobile spectrum + interactive EQ vertical slice.
- Web project is present and has Node/TypeScript/Vitest scripts.
- Repository architecture documents a Rust Core and Android/Web/VST3 adapters.
- The Android workflow expects `core/` and `android/`, while the current root tree inspected on `main` did not expose those directories.

## Verified
- Repository accessible.
- README and architecture documentation exist.
- Harness documents exist.
- Web package defines Node >=24 <25, npm >=11 <12, test and typecheck scripts.
- Android workflow configuration was inspected.
- Product north star and first vertical-slice acceptance criteria are now stored in the repository.

## Not yet verified
- Actual `npm test` result.
- Actual TypeScript typecheck result.
- Rust Core compilation.
- Android target compilation.
- APK generation.
- Runtime audio path.
- Realtime FFT performance.
- Realtime allocation safety.
- Touch interaction latency.
- Spectrum/EQ rendering performance.

## Current blockers / inconsistencies
1. Android workflow references `core/Cargo.toml` and Android paths that were not visible in the current main tree audit. Reconcile before declaring Android/Core PASSING.
2. Executable verification has not yet been run.

## Next technical action
Complete H-002. Then implement the smallest vertical slice in this order:
1. establish/repair mobile audio I/O path;
2. establish realtime FFT data path;
3. render spectrum;
4. implement one parametric EQ band;
5. render EQ curve over spectrum;
6. add touch node manipulation;
7. verify audio result, responsiveness and latency on a real Android device.
