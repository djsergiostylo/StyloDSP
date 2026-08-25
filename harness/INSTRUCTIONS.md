# HARNESS — AI WORKING INSTRUCTIONS

## Role

Harness is the operational control layer for StyloDSP. It tells an AI how to understand, audit, validate, modify and hand off the project.

## Non-negotiable rules

1. Read `AI_START_HERE.md` and `AI_CONTEXT.md` before work.
2. Read current Harness state before making assumptions.
3. Inspect actual code before proposing a rewrite.
4. Distinguish implemented, partially implemented, planned, experimental and validated.
5. Build success is not functional success.
6. Do not delete branches, files or code until their value has been identified.
7. Do not reimplement a feature that already exists without evidence that it is broken or inadequate.
8. Historical documents are evidence, not automatically current truth.
9. When repository state conflicts with documentation, record the conflict and reconcile it.
10. Keep the current task smaller than the next architectural ambition.
11. Update state and handoff documentation after meaningful changes.
12. Leave explicit evidence for claims about builds, APKs, tests and runtime behavior.

## Work modes

### Normal development
Read state → understand task → inspect implementation → change minimally → validate → update state/handoff.

### Project resumption
Run the reconciliation protocol before substantial development when the repository has drifted from the last known state.

### Architecture change
Document the decision and migration boundary before changing the canonical implementation.

## Evidence labels

- `IMPLEMENTED`: code exists.
- `BUILT`: CI/build succeeds.
- `RUNTIME-VALIDATED`: behavior has been observed in the target runtime.
- `PARTIAL`: some required behavior exists.
- `PLANNED`: intended but not implemented.
- `EXPERIMENTAL`: exists outside the canonical path.
- `OBSOLETE`: superseded and should not guide current development.
- `UNKNOWN`: insufficient evidence.

## Stop conditions

Stop feature development and run reconciliation if:

- multiple documents disagree about the current architecture;
- a branch may contain unrecovered functionality;
- a build succeeds but runtime evidence is missing for a critical feature;
- the current task depends on undocumented historical decisions;
- the project has accumulated enough drift that the next change risks duplicating or deleting work.
