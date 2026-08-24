# STYLO EQ 0.3.x - Finalization Checklist

This document is the release gate. The project is not considered production-ready until every item is verified on a physical Android device and the final APK/AAB builds pass CI.

## Core DSP
- [x] Radix-2 FFT with precomputed twiddles
- [x] Persistent FFT buffers
- [x] 31 graphic EQ bands
- [x] 8 parametric bands
- [x] Biquad Peak, shelves, HP, LP, notch, band-pass and all-pass
- [x] Stereo filter state isolation
- [x] DSP bypass path
- [ ] Audibly verify every filter type
- [ ] Verify Q/frequency/gain changes against reference calculations
- [ ] Replace hard clipping with a production-grade transparent limiter/soft clip stage

## Player / PCM
- [x] MediaExtractor track selection
- [x] MediaCodec decoding
- [x] PCM streaming through AudioTrack
- [x] Seek + decoder flush
- [x] Loop handling
- [x] Reusable PCM scratch buffer
- [ ] Validate AAC/MP3/WAV/FLAC device compatibility
- [ ] Validate sample rates 44.1/48/96 kHz
- [ ] Validate mono/stereo transitions
- [ ] Remove allocations from the control-to-DSP update path
- [ ] Verify parameter changes do not create audible clicks

## Analyzer / UI
- [x] 20 Hz-20 kHz logarithmic display
- [x] Touch frequency/gain editing
- [x] Q gesture in parametric mode
- [x] 31-band / parametric mode switching
- [x] Persistent transport controls
- [x] Mobile bottom-control rows
- [x] Play/pause/seek/loop/volume
- [x] A/B, bypass, flat/reset, save/load
- [ ] Verify analyzer does not contend with audio thread on-device
- [ ] Verify 60 Hz UI rendering target with bounded analyzer updates
- [ ] Accessibility / touch target audit

## Release gates
- [ ] Static source review
- [ ] Unit tests for FFT and biquad coefficients
- [ ] Instrumented smoke test on Android
- [ ] Clean Gradle build
- [ ] APK existence + non-zero size
- [ ] APK install verification
- [ ] Manual audio playback verification
- [ ] Performance verification
- [ ] Final APK
- [ ] Final AAB

**Important:** no release APK should be presented as final until all unchecked verification gates above have passed.
