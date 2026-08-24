# STYLO HARNESS · VERIFICATION

## Verification ladder

### Level 1 · Static
- Files/configuration are present.
- Architecture boundaries are respected.
- No obvious accidental scope expansion.

### Level 2 · Build
- Run the project's documented build commands.
- Record exact result.

### Level 3 · Unit/integration tests
- Run relevant tests.
- Record pass/fail and meaningful failures.

### Level 4 · Runtime
- Execute the actual feature path where possible.
- Verify expected behavior, finite audio and error handling.

### Level 5 · Parity
- Compare reference PCM/vector outputs across implementations.
- Use explicit tolerances.

## PASSING rule
A feature can be `PASSING` only when its acceptance criteria have evidence.

## BLOCKED rule
Use `BLOCKED` when progress depends on an external prerequisite, missing toolchain, unavailable target or unresolved design decision. Explain the blocker in `STATE.md`.

## Regression rule
A new feature must not silently invalidate an existing PASSING feature. If it does, downgrade the affected feature and record the regression.

## Minimum session report
```text
Feature:
Change:
Verification:
Result:
Known risks:
Next step:
```
