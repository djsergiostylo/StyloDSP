# STYLO HARNESS · CURRENT CONTEXT

**Effective version:** v0.2-harness-pilot · Product/UI context lock
**Updated:** 2026-08-24

This file is the current context override for the pilot. If older harness notes conflict with this file, this file wins unless a newer dated document says otherwise.

## Product decision
Build the first Android APK around one core experience: realtime frequency spectrum + interactive EQ on the same graph.

## UX decision
Adopt **STYLO AUDIO DARK / PRO**: graph-first, professional, restrained, touch-optimized, low-rendering-overhead. Use professional EQ/DAW products as interaction references only. Do not copy their code, assets or identity.

## Reference blend
FabFilter Pro-Q principles for direct editing; CamillaEQ for spectrum/curve/node interaction; Bitwig EQ+ for organization; audioMotion for lightweight analyser concepts; DSSSP for UI/filter separation.

## MVP sequence
H-002 audit → audio I/O → FFT → spectrum → one EQ band → EQ curve → touch frequency/gain → Q control → integrated audio → real-device performance → reproducible APK.

## Architectural rule
Keep DSP/audio processing independent from UI. Separate audio-rate processing from UI-rate rendering. Choose the renderer after profiling. Canvas/native drawing is the default low-overhead direction; WebGL is optional only if measured need exists.

## AI continuity rule
Any AI agent (ChatGPT, Gemini, Codex or compatible agent) must read this file and `docs/AI_CONTEXT.md` before changing product architecture or UI. The repository is the persistent source of truth; chat history is not.

## Completion rule
No feature is PASSING without acceptance criteria and evidence. Never infer Android/Core readiness from a workflow file alone.
