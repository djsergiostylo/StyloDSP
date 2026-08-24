# STYLO EQ · AI CONTEXT

This file is the portable context entry point for any AI agent working on StyloDSP.

## Read first
1. `/AGENTS.md`
2. `/docs/STYLO_EQ_PRODUCT_VISION.md`
3. `/docs/STYLO_EQ_UI_SPEC.md`
4. `/docs/ARCHITECTURE.md`
5. `/harness/PROJECT.md`
6. `/harness/STATE.md`
7. `/harness/features.json`
8. `/harness/SESSION_HANDOFF.md`

## Current product
Build the first Android APK as a realtime audio spectrum + interactive EQ vertical slice.

## First demo
The user plays or loads audio. The spectrum moves in realtime. The EQ response curve is visible over the spectrum. A user taps a node and drags it horizontally/vertically to change frequency/gain. A gesture changes Q. The DSP output changes with the displayed parameters.

## Design decision
Use **STYLO AUDIO DARK / PRO**. The graph is the primary surface. Professional EQ/DAW products are references for interaction and hierarchy, not assets/code/identity to copy.

## Engineering decision
Keep DSP independent from Android/UI. Separate audio-rate processing, analysis and UI-rate rendering. Prefer low-allocation typed/native buffers and a lightweight graph renderer. Choose the actual rendering stack from the repository audit and measured performance, not preference.

## AI behavior
- Do not start by adding unrelated features.
- Do not assume Android/Core exists merely because a workflow references it.
- Do not claim APK/DSP readiness without executable evidence.
- If context conflicts, report the conflict and update the harness state rather than guessing.
- Make focused commits and update state/handoff after meaningful work.

## Priority
P0: H-002 audit → mobile audio → FFT → renderer → one EQ band → touch → integrated APK proof.
P1: multi-band, professional interaction, presets and usability improvements.
P2+: web/WASM parity, VST3 and advanced automation after the mobile vertical slice is stable.
