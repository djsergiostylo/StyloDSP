# STYLO HARNESS · AGENTS

## Mission
Work on StyloDSP as a verified engineering system. Preserve the portable DSP architecture and avoid claiming completion without evidence.

## Source of truth
Before changing code, read in this order:
1. `AGENTS.md`
2. `docs/ARCHITECTURE.md`
3. `harness/PROJECT.md`
4. `harness/STATE.md`
5. `harness/features.json`
6. relevant source/tests

## Rules
- One active feature at a time unless a dependency requires otherwise.
- Prefer small, reversible changes.
- Do not rewrite architecture or replace working systems without explicit justification.
- Keep `core/` platform-independent.
- Do not put Android, Web, Kotlin, React or VST SDK dependencies into the DSP Core.
- Realtime audio code must remain allocation-free, non-blocking and free of I/O/UI calls.
- Preserve existing TypeScript implementation as reference until Rust parity exists.
- Never mark a feature PASSING without verification evidence.
- If verification is impossible, mark the feature BLOCKED or IN_PROGRESS and explain why.

## Work protocol
### Before coding
State: goal, active feature, files likely affected, acceptance criteria, risks.

### During coding
Implement the smallest coherent change. Avoid unrelated cleanup.

### After coding
Run the strongest available verification. Record exact commands/results when possible. Update feature state and handoff.

### Completion rule
A task is complete only when:
- acceptance criteria are satisfied;
- relevant tests/checks pass;
- no known regression is introduced;
- state files are updated.

## Failure protocol
If a test fails:
1. reproduce;
2. identify the first meaningful failure;
3. fix the cause, not the symptom;
4. rerun the relevant verification;
5. do not hide or downgrade the failure.

## Session protocol
At session end update `harness/STATE.md` and `harness/SESSION_HANDOFF.md`.

## Git protocol
Use focused commits. Do not mix unrelated features. Never force-push or rewrite history unless explicitly requested.
