# AI Project Memory — Persistence Checklist

## Purpose
Verify that every document promised by the AI handoff actually exists in the target branch, is non-empty, has plausible content, and does not point to missing files.

## A — Required files
- [ ] `docs/AI_MASTER_CONTEXT.md`
- [ ] `docs/ai-project-memory/README.md`
- [ ] `AI_START_HERE.md`
- [ ] `AI_CONTEXT.md`
- [ ] `AGENTS.md`
- [ ] `harness/INSTRUCTIONS.md`
- [ ] `harness/PROJECT.md`
- [ ] `harness/STATE.md`
- [ ] `harness/PROJECT_STATE.md`
- [ ] `harness/ARCHITECTURE.md`
- [ ] `harness/ROADMAP.md`
- [ ] `harness/DECISION_LOG.md`
- [ ] `harness/RECONCILIATION_PROTOCOL.md`
- [ ] `harness/AUDIT_PROTOCOL.md`
- [ ] `harness/VALIDATION_PROTOCOL.md`
- [ ] `harness/SESSION_HANDOFF.md`
- [ ] `harness/features.json`
- [ ] latest branch/repository audit

## B — Size / truncation
Flag files that are missing, empty, suspiciously tiny, placeholders, truncated, syntactically incomplete, or substantially smaller than the scope promised.

Warning thresholds: `<100 B` inspect; `<500 B` inspect unless intentionally a pointer; `<1 KB` is high priority for a full protocol/context document. Size is a warning signal, not proof of failure.

## C — Cross-reference integrity
- [ ] Every referenced path exists.
- [ ] Every referenced branch/commit exists.
- [ ] No document claims a file was created without confirming it exists.
- [ ] Harness status agrees with latest audit.
- [ ] `features.json` does not claim PASSING when evidence says otherwise.
- [ ] README, STATE, PROJECT and MASTER_CONTEXT contradictions are explicitly recorded.

## D — Historical evidence
- [ ] ChatGPT claims labelled historical/unverified when appropriate.
- [ ] GitHub source/commit evidence distinguished from chat memory.
- [ ] Historical branches inventoried before rename/delete.
- [ ] APK claims identify commit/version where possible.
- [ ] Runtime validation distinguished from build success.

## E — Clean-room handoff
A new AI must be able to find the entrypoint, master context, Harness, current state, latest audit, verified-vs-historical status and next task without reading old chats.

## Execution
**Status:** PENDING COMPLETE REPOSITORY AUDIT
**Target:** `docs/ai-project-memory`
**Date:** 2026-08-25
**Rule:** a file is considered persisted only after commit AND successful post-commit read-back.
