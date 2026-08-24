# STYLO HARNESS · MVP ROADMAP

## Phase 0 · Context lock
- [x] Harness baseline.
- [x] Product north star.
- [x] UI/design direction.
- [x] External reference patterns documented.
- [x] Portable AI context entry point.

## Phase 1 · H-002 audit
- [ ] Audit repository tree and branches.
- [ ] Reconcile Android workflow with actual Android/Core source.
- [ ] Locate or recover Rust Core if it exists elsewhere.
- [ ] Run Web tests/typecheck where available.
- [ ] Record executable baseline.
- [ ] Remove false assumptions from state.

## Phase 2 · First Android vertical slice
1. Mobile audio I/O.
2. Realtime FFT.
3. Spectrum renderer.
4. One parametric EQ filter.
5. EQ response curve renderer.
6. Touch node: frequency/gain.
7. Q gesture or precision control.
8. Integrated audio path.
9. Real-device latency/performance verification.
10. Reproducible APK.

## Phase 3 · Professional MVP
- Multiple bands.
- Filter types: bell, low/high shelf, HP/LP as justified.
- Precise numeric editing.
- Bypass and A/B.
- Presets.
- Band management.
- Better touch affordances.
- Performance profiling on target Android devices.

## Phase 4 · Expansion
- Advanced analyser features.
- Web/WASM parity.
- VST3 adapter.
- Optional Loop Engineering automation.
- Optional Graph Engineering orchestration.

## Gate rule
A phase cannot be considered complete without evidence stored in the harness. A visual prototype is not a functional audio proof; an APK build is not a realtime-audio proof; and passing unit tests are not by themselves a UX proof.
