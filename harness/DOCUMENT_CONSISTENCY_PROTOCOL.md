# STYLO DSP — DOCUMENT CONSISTENCY PROTOCOL

## Purpose
Prevent documentation drift by defining one source of truth for each class of project information.

## Canonical sources

| Information | Canonical document | Others may do |
|---|---|---|
| Operational current state | `harness/STATE.md` | summarize only |
| Expanded project state | `harness/PROJECT_STATE.md` | explain, never contradict |
| AI entrypoint | `AI_START_HERE.md` | point to canonical sources |
| AI context | `AI_CONTEXT.md` | compact orientation |
| Master AI context | `docs/AI_MASTER_CONTEXT.md` | durable consolidated context |
| Architecture | `harness/ARCHITECTURE.md` | describe only |
| Roadmap | `harness/ROADMAP.md` | point to it, do not fork plans |
| Decisions | `harness/DECISION_LOG.md` | historical docs may preserve old decisions |
| Features/status | `harness/features.json` | status evidence only |
| Validation rules | `harness/VALIDATION_PROTOCOL.md` | point to it |
| Audit rules | `harness/AUDIT_PROTOCOL.md` | point to it |
| Reconciliation rules | `harness/RECONCILIATION_PROTOCOL.md` | short pointers allowed |
| Session transfer | `harness/SESSION_HANDOFF.md` | temporary handoff only |
| Branch evidence | `harness/audits/` | historical audit records |

## Change transaction

When a change affects project truth, update every affected canonical source in the same work unit. Do not update one document and leave known dependent documents stale.

Minimum dependency check:

`STATE → PROJECT_STATE → features.json → ARCHITECTURE/ROADMAP/DECISIONS → MASTER_CONTEXT → START_HERE/AI_CONTEXT → SESSION_HANDOFF`

Not every change requires every file to change. The author must explicitly decide which are affected.

## Status rules

- `PASSING` requires evidence at the acceptance level.
- `BUILT` means an artifact was built, not that runtime works.
- Historical validation must identify its branch/version/commit.
- A historical document never overrides current source or current validation evidence.
- Unknown information stays `UNKNOWN`; do not fill gaps by inference.

## Conflict resolution

When two canonical documents disagree:

1. Stop feature work.
2. Inspect source/build/runtime evidence.
3. Record the conflict.
4. Decide the canonical truth.
5. Update all affected documents together.
6. Commit.
7. Read every changed document back from the commit.
8. Run a repository search for the old contradictory claim.
9. Continue only when no active canonical document contradicts the result.

## Version history

Branch names are not product versions. Version labels are assigned only after commit lineage and product milestones have been reconstructed.

## Persistence gate

A change is considered persisted only after:

`WRITE → COMMIT → READ-BACK → CROSS-REFERENCE CHECK`
