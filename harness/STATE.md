# STYLO HARNESS · STATE

**Pilot date:** 2026-08-24
**Pilot status:** IN_PROGRESS
**Active feature:** H-002 Repository audit and executable verification baseline

## Current truth
- Repository: `djsergiostylo/StyloDSP`
- Default branch: `main`
- Harness baseline is installed.
- Web project is present and has Node/TypeScript/Vitest scripts.
- Repository architecture documents a Rust Core and Android/Web/VST3 adapters.
- The Android GitHub Actions workflow expects `core/` and `android/`, but the current root tree inspected on `main` does not expose those directories.

## Verified
- Repository accessible.
- `README.md` and `docs/ARCHITECTURE.md` exist.
- Harness documents exist.
- `web/package.json` defines Node >=24 <25, npm >=11 <12, `test` and `typecheck` scripts.
- `web/README.md` documents the current Web pipeline and next Web testing milestone.
- Android workflow configuration was inspected.

## Not yet verified
- Actual `npm test` result.
- Actual TypeScript typecheck result.
- Rust Core compilation.
- Android target compilation.
- APK generation.
- Runtime audio path.
- Realtime allocation safety through executable tests.
- Cross-platform parity.

## Current blockers / inconsistencies
1. The Android workflow references `core/Cargo.toml` and Android paths that are not visible in the current root tree. This must be reconciled before Android/Core can be PASSING.
2. This audit has not executed the project commands, so executable verification remains open.

## Next feature
`H-002` remains IN_PROGRESS. Next: enumerate Web tests, inspect workflow history and reconcile the Rust/Android tree before declaring any technical gate PASSING.
