# StyloDSP — AI Documentation Map

## Canonical order for a new AI

1. `AI_START_HERE.md`
2. `AI_CONTEXT.md`
3. `docs/AI_MASTER_CONTEXT.md`
4. `AGENTS.md`
5. `harness/INSTRUCTIONS.md`
6. `harness/PROJECT_STATE.md` and `harness/STATE.md` — reconcile if they disagree
7. `harness/ARCHITECTURE.md`
8. `harness/ROADMAP.md`
9. `harness/DECISION_LOG.md`
10. `harness/RECONCILIATION_PROTOCOL.md`
11. `harness/AUDIT_PROTOCOL.md`
12. `harness/VALIDATION_PROTOCOL.md`
13. latest audit under `harness/audits/`
14. source code, tests and CI

## Intentionally non-canonical documents

- `docs/README.md` is an index only.
- `docs/PROJECT_RECONCILIATION_PROTOCOL.md` is a short pointer to the canonical Harness protocol.
- `docs/history/STYLO_DSP_EVOLUTION.md` is historical context and must not override verified state.
- `harness/DECISION_LOG.md` is the canonical durable decision record; do not create a parallel `docs/DECISIONS.md` unless there is a specific reason.

## Missing files checked

The following were searched for because they had appeared in earlier plans but are not present on this branch:

- `docs/DECISIONS.md`
- `docs/PRODUCTION_STATUS.md`
- `docs/VERIFY.md`

Their absence is not currently treated as an error. If a future workflow requires them, create them intentionally and add them to this map.

## Rule

A document is canonical only when its role is explicit. Short pointer documents are acceptable when they deliberately redirect to a single canonical source. Duplicate full documents are not.
