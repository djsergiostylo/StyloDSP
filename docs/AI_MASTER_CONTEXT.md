# STYLO DSP — MASTER AI CONTEXT / PROJECT DNA

**Purpose:** compact handoff for a new AI agent. This document combines the currently verified GitHub state with durable project context reconstructed from the StyloDSP work recorded in ChatGPT. It is deliberately written as a 3+ page equivalent rather than a one-page summary so that a new agent can understand the project without access to prior chats.

**Date:** 2026-08-25  
**Repository:** `djsergiostylo/StyloDSP`  
**Default branch:** `main`  
**Documentation staging branch:** `docs/ai-project-memory`  
**Current phase:** H-002 / deep repository radiography and reconciliation.

> **Evidence rule:** ChatGPT history is historical design/context evidence, not automatically current truth. When this document conflicts with source code, CI, or runtime evidence, the contradiction must be reported and resolved rather than silently choosing one side.

---

## 1. WHAT STYLO DSP IS

StyloDSP is a single evolving DSP/audio project. It is **not a collection of unrelated applications**. Over time it has accumulated Android implementations, EQ/audio experiments, CI/APK checkpoints, a Web/TypeScript implementation, and a Rust DSP Core architecture.

The long-term architectural target documented in the repository is a portable **STYLO DSP CORE**, intended to implement DSP once and expose it through platform adapters for Web/WASM, Android and eventually VST3. The repository README explicitly states that Web, Android and VST3 should be hosts/adapters rather than separate implementations of the DSP algorithms. fileciteturn78file0L2-L2

The engineering objective is therefore twofold:

1. build and validate useful DSP/audio functionality;
2. preserve enough persistent project memory that a future AI can continue the work without depending on an old conversation.

---

## 2. CURRENT REPOSITORY TOPOLOGY

The current `main` root contains `.github`, `android`, `docs`, `harness`, `web`, `AGENTS.md`, `README.md` and `.gitignore`. fileciteturn77file0L1-L2

The repository currently has a Harness layer whose purpose is to make AI-assisted development repeatable. `AGENTS.md` instructs agents to read the project/architecture/state/feature documents before changing code, keep the Core platform-independent, preserve the TypeScript implementation as reference until Rust parity exists, and never mark features PASSING without evidence. fileciteturn79file0L2-L2

The repository is therefore intended to function as a persistent engineering memory system, not merely a source-code container.

---

## 3. THE MOST IMPORTANT CURRENT CONTRADICTION

There is a material discrepancy that a new AI **must not ignore**.

The README describes the project as being in transition toward a Rust DSP Core and specifies a future architecture containing `core/`, platform adapters and a Rust implementation. fileciteturn78file0L2-L2

However, the current Harness state says that the Android GitHub Actions workflow expects `core/` and `android/`, while the root tree inspected on `main` did not expose those directories at the time of that audit. It also says Rust compilation, Android compilation, APK generation and runtime audio were not yet verified. fileciteturn81file0L2-L2

At the same time, `harness/features.json` contains stronger historical claims, including H-002 marked `PASSING`, a previously verified Android MVP APK, and several Android DSP features connected to a canonical `PcmPlayerEngine`, while still marking their final real-device verification as pending. fileciteturn82file0L2-L2

**Interpretation:** the repository contains evidence from multiple project generations. This is exactly why the current H-002 reconciliation must finish before anyone declares the present Android/Rust state final.

Do not resolve this contradiction by assumption. Inspect commits, branches, files and CI artifacts.

---

## 4. HISTORICAL CHATGPT CONTEXT

The accumulated project work recorded in ChatGPT describes an Android DSP application with a substantial evolution beyond an early visual/UI prototype. Historical work discussed and/or implemented included:

- 31-band graphic EQ;
- parametric EQ concepts;
- Biquad filtering;
- PCM-oriented playback;
- FFT/spectrum analysis;
- gain/volume;
- bypass;
- seek and loop transport;
- safety limiting;
- professional mobile UI work;
- presets/persistence;
- Android APK generation and CI;
- a Rust DSP Core;
- future WASM and VST3 hosts;
- cross-platform DSP parity;
- Harness Engineering and persistent AI state.

Some of these statements have direct current-repository evidence; others are historical claims that require reconciliation. The correct status is determined by the audit, not by the age or confidence of a chat statement.

---

## 5. CURRENT / TARGET DSP ARCHITECTURE

The repository README defines a target architecture approximately as:

```text
                    STYLO DSP CORE
                         Rust
                          |
             +------------+------------+
             |            |            |
          Web/WASM     Android/FFI    VST3
             |            |            |
        AudioWorklet   Oboe/AAudio     DAW
```

The Core is intended to remain independent of Web Audio, Android, Kotlin, React and the VST SDK. The realtime policy requires no heap allocation/deallocation in the callback, no blocking locks, no I/O or UI calls, preallocated buffers and lock-free parameter transport where appropriate. The repository README documents these principles explicitly. fileciteturn78file0L2-L2

The intended modular engine includes Gain, EQ, Compressor, Saturation, Clipper V1, Limiter V1, Analyzer and a `ChainManager` responsible for adding, removing, moving, duplicating, naming, enabling/disabling, validating, building, serializing and restoring modules. fileciteturn78file0L2-L2

The intended numeric policy is `f32` by default, selective `f64` only where justified by tests, with a common SIMD abstraction targeting ARM NEON, WASM SIMD and scalar fallback. fileciteturn78file0L2-L2

---

## 6. ANDROID / AUDIO PATH: DO NOT COLLAPSE GENERATIONS

Historical ChatGPT work identified an Android PCM path described as:

`MediaExtractor → MediaCodec → PCM → DSP/EQ → FFT/metering → SafetyLimiter → AudioTrack`

and the current Harness handoff records this as the known Android path while explicitly stating that runtime validation is incomplete. fileciteturn84file0L2-L2

Separately, the current README describes a **first experimental Android architecture test** based on:

`PCM/WAV → Android → Oboe/AAudio → native bridge/FFI → STYLO DSP CORE → Gain → output`.

The README treats this as a target experimental architecture, not proof that the migration is complete. fileciteturn78file0L2-L2

Therefore a new AI must distinguish:

- the historical/current Android application path;
- the proposed Rust/Oboe architecture test;
- actual APK artifacts;
- verified runtime behavior.

They are not interchangeable.

---

## 7. RUST CORE: REAL HISTORY, NOT AUTOMATICALLY CURRENT

ChatGPT/GitHub investigation identified a real historical `core-rust` development line containing Rust DSP Core scaffolding, FFI and a gain processor. This means Rust was not merely a future idea. However, historical Rust work must not be described as integrated into the current `main` unless the current tree and build prove it.

The repository's current stated roadmap starts with:

1. Rust DSP Core;
2. Gain + reference tests;
3. realtime safety / zero allocation;
4. experimental Android APK;
5. Web/WASM + AudioWorklet;
6. progressive module migration;
7. presets/parity;
8. VST3 adapter;
9. final UIs.

The first platform gate is explicitly **Core + Gain + Android APK**. fileciteturn78file0L2-L2

Strategic decision from the project reconciliation: **do not perform a broad Rust migration before reconciling and validating the existing project.** Recover useful Rust work selectively.

---

## 8. HISTORICAL BRANCHES AND NOMENCLATURE PROBLEM

The project has used branch names that can look like product versions, such as `android-v0.2.0-validated`, `v0.2.1-stylo-eq-mvp`, `v0.2.2-first-apk-mvp`, `prod/full-eq-v1`, `pre-final-build-2026-08-24`, `core-rust`, `android-gain-realtime` and prototype branches.

These must not be interpreted as separate products.

**Correct model:**

```text
Product: StyloDSP
  |
  +-- official version: V0.x.x
  |
  +-- development branch: feature/...
  |
  +-- experiment: experiment/...
  |
  +-- archived history: archive/...
  |
  +-- APK artifact: generated from a specific commit/version
```

Do not rename historical branches until their contents are inventoried and the chronology is reconstructed.

Future official product versions should use `V0.x.x`; Git branches should use semantic development categories.

---

## 9. HARNESS: THE PROJECT CONTROL LAYER

Harness is not simply a state file. It is the operational control layer for AI agents.

Its purpose is to enforce:

- persistent state;
- explicit feature status;
- acceptance criteria;
- verification evidence;
- session handoff;
- decision logging;
- controlled changes;
- reproducibility.

`harness/PROJECT.md` states that the pilot exists to test whether different AIs can continue the same project using persistent truth, explicit states and objective verification. fileciteturn80file0L2-L2

`AGENTS.md` reinforces the same principle: one active feature at a time where possible, small reversible changes, no architecture rewrite without justification, platform-independent Core, realtime safety, and evidence before PASSING. fileciteturn79file0L2-L2

A new AI should therefore enter through:

`AI_START_HERE.md`
→ `AI_CONTEXT.md`
→ `harness/INSTRUCTIONS.md`
→ `harness/PROJECT_STATE.md` / `harness/STATE.md`
→ `harness/ARCHITECTURE.md`
→ `harness/ROADMAP.md`
→ `harness/DECISION_LOG.md`
→ latest audit
→ validation protocol
→ source/tests.

---

## 10. RECONCILIATION PROTOCOL

Before major coding, execute:

1. Audit `main` completely.
2. Compare important branches against `main`.
3. Identify valuable branch-only code.
4. Identify obsolete/contradictory documentation.
5. Reconstruct the real APK/build/runtime state.
6. Reconcile Harness/STATE/PROJECT/README.
7. Create one canonical roadmap.
8. Identify dead, duplicate and incomplete code.
9. Cross-reference ChatGPT decisions with GitHub.
10. Review CI/CD.
11. Review tests and evidence.
12. Record validation gaps.
13. Classify branches.
14. Establish acceptance criteria.
15. Only then modify source architecture.

Protocol:

**OBSERVE → COMPARE → CLASSIFY → DOCUMENT → VALIDATE → MODIFY**

Never delete historical code before understanding what it contains.

---

## 11. VALIDATION MODEL

The project uses a useful evidence ladder:

`E0 Assumption → E1 Documentation → E2 Source → E3 Build → E4 Runtime → E5 Reproducible validation`

The key rule is:

**BUILD SUCCESS ≠ FUNCTIONAL SUCCESS.**

For Android, the real validation sequence must eventually cover:

Build → APK artifact → Install → Launch → Load audio → Play → DSP audible → EQ → FFT → Seek → Bypass → Loop → Volume → long-run stability → logs/crash review.

The repository's current state explicitly says Rust compilation, Android target compilation, APK generation, runtime audio path and realtime allocation safety were not yet verified in that audit. fileciteturn81file0L2-L2

---

## 12. KNOWN FEATURE MAP

The current `features.json` tracks:

- H-001 Harness baseline: PASSING;
- H-002 repository audit/verification baseline: recorded as PASSING in the file, but this status conflicts with the later STATE audit and therefore must be revalidated;
- AND-002 smooth realtime analyzer: IN_PROGRESS;
- EQ-031 31-band graphic EQ: IN_PROGRESS;
- EQ-PARAM 8-band parametric EQ: IN_PROGRESS;
- PLAYER-001 integrated player: IN_PROGRESS;
- PRESET-001 presets/persistence: IN_PROGRESS;
- UX-001 mobile professional UI: IN_PROGRESS;
- REL-001 production release: NOT_STARTED. fileciteturn82file0L2-L2

This feature matrix is valuable, but its evidence must be reconciled with current source and runtime before being treated as canonical.

---

## 13. PRODUCT / ENGINEERING DIRECTION

The project is intended to become a professional configurable DSP/mastering-oriented system, with modular processing, presets, analyzer/metering, realtime-safe processing and cross-platform parity.

Long-term platform model:

`Rust Core → Android / Web / VST3`

The DSP algorithms should live in the Core. Hosts/adapters should handle platform-specific lifecycle, buffers, automation and UI integration.

Cross-platform parity is important: reference PCM vectors should eventually be processed by scalar Rust, SIMD Rust, WASM and Android/VST3 and compared within documented tolerances. The repository README explicitly describes this parity goal. fileciteturn78file0L2-L2

---

## 14. WHAT A NEW AI MUST NOT DO

- Do not assume branch names are official product versions.
- Do not assume every APK is a different application.
- Do not assume historical documentation is current.
- Do not assume Rust is integrated simply because Rust files exist in history.
- Do not migrate the whole project to Rust without first reconciling the current implementation.
- Do not delete old branches before inventorying them.
- Do not mark a feature PASSING because it compiles.
- Do not create a competing roadmap.
- Do not rewrite working architecture without explicit justification.
- Do not mix unrelated cleanup with a feature implementation.
- Do not silently resolve contradictions between Harness documents.

---

## 15. CURRENT PRIORITY

The immediate project task is **second deep radiography / reconciliation**.

Required order:

### A. Discover
Audit `main`, branches, source tree, workflows, tests, APK artifacts and documentation.

### B. Compare
Compare historical branches and commits against `main`.

### C. Recover
Identify unique valuable code, especially Rust Core work and historically useful DSP implementations.

### D. Canonicalize
Update Harness, STATE, PROJECT, README, feature states and roadmap using verified evidence.

### E. Validate
Build/install/run and record objective evidence.

### F. Clean
Only after the above, archive/remove obsolete branches, docs and duplicate code.

### G. Develop
Only after reconciliation, resume architectural/product implementation.

---

## 16. CHATGPT CONTEXT EXTRACTION: WHAT IS AND IS NOT INCLUDED

This document incorporates the durable StyloDSP information available in the current conversation context and user/project memory, including the project's Android/DSP evolution, Rust direction, Harness design, branch/version confusion, validation philosophy and the objective of making the repository self-contained for future AIs.

It **does not claim to have exhaustively read every private ChatGPT conversation or every ChatGPT Project transcript**. ChatGPT does not expose all historical conversations as a single guaranteed searchable corpus through this document-generation process. Therefore, any future full-history extraction should be treated as a separate evidence-gathering phase.

The correct way to finish the full-history version is:

`ChatGPT conversations/projects → extract decisions/features/requirements → deduplicate → timestamp → classify as current/historical/planned → cross-check against GitHub → update this document and Harness.`

Never paste raw conversation dumps into the project memory. Convert them into structured knowledge.

---

## 17. DEFINITIVE HANDOFF COMMAND

When giving this project to another AI, the user can say:

> **Open the StyloDSP repository. Read `docs/AI_MASTER_CONTEXT.md` first, then follow `AI_START_HERE.md` and the Harness instructions. Treat the repository as the persistent source of truth. Before changing code, reconcile any contradictions between the master context, Harness, source, branches, CI and runtime evidence. Do not declare anything complete without verification.**

The new AI should then report:

1. current branch and commit;
2. current verified project state;
3. contradictions found;
4. relevant historical branches;
5. current feature gates;
6. validation gaps;
7. proposed next action.

---

## 18. FINAL PROJECT DNA

**StyloDSP is one project, not many APK projects.** Its history contains multiple Android generations, DSP experiments, a Web/TypeScript reference implementation, Rust Core work and Harness Engineering. The central engineering problem now is not simply adding features. It is reconciling the accumulated work into one verified, persistent, AI-readable project state.

The desired end state is:

```text
                 STYLO DSP
                     |
          +----------+----------+
          |                     |
      PROJECT MEMORY        SOURCE CODE
          |                     |
     Harness / Docs         Core / Apps
          |                     |
          +----------+----------+
                     |
                 EVIDENCE
                     |
              Build / Runtime
                     |
                 RELEASE
```

The repository must become capable of answering, without a historical chat:

**What is StyloDSP? What exists? What works? What was tried? Why was it built this way? What is experimental? What is planned? What is verified? What should the next AI do?**

That is the purpose of this document and of the Harness system around it.
