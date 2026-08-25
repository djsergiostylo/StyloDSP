# HARNESS — AI WORKING INSTRUCTIONS

## Role
Harness is the operational control layer for StyloDSP. It tells an AI how to understand, audit, validate, modify and hand off the project.

## Before changing anything
1. Read `AI_START_HERE.md` and `AI_CONTEXT.md`.
2. Read `harness/STATE.md`, then `harness/PROJECT_STATE.md`.
3. Read `harness/DOCUMENT_CONSISTENCY_PROTOCOL.md`.
4. Inspect actual source before proposing a rewrite.
5. If current truth is unclear, stop and run reconciliation.

## Non-negotiable rules
1. Distinguish implemented, partially implemented, planned, experimental and validated.
2. Build success is not functional success.
3. Do not delete branches, files or code until their value has been identified.
4. Do not reimplement a feature that already exists without evidence that it is broken or inadequate.
5. Historical documents are evidence, not automatically current truth.
6. When repository state conflicts with documentation, reconcile all affected canonical documents together.
7. Keep the current task smaller than the next architectural ambition.
8. Update all affected canonical documents in the same change unit.
9. Leave explicit evidence for claims about builds, APKs, tests and runtime behavior.
10. Do not claim persistence until commit + read-back + cross-reference check succeeds.

## Canonical sources
Use `harness/DOCUMENT_CONSISTENCY_PROTOCOL.md` for the source-of-truth map. Do not create competing state, roadmap, architecture or decision documents.

## Work modes

### Normal development
Read state → understand task → inspect implementation → change minimally → validate → update all affected canonical documents → read back.

### Project resumption
Run the reconciliation protocol before substantial development when repository drift exists.

### Architecture change
Document the decision and migration boundary before changing the canonical implementation.

## Evidence labels
- `IMPLEMENTED`: code exists.
- `BUILT`: CI/build succeeds.
- `RUNTIME-VALIDATED`: behavior observed in the target runtime.
- `PARTIAL`: some required behavior exists.
- `PLANNED`: intended but not implemented.
- `EXPERIMENTAL`: exists outside the canonical path.
- `OBSOLETE`: superseded and should not guide current development.
- `UNKNOWN`: insufficient evidence.

## Stop conditions
Stop feature development and run reconciliation if:
- multiple canonical documents disagree;
- a branch may contain unrecovered functionality;
- a build succeeds but runtime evidence is missing for a critical feature;
- the current task depends on undocumented historical decisions;
- the project has accumulated enough drift that the next change risks duplicating or deleting work.
