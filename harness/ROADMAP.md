# STYLO DSP — SINGLE ROADMAP

## Phase 1 — Reconcile

1. Complete deep audit of `main`.
2. Compare important branches against `main`.
3. Recover valuable branch-only work.
4. Identify obsolete/contradictory documentation.
5. Reconcile prior AI project knowledge with repository evidence.

## Phase 2 — Validate Android

1. Produce a known APK.
2. Install on reference device.
3. Validate launch/load/playback.
4. Validate EQ, FFT, seek, bypass, loop and volume.
5. Investigate any native-library/runtime packaging issue.
6. Record evidence.

## Phase 3 — Canonicalize

1. Finalize Harness state.
2. Establish one architecture document.
3. Establish one decision log.
4. Establish one roadmap.
5. Establish repeatable validation.

## Phase 4 — Clean

Only after reconciliation and validation:

- consolidate branches;
- archive obsolete experiments;
- remove safe duplicates/dead code;
- clean documentation;
- simplify CI/CD where justified.

## Phase 5 — Architecture evolution

Evaluate the Rust Core migration only after the Android MVP is validated and the API boundary is defined.

## Phase 6 — Product expansion

Potential future targets:

- reusable cross-platform DSP core;
- WASM/web;
- VST3;
- expanded analysis/mastering capabilities.

## Rule

Do not skip a validation/cleanup phase merely to reach a later architecture faster.
