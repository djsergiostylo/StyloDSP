# StyloDSP — AI Documentation Map

## Purpose
This map prevents multiple documents from becoming competing sources of truth. Canonical documents contain current truth. Pointer documents redirect to the canonical source. Historical documents preserve evidence but never override current verified state.

## Canonical order for a new AI

1. `AI_START_HERE.md`
2. `AI_CONTEXT.md`
3. `docs/AI_MASTER_CONTEXT.md`
4. `AGENTS.md`
5. `harness/INSTRUCTIONS.md`
6. `harness/DOCUMENT_CONSISTENCY_PROTOCOL.md`
7. `harness/STATE.md`
8. `harness/PROJECT_STATE.md`
9. `harness/ARCHITECTURE.md`
10. `harness/ROADMAP.md`
11. `harness/DECISION_LOG.md`
12. `harness/RECONCILIATION_PROTOCOL.md`
13. `harness/AUDIT_PROTOCOL.md`
14. `harness/VALIDATION_PROTOCOL.md`
15. latest audit under `harness/audits/`
16. source code, tests and CI

## Pointer / compatibility documents

- `docs/README.md` is an index.
- `docs/PROJECT_RECONCILIATION_PROTOCOL.md` points to the canonical reconciliation protocol.
- `harness/DECISIONS.md` is a legacy compatibility pointer to `harness/DECISION_LOG.md`.
- `harness/VERIFY.md` is a legacy compatibility pointer to `harness/VALIDATION_PROTOCOL.md`.

## Historical / checkpoint documents

- `docs/history/STYLO_DSP_EVOLUTION.md` is historical context.
- `harness/PRODUCTION_STATUS.md` is a dated implementation checkpoint and must not override `STATE.md`.
- `harness/AUDIT-2026-08-24.md` is historical audit evidence.
- `harness/audits/*` contains dated audit evidence.

## Important distinction

`docs/PRODUCTION_CANDIDATE_0.3.0.md` and similar version-labelled documents describe a candidate or historical target. They are not proof that the current `main` runtime is release-validated.

## Missing names previously investigated

Earlier planning mentioned `docs/DECISIONS.md`, `docs/PRODUCTION_STATUS.md`, and `docs/VERIFY.md`. The current repository uses `harness/DECISIONS.md`, `harness/PRODUCTION_STATUS.md`, and `harness/VERIFY.md` instead. The two legacy policy files are now explicit pointers, while the production-status file remains a dated checkpoint.

## Rule
A document is canonical only when its role is explicit. Duplicate full policies are not allowed. When a canonical fact changes, update the canonical source and all directly dependent summaries in one synchronized change unit, then perform read-back and contradiction search.
