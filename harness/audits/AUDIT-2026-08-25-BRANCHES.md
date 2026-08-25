# STYLO DSP — BRANCH RECONCILIATION 2026-08-25

## Scope
Second-radiography branch pass. No source branch has been merged or deleted as a result of this audit.

## Branch inventory

Current branches identified:

- `main`
- `docs/ai-project-memory`
- `audit/second-radiography`
- `core-rust`
- `android-gain-realtime`
- `android-v0.2.0-validated`
- `v0.2.1-stylo-eq-mvp`
- `v0.2.2-first-apk-mvp`
- `prod/full-eq-v1`
- `pre-final-build-2026-08-24`
- `ci/android-build-verification`
- `prototype/android-apk`
- `prototype/android-apk-v2`
- `checkpoint/pre-harness-next-step-2026-08-24`
- `v0.2-harness-pilot`
- `v0.2-harness-pilot-checkpoint`
- `v0.2-harness-pilot-rollback`

## Confirmed findings

### `core-rust`

Classification: **KEEP / RECOVER SELECTIVELY**

The branch diverges from current `main` and contains a Rust DSP core history. Its history includes commits such as `core: scaffold Rust DSP core` and `core: add realtime-safe gain processor`.

The Rust implementation inspected on the historical branch contains a small FFI-safe `StyloGain` processor with tests. It is useful historical implementation evidence, but it is not evidence that a complete Rust DSP core is currently integrated into `main`.

Action: preserve the branch and inspect its complete unique file/commit inventory before any cherry-pick.

### `android-v0.2.0-validated`

Classification: **ARCHIVE / RECOVER SPECIFIC CORE MATERIAL IF NEEDED**

The branch contains `.cargo`, `core`, `android`, `docs` and `web`. Its `core/src/lib.rs` contains the same small FFI-safe gain processor lineage. Its Android tree is an older Java-based implementation and is not a candidate to replace the current Android player wholesale.

Action: preserve historical evidence; do not merge the old Android application wholesale.

### `android-gain-realtime`

Classification: **RECOVER SELECTIVELY / ARCHIVE**

The branch history contains realtime gain UI work and audited DSP dependency/integration work. It predates current `main` and diverges substantially. The useful value is likely historical DSP/dependency decisions rather than direct application replacement.

Action: inspect unique files/commits before deciding whether any specific change belongs in current architecture.

### `prod/full-eq-v1`

Classification: **HISTORICAL / ARCHIVE**

Current comparison shows 16 unique commits and 5 changed files relative to `main`, including an older `AudioEqProcessor.kt`, `EqModel.kt`, `FastFft.kt`, `MainActivity.kt` and an older Android release workflow.

The `AudioEqProcessor` implementation uses Android platform `DynamicsProcessing`/`Equalizer`, which is materially different from the current PCM software-DSP direction. The current `main` workflow is also more advanced, building debug APK, release APK and AAB plus unit tests.

Action: do not merge wholesale. Preserve as historical implementation evidence and inspect only for missing ideas/tests.

### `ci/android-build-verification`

Classification: **OBSOLETE AS IMPLEMENTATION**

The branch contains an older Android CI workflow that builds a debug APK. Current `main` has a more capable workflow that compiles/tests and builds debug APK, release APK and AAB.

Action: retain history; no cherry-pick expected.

### `pre-final-build-2026-08-24`

Classification: **REFERENCE / ARCHIVE**

Comparison indicates it is an ancestor/older state relative to current `main`, with no unique commits ahead of `main` in the current comparison. Its main value is historical provenance for the pre-final-build state.

### `v0.2.1-stylo-eq-mvp`

Classification: **HISTORICAL / INSPECT SELECTIVELY**

The branch contains a substantial earlier product-vertical-slice history. Its merge base predates the current Android implementation. The branch should not be merged wholesale without file-level inspection because current `main` has evolved considerably.

### Other prototype/checkpoint/harness branches

Classification: **ARCHIVE PENDING FINAL INVENTORY**

These branches are retained until their unique commits/files have been catalogued. No deletion is justified during this audit pass.

## Important architecture conclusion

The branch scan confirms that the repository has had multiple architectural generations:

1. Android platform/system EQ generation
2. Android software DSP / PCM generation
3. Rust-core experimentation
4. Current Android PCM/DSP generation
5. Cross-platform Rust/WASM/VST3 planning

These generations must not be conflated.

## Immediate next branch-audit task

Perform a complete unique-file/unique-commit inventory for the highest-value branches:

1. `core-rust`
2. `android-gain-realtime`
3. `android-v0.2.0-validated`
4. `v0.2.1-stylo-eq-mvp`
5. `v0.2.2-first-apk-mvp`
6. `prod/full-eq-v1`
7. `prototype/android-apk-v2`

Only after that inventory should any cherry-pick, merge, archive or deletion decision be made.

## Safety rule

No historical branch is considered disposable merely because it is behind `main`. A branch can contain unique architectural evidence or a fix that was later lost.
