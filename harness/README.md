# STYLO DSP HARNESS

Harness is the operational control layer for AI-assisted work on StyloDSP.

## Canonical entry sequence

1. `../AI_START_HERE.md`
2. `../AI_CONTEXT.md`
3. `../AGENTS.md`
4. `INSTRUCTIONS.md`
5. `STATE.md` + `PROJECT_STATE.md`
6. `DOCUMENT_CONSISTENCY_PROTOCOL.md`
7. `ARCHITECTURE.md`
8. `ROADMAP.md`
9. `DECISION_LOG.md`
10. `RECONCILIATION_PROTOCOL.md`
11. `AUDIT_PROTOCOL.md`
12. `VALIDATION_PROTOCOL.md`
13. latest relevant file under `audits/`
14. source/tests/CI only after the above

## Authority rule
Each document has one defined role. `STATE.md`, `PROJECT_STATE.md`, `features.json`, `DECISION_LOG.md`, `ARCHITECTURE.md`, `ROADMAP.md`, validation/audit protocols and the master AI context are canonical within their domains. Compatibility pointers and historical records never override canonical state.

## Core rule

**Understand → compare → classify → document → validate → modify → read back → cross-check.**

Do not treat a successful build as proof of runtime functionality. Do not create a second source of truth when an existing canonical document already owns the information.
