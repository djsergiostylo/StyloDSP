# AI Project Memory — Active Work Plan

## Mission
Finish documentation/harness reconciliation before modifying product code, with synchronized canonical documentation and no known cross-document contradictions.

## Execution loop
1. Inspect repository state.
2. Compare documents and source.
3. Classify canonical vs pointer vs historical.
4. Fix documentation drift as one synchronized change unit.
5. Update checklist and state.
6. Commit.
7. Read back every changed canonical document.
8. Search for the superseded contradictory claim.
9. Only then continue.

## Current checklist
- [x] Create `docs/AI_MASTER_CONTEXT.md`.
- [x] Create `docs/ai-project-memory/README.md`.
- [x] Create persistence checklist.
- [x] Confirm master context exists by post-commit read-back.
- [x] Confirm project reconciliation protocol is a pointer, not a competing full protocol.
- [x] Confirm `docs/README.md` is an index/pointer.
- [x] Confirm historical evolution document is explicitly non-canonical.
- [x] Confirm `harness/DECISION_LOG.md` is the durable decision source.
- [x] Record missing legacy document names as non-errors.
- [x] Reconcile `STATE.md` vs `PROJECT_STATE.md`.
- [x] Reconcile `features.json` against latest evidence.
- [x] Audit core Harness instructions/architecture/validation/audit/handoff for stale high-level claims.
- [x] Correct AI context/entrypoint to distinguish historical V0.2.0 runtime validation from current `main` runtime status.
- [x] Inventory high-value branches: `core-rust`, `android-v0.2.0-validated`, `android-gain-realtime`, `prod/full-eq-v1`.
- [x] Record historical V0.2.0 physical-device validation evidence.
- [x] Recheck `AI_MASTER_CONTEXT`, branch inventory and `STATE` for consistency.
- [x] Update `STATE` to explicitly separate historical V0.2.0 validation from current-main validation.
- [x] Confirm updated `STATE` by post-commit read-back.
- [x] Create canonical document consistency/source-of-truth protocol.
- [x] Update Harness instructions to enforce synchronized document changes.
- [x] Synchronize expanded project state with the new consistency protocol.
- [x] Read back the new consistency protocol, instructions and project state.
- [x] Search repository for known stale H-002 `PASSING` claim; retained only in historical audit evidence.
- [x] Classify `v0.2.1-stylo-eq-mvp` as product-history/MVP source.
- [x] Classify `v0.2.2-first-apk-mvp` as APK-history source.
- [x] Compare V0.2.1/V0.2.2 against current `main` and record branch-only evidence.
- [x] Update branch inventory with V0.2.1/V0.2.2 divergence and recovery rules.
- [x] Search repository for embedded tool/chat citation markup; none found.
- [x] Verify branch inventory read-back after update.
- [x] Compare `prototype/android-apk`, `prototype/android-apk-v2`, `ci/android-build-verification`, `pre-final-build-2026-08-24` and `v0.2-harness-pilot` against `main`.
- [x] Expand branch inventory to all 20 currently listed branches and explicitly mark uninspected branches as PENDING.
- [x] Preserve branch disposition as evidence; no branch deletion or rename performed.
- [ ] Compare remaining pending audit/checkpoint branches.
- [ ] Complete full tree + file-size audit.
- [ ] Complete cross-reference audit.
- [ ] Audit all remaining Harness paths/references.
- [ ] Recover unique historical code/decisions where justified.
- [ ] Reconstruct latest APK/runtime status.
- [ ] Reconcile complete ChatGPT history where available.
- [ ] Finalize official V0.x.x history.
- [ ] Generate final local export ZIP.
- [ ] Review merge to `main`.

## Guardrails
- Do not delete historical branches before inventory.
- Do not mark PASSING without evidence.
- Do not migrate the architecture broadly before reconciliation.
- Do not treat branch names as product versions.
- Do not claim persistence without commit + read-back + cross-reference check.
- If canonical documents disagree, stop feature work and reconcile them before continuing.
