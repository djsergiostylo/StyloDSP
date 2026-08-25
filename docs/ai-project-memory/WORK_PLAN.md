# AI Project Memory — Active Work Plan

## Mission
Move from repository reconciliation into the real product goal: a reliable Android STYLO DSP EQ APK, while preserving the synchronized Harness and historical evidence.

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

## Reconciliation checklist
- [x] Create `docs/AI_MASTER_CONTEXT.md`.
- [x] Create `docs/ai-project-memory/README.md`.
- [x] Create persistence checklist.
- [x] Confirm master context exists by post-commit read-back.
- [x] Establish canonical document hierarchy and consistency protocol.
- [x] Synchronize STATE, PROJECT_STATE, features, roadmap, decisions, validation, audit and handoff rules.
- [x] Record historical V0.2.0 physical-device validation separately from current-main status.
- [x] Classify all 20 listed branches.
- [x] Resolve V0.2.1/V0.2.2 history.
- [x] Resolve prototype Android/Rust lines.
- [x] Resolve Harness/audit checkpoint branches.
- [x] Search repository for embedded tool/chat citation markup.
- [x] Complete current product/Harness cross-reference pass.
- [x] Freeze current Android implementation baseline.
- [x] Correct current-main feature truth: H-002 is IN_PROGRESS until current-main runtime evidence exists.
- [x] Transition STATE from reconciliation to current Android EQ validation.

## Product execution: CURRENT
- [ ] Clean-build current `main`.
- [ ] Run current unit-test task.
- [ ] Produce current debug APK.
- [ ] Install APK on reference Android device.
- [ ] Verify audio file selection and decode.
- [ ] Verify play/pause/seek/loop/volume.
- [ ] Verify 31-band EQ audible response, bypass, flat and reset.
- [ ] Verify 8-band parametric frequency/gain/Q/filter types.
- [ ] Verify spectrum correctness and smooth UI refresh.
- [ ] Verify preset save/load and process-restart persistence.
- [ ] Fix evidence-backed defects only.
- [ ] Repeat build + tests + device gates after fixes.
- [ ] Record device evidence.
- [ ] Only then decide official version/release status.

## Later
- [ ] Recover unique historical components where current product gates justify them.
- [ ] Reconcile complete ChatGPT project history where available.
- [ ] Finalize official V0.x.x genealogy.
- [ ] Clean/archive historical branches only after final export.
- [ ] Generate final local export ZIP.
- [ ] Review merge of reconciliation documentation into `main`.

## Guardrails
- Do not delete historical branches before final export.
- Do not mark PASSING without evidence.
- Do not migrate architecture broadly before a measured defect requires it.
- Do not treat branch names as product versions.
- Do not claim persistence without commit + read-back + cross-reference check.
- If canonical documents disagree, stop product work and reconcile them before continuing.
