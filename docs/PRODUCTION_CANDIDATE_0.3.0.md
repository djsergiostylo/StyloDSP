# STYLO EQ 0.3.0 Production Candidate

## Scope implemented before final build
- Realtime FFT using radix-2 instead of per-bin DFT.
- UI refresh throttled to ~30 FPS to avoid flooding the main thread.
- 31 fixed graphic EQ bands: 20 Hz–20 kHz.
- 8 parametric bands with frequency, gain, Q and filter type model.
- Direct graph node selection and drag editing.
- Bypass and A/B state.
- Flat/reset controls.
- Save/load preset in local SharedPreferences.
- Audio file picker for playback.
- Play/pause, ±10 s seek, loop, seek bar and volume.
- Playback audio session bridge to Android DynamicsProcessing when supported.
- Native DSP primitives: FFT, biquads and filter models for Peak, shelves, HP/LP, notch, band-pass, all-pass and tilt.
- Mobile bottom controls split into dedicated rows to prevent crowding.

## Important engineering boundary
The native Android playback bridge provides the production-safe 31-band graphic path on supported Android versions. The custom biquad engine is the reference/extension path for parametric filter processing and must be connected to the decoded PCM pipeline before the release gate is marked PASSING. UI-only parameter changes must never be described as audible DSP changes.

## Final release gates
1. Build compiles cleanly.
2. Unit/static verification passes.
3. APK exists and is non-empty.
4. App installs and launches on Redmi Note 9 Pro / Android 12.
5. Spectrum remains smooth under sustained audio.
6. 31-band edits are audible on playback when native bridge is available.
7. Parametric Q/type processing is audible through the custom PCM path.
8. Player controls work: open, play, pause, back, forward, seek, loop, volume.
9. Presets survive restart.
10. Bypass/A-B are audible and deterministic.
11. No crashes on permission denial, file error, lifecycle rotation/exit.
12. Release APK/AAB is reproducible.

## Rule
Do not compile the final APK until all implementation work above is complete. The first final build is a verification build, not the definition of completion.
