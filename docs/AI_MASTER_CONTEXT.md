# STYLO DSP — MASTER AI CONTEXT / PROJECT DNA

**Date:** 2026-08-25  
**Repository:** `djsergiostylo/StyloDSP`  
**Canonical development branch:** `main`  
**Documentation branch:** `docs/ai-project-memory`  
**Current phase:** H-002 / second deep repository radiography and reconciliation.

## 1. PROJECT IDENTITY

StyloDSP is one evolving DSP/audio project, not a collection of unrelated applications. Its history contains multiple Android generations, EQ/audio experiments, native/Rust experiments, Web/TypeScript reference DSP, CI/APK checkpoints and Harness Engineering.

The long-term target is a reusable DSP Core with platform adapters. The Core should own DSP algorithms; Android/Web/VST3 should handle host/platform concerns.

The immediate engineering goal is **not feature expansion**. It is to reconcile accumulated work into one verified, persistent project state so future AIs can continue without requiring historical chat access.

## 2. CURRENT REPOSITORY SHAPE

The current `main` contains Android, Web, docs, Harness and CI-related material. The repository also contains many historical/prototype branches.

The Harness is the operational control layer for AI work. `AGENTS.md` requires evidence before PASSING, small reversible changes, platform-independent Core boundaries and preservation of historical work until its value is known.

## 3. CURRENT `main` ANDROID PATH

The current Android implementation on `main` contains a real PCM-oriented player/DSP path:

`MediaExtractor → MediaCodec → PCM → EQ → FFT/spectrum → SafetyLimiter → AudioTrack`

Known current engine capabilities include:

- audio URI loading;
- play/pause;
- seek;
- loop;
- bypass;
- volume;
- dynamic EQ-band updates;
- per-channel EQ processing;
- FFT/spectrum callback;
- safety limiting;
- playback state/duration.

Current CI evidence includes an Android release artifact produced from `main` at commit:

`890a97a1d05448ff0d858aa46bb84fda8b4a4853`

Observed artifact name:

`stylo-eq-release-890a97a1d05448ff0d858aa46bb84fda8b4a4853`

Approximate artifact size: 2.23 MB.

## 4. CRITICAL GENERATION DISTINCTION

There are at least three materially different Android generations in project history.

### Generation A — Historical V0.2.0 Android/Rust/Gain milestone

Branch:

`android-v0.2.0-validated`

Reference implementation branch:

`prototype/android-apk`

Its documented architecture was:

`Android → Oboe/AAudio → Native bridge/FFI → STYLO DSP Core (Rust) → Gain (f32)`

The branch contains an explicit **physical-device validation record**. It documents a real `UnsatisfiedLinkError` caused by an ELF dependency using an absolute GitHub Actions runner path. The fix established `SONAME = libstylo_dsp_core.so` and `DT_NEEDED = libstylo_dsp_core.so`, and CI checked that runner paths were absent. It records build commit `85f4bdbf6030cbc3d83903874c0e01e033bb80fb`, Actions run `31965884079`, and `arm64-v8a` testing.

**Interpretation:** V0.2.0 was a genuinely device-validated Android/Rust/Gain milestone. This is historical evidence and must be preserved. It is NOT proof that the latest `main` APK uses this architecture or is already runtime-validated.

### Generation B — Historical full-EQ Android generation

Branch:

`prod/full-eq-v1`

This line contains an older Android EQ/UI/FFT implementation, including `AudioEqProcessor.kt`, `EqModel.kt`, `FastFft.kt` and a different `MainActivity.kt` generation.

It is valuable historical EQ/UI work but should not be mixed into the current PCM implementation without evidence.

### Generation C — Current `main` Kotlin/PCM/DSP generation

This is the current active Android path. It is newer and more feature-rich, but its latest runtime behavior still requires explicit current-device validation.

## 5. CURRENT RUNTIME STATUS

**Do not say “the latest APK is validated.”**

The current `main` build has CI/build evidence, but current-device runtime validation is still incomplete.

Historical native-library failures exist, but they belong to earlier milestones unless current testing proves the same issue exists in the latest build.

Reference-device context includes Redmi Note 9 Pro-specific UI/layout work.

## 6. RUST CORE

Rust is real project history, not merely a future idea.

Branch:

`core-rust`

The branch is three commits ahead of the shared merge base and substantially behind current `main`.

Its three unique commits establish:

1. `23ce61d5...` — Rust DSP Core scaffold with `core/Cargo.toml`, package `stylo-dsp-core`, `rlib` and `cdylib` outputs.
2. `e3357e4e...` — in-place `f32` `Gain` processor plus unit tests.
3. `3badc0f9...` — prototype README defining PCM → Core → Gain → PCM and stating that Android/Web/VST3 adapters were still pending.

The historical Core is therefore valuable and should be preserved/recovered selectively.

However, **Rust is not the canonical DSP implementation in current `main` unless current source, build and runtime evidence prove integration.**

## 7. VERSION / BRANCH / APK MODEL

These concepts must remain separate:

```text
Product version
    V0.x.x

Git branch
    feature/...
    experiment/...
    archive/...

Commit
    exact source state

APK/AAB
    build artifact from a commit

Validation
    evidence for that artifact
```

A branch name such as `android-v0.2.0-validated` represents a historical milestone; it does not mean every later Android state is V0.2.0.

Do not rename historical branches until their genealogy has been reconstructed.

## 8. HARNESS MODEL

Harness is the operational control layer, not merely a status file.

A new AI should follow:

`AI_START_HERE.md`
→ `AI_CONTEXT.md`
→ `docs/AI_MASTER_CONTEXT.md`
→ `harness/INSTRUCTIONS.md`
→ `harness/PROJECT_STATE.md`
→ `harness/STATE.md`
→ `harness/ARCHITECTURE.md`
→ `harness/ROADMAP.md`
→ `harness/DECISION_LOG.md`
→ latest audits
→ `harness/VALIDATION_PROTOCOL.md`
→ source/tests/CI.

The current canonical decision is that Harness is the persistent project-memory/control layer for AI agents.

## 9. EVIDENCE MODEL

Use this hierarchy:

`E0 Assumption → E1 Documentation → E2 Source → E3 Build → E4 Runtime → E5 Reproducible validation`

Rule:

**BUILD SUCCESS ≠ FUNCTIONAL SUCCESS**

For Android the eventual release validation chain is:

`Build → APK → Install → Launch → Load → Play → DSP audible → EQ → FFT → Seek → Bypass → Loop → Volume → Stability → logs/evidence`

## 10. CURRENT FEATURE STATE

The current evidence-driven feature matrix is:

- H-001 Harness baseline: PASSING.
- H-002 Repository reconciliation and executable verification baseline: IN_PROGRESS.
- AND-002 Smooth realtime analyzer: IN_PROGRESS.
- EQ-031 31-band graphic EQ: IN_PROGRESS.
- EQ-PARAM Parametric EQ: IN_PROGRESS.
- PLAYER-001 Integrated audio player: IN_PROGRESS.
- PRESET-001 Presets/persistence: IN_PROGRESS.
- UX-001 Mobile professional UI: IN_PROGRESS.
- REL-001 Production release: NOT_STARTED.

H-002 was intentionally downgraded from an older PASSING claim because the previous state documents were stale and current runtime evidence is incomplete.

## 11. RECONCILIATION PROTOCOL

Before major coding:

1. Audit `main`.
2. Compare important branches.
3. Identify valuable branch-only work.
4. Identify obsolete documentation.
5. Reconstruct real APK/runtime status.
6. Reconcile Harness and canonical state.
7. Create one roadmap.
8. Identify dead/duplicate/incomplete work.
9. Reconcile available ChatGPT history with GitHub.
10. Review CI/CD.
11. Review tests/evidence.
12. Establish validation gaps.
13. Classify branches.
14. Establish acceptance criteria.
15. Only then change production architecture/code.

Operational sequence:

**OBSERVE → COMPARE → CLASSIFY → DOCUMENT → VALIDATE → CLEAN → DEVELOP**

## 12. CURRENT BRANCH INVENTORY

High-value branches already inspected:

### `core-rust`
`KEEP / RECOVER SELECTIVELY`

Real Rust Core/Gain engineering; small initial slice, not a full replacement.

### `android-v0.2.0-validated`
`KEEP AS HISTORICAL VALIDATION REFERENCE / RECOVER SELECTIVELY`

Real physical-device Android/Rust/Gain validation and ELF packaging fix.

### `android-gain-realtime`
`KEEP / INVESTIGATE DEEPLY`

Substantial Android native/Rust/gain line with dedicated workflow, native bridge, Core and validation/audit docs.

### `prod/full-eq-v1`
`ARCHIVE / EXTRACT SELECTIVELY`

Older full-EQ/UI/FFT generation.

### `v0.2.1-stylo-eq-mvp`
Historical MVP line containing product vision, UI spec, production planning and Harness/version material. Audit for reusable product/documentation decisions.

### `v0.2.2-first-apk-mvp`
Historical APK/MVP line containing Android/native scaffolding, Core beginnings and older AI/Harness context. Audit for historical decisions and genealogy.

Other prototype/checkpoint/CI branches must remain preserved until their unique value is classified.

Detailed current branch evidence is stored in:

`harness/audits/BRANCH-INVENTORY-2026-08-25.md`

## 13. WHAT A NEW AI MUST NOT DO

- Do not treat branches as separate products.
- Do not treat every APK as a different application.
- Do not assume historical documentation is current.
- Do not assume Rust is integrated because it exists in history.
- Do not merge historical Android/Rust lines blindly.
- Do not delete branches before inventory.
- Do not mark PASSING because code merely compiles.
- Do not create a second roadmap.
- Do not rewrite working architecture without evidence.
- Do not silently erase historical contradictions.

## 14. CURRENT NEXT ACTION

The remaining work is:

1. full tree/file-size audit;
2. cross-reference audit;
3. remaining Harness path audit;
4. compare remaining historical branches;
5. recover valuable unique code and decisions;
6. reconstruct latest APK/runtime status;
7. reconcile all available ChatGPT project history;
8. finalize V0.x.x genealogy;
9. generate final local ZIP;
10. review a clean merge into `main`.

No broad production-code refactor should begin before these gates are satisfied.

## 15. CHATGPT HISTORY LIMIT

This document uses the durable StyloDSP context currently available in ChatGPT plus verified GitHub evidence. It does **not** claim automatic exhaustive access to every private ChatGPT chat/project transcript.

A future full-history pass should extract decisions/features/requirements from any additional accessible chats, deduplicate them, classify them as current/historical/planned/discarded, and cross-check them against GitHub before becoming canonical.

## FINAL PRINCIPLE

A new AI should be able to answer from the repository alone:

**What is StyloDSP? What exists? What is historical? What works? What was validated? What is planned? What decisions were made? What must happen next?**

That is the purpose of the project memory + Harness system.
