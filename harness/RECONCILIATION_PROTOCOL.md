# STYLO DSP — PROJECT RECONCILIATION PROTOCOL

## Purpose

Periodically reconstruct the real state of StyloDSP across source code, branches, commits, CI/CD, APKs, tests, documentation and AI project history.

This protocol exists to prevent project drift, duplicated work, lost fixes and contradictory documentation.

## Golden rule

**Do not modify production code during the discovery phase.**

Order:

`OBSERVE → COMPARE → CLASSIFY → DOCUMENT → VALIDATE → CLEAN → DEVELOP`

## When to run

Run when:

- the project has been inactive and is being resumed;
- several AI agents have worked on it;
- many branches/prototypes have accumulated;
- documentation and code disagree;
- a major release is approaching;
- an architectural migration is planned;
- a previous handoff is no longer trustworthy.

# Phase 1 — Full `main` audit

1. Inventory directories and files.
2. Identify application modules, DSP modules, native code, tests, assets and workflows.
3. Inspect recent and significant commits.
4. Identify current build configuration.
5. Identify current release/version metadata.
6. Record implemented features with evidence.
7. Record unknowns separately.

# Phase 2 — Compare important branches against `main`

1. Inventory branches.
2. Rank branches by relevance and recency.
3. Compare relevant branches to `main`.
4. Identify unique files, commits and functionality.
5. Identify fixes/features that exist only outside `main`.
6. Do not merge or delete yet.

# Phase 3 — Recover valuable historical code

For each relevant branch classify:

- `KEEP`
- `MERGE`
- `CHERRY-PICK`
- `ARCHIVE`
- `REBUILD`
- `OBSOLETE`
- `UNKNOWN`

Never delete a branch before its unique value has been recorded.

# Phase 4 — Identify obsolete documentation

Compare documentation claims against current code.

Mark each document/section:

- `CURRENT`
- `PARTIAL`
- `HISTORICAL`
- `OBSOLETE`
- `CONTRADICTED`

Do not silently rewrite historical records. Preserve history and create current canonical documentation where necessary.

# Phase 5 — Reconstruct real APK state

Separate:

`SOURCE BUILDS` → `APK EXISTS` → `APK INSTALLS` → `APP LAUNCHES` → `FEATURE WORKS`.

Record CI run, commit, artifact, checksum if available, installation result, runtime logs and device used.

A successful GitHub Actions build is not runtime validation.

# Phase 6 — Reconcile Harness and project documents

Update from evidence:

- `PROJECT_STATE.md`
- `ARCHITECTURE.md`
- `ROADMAP.md`
- `DECISION_LOG.md`
- `SESSION_HANDOFF.md`
- latest audit
- README/AI entrypoint where needed

# Phase 7 — Create one roadmap

Merge competing plans into one ordered roadmap.

Separate:

- immediate blockers;
- validation work;
- cleanup;
- near-term product work;
- architecture migrations;
- future platform expansion.

# Phase 8 — Inspect dead/incomplete work

Identify duplicate, dead, half-integrated or abandoned implementations.

Do not delete during discovery.

# Phase 9 — Reconcile AI history

Recover useful decisions and requirements from prior AI work where available.

Convert them into durable project knowledge rather than copying conversations wholesale.

Classify each recovered item:

`IMPLEMENTED / PARTIAL / PLANNED / DISCARDED / UNKNOWN`.

# Phase 10 — Review CI/CD

Inspect workflows, artifacts, build variants, release jobs and reproducibility.

Record exactly what CI proves and what it does not prove.

# Phase 11 — Review tests and evidence

Inventory automated and manual tests.

Map important features to evidence.

# Phase 12 — Establish validation matrix

At minimum distinguish:

`BUILD → INSTALL → LAUNCH → LOAD AUDIO → PLAY → DSP → FFT → SEEK → BYPASS → LOOP → VOLUME → STABILITY`.

# Phase 13 — Branch disposition

Only after evidence is recorded, decide which branches should be merged, archived or removed.

# Phase 14 — Canonical state

Produce a concise current state that another AI can trust without reading the entire history.

# Phase 15 — Resume development

Only now modify production code.

The first development task should be the highest-priority validated gap, not an arbitrary feature from historical plans.

## Required audit output

Every reconciliation must end with:

1. Current state
2. What changed since previous audit
3. Contradictions found
4. Recovered work
5. Obsolete work
6. Runtime/build evidence
7. Open risks
8. Branch disposition
9. Documentation updates
10. Single next action
