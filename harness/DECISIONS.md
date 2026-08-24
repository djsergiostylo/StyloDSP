# STYLO HARNESS · DECISIONS

## D-001 · Pilot repository
**Decision:** Use `djsergiostylo/StyloDSP` as the first Harness Engineering pilot.

**Reason:** It already has a documented modular DSP architecture, explicit platform targets and a clear first milestone: Core + Gain + Android APK.

## D-002 · Repository as source of truth
**Decision:** Persistent project state lives in GitHub files, not only in chat history.

**Reason:** ChatGPT, Gemini and Codex can all consume the same project state when operating on the repository.

## D-003 · Incremental harness
**Decision:** Start with instructions, state, feature tracking, verification and handoff before adding autonomous loops or graph orchestration.

**Reason:** Validate the control system first; add autonomy only after the baseline is trustworthy.

## D-004 · No false completion
**Decision:** PASSING requires acceptance criteria plus evidence.

**Reason:** The harness exists to reduce regressions, forgotten work and unsupported claims of completion.
