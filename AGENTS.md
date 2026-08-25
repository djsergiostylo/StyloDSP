# STYLO HARNESS · AGENTS

## Mission
Work on StyloDSP as a verified engineering system. Preserve the portable DSP architecture and avoid claiming completion without evidence.

## AI entry point
Any new AI agent MUST start with:
1. `AI_START_HERE.md`
2. `AI_CONTEXT.md`

Then read the current Harness state and the relevant protocol documents.

## Source of truth
Before changing code, read in this order:
1. `AI_START_HERE.md`
2. `AI_CONTEXT.md`
3. `AGENTS.md`
4. `docs/ARCHITECTURE.md`
5. `harness/PROJECT.md`
6. `harness/STATE.md` and `harness/PROJECT_STATE.md`
7. `harness/features.json`
8. relevant source/tests

## Rules
- One active feature at a time unless a dependency requires otherwise.
- Prefer small, reversible changes.
- Do not rewrite architecture or replace working systems without explicit justification.
- Keep `core/` platform-independent when/if it is introduced as the canonical core.
- Do not put Android, Web, Kotlin, React or VST SDK dependencies into a portable DSP Core.
- Realtime audio code must remain allocation-free, non-blocking and free of I/O/UI calls where the contract requires realtime execution.
- Preserve existing working/reference implementations until replacement parity is demonstrated.
- Never mark a feature PASSING without verification evidence.
- If verification is impossible, mark the feature BLOCKED or IN_PROGRESS and explain why.
- Historical documents are evidence, not automatically current truth.
- Do not delete branches, files or code before identifying their contents and value.

## Work protocol
### Before coding
State: goal, active feature, files likely affected, acceptance criteria, risks. If repository state is inconsistent, run `harness/RECONCILIATION_PROTOCOL.md` first.

### During coding
Implement the smallest coherent change. Avoid unrelated cleanup.

### After coding
Run the strongest available verification. Record exact commands/results when possible. Update feature state and handoff.

## Completion rule
A task is complete only when:
- acceptance criteria are satisfied;
- relevant tests/checks pass;
- no known regression is introduced;
- state files are updated;
- evidence level is explicit.

## Failure protocol
If a test fails:
1. reproduce;
2. identify the first meaningful failure;
3. fix the cause, not the symptom;
4. rerun the relevant verification;
5. do not hide or downgrade the failure.

## Session protocol
At session end update `harness/STATE.md`, `harness/PROJECT_STATE.md` when relevant, and `harness/SESSION_HANDOFF.md`.

## Git protocol
Use focused commits. Do not mix unrelated features. Never force-push or rewrite history unless explicitly requested.
