# STYLO DSP — DECISION LOG

This log records durable decisions recovered from project work. It is intentionally not a transcript of AI conversations.

## 2026-08-25 — Reconcile before expanding

**Decision:** Perform a deep repository reconciliation before further feature development.

**Reason:** Multiple generations of code, branches and documentation exist. Continuing without reconciliation risks duplicate implementation and loss of valuable historical work.

**Status:** ACTIVE

## 2026-08-25 — Harness as operational control layer

**Decision:** Use `harness/` as the operational control layer for AI agents working on StyloDSP.

**Reason:** New AI agents need durable instructions, current state, architecture, decisions, validation and handoff information inside the repository.

**Status:** ACTIVE

## 2026-08-25 — AI onboarding must be repository-native

**Decision:** Maintain `AI_START_HERE.md` and `AI_CONTEXT.md` in the repository.

**Reason:** Future AI sessions must not depend on access to historical ChatGPT conversations.

**Status:** ACTIVE

## 2026-08-25 — Android validation before Rust migration

**Decision:** Validate the current Android path before making the Rust Core the canonical implementation.

**Reason:** The Android path has substantial implemented functionality, while runtime validation is incomplete and the Rust path is not yet canonical on `main`.

**Status:** ACTIVE

## 2026-08-25 — Build is not runtime proof

**Decision:** Never classify the Android release as validated from CI success alone.

**Reason:** A previous APK test exposed a native-library packaging/runtime problem.

**Status:** ACTIVE

## 2026-08-25 — Preserve historical branches until audited

**Decision:** Do not delete branches before their unique value is identified.

**Reason:** The repository contains multiple prototype and architecture branches that may contain unrecovered fixes or functionality.

**Status:** ACTIVE
