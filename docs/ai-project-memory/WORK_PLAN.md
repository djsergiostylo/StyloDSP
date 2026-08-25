# AI Project Memory — Active Work Plan

## Mission
Finish documentation/harness reconciliation before modifying product code.

## Execution loop
1. Inspect repository state.
2. Compare documents and source.
3. Classify canonical vs pointer vs historical.
4. Fix documentation drift.
5. Update checklist.
6. Update roadmap/state.
7. Commit.
8. Read back the committed files.
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
- [ ] Complete full tree + file-size audit.
- [ ] Complete cross-reference audit.
- [ ] Audit all remaining Harness paths/references.
- [ ] Audit branches and recover unique code.
- [ ] Reconstruct real APK/runtime status.
- [ ] Reconcile complete ChatGPT history where available.
- [ ] Finalize official V0.x.x history.
- [ ] Generate final local export ZIP.
- [ ] Review merge to `main`.

## Guardrails
- Do not delete historical branches before inventory.
- Do not mark PASSING without evidence.
- Do not migrate the architecture broadly before reconciliation.
- Do not treat branch names as product versions.
- Do not claim persistence without commit + read-back.
