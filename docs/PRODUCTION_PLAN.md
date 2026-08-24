# STYLO EQ Production Implementation Plan

## Baseline
The first APK has been successfully exercised by the project owner. This version is the functional starting point for production work.

## Product target
A mobile audio analyzer/EQ where the realtime spectrum and EQ response curve share one graph and are directly editable by touch.

## Implementation order

### P0-A Audio foundation
- permission/lifecycle correctness
- input/output routing
- stable start/stop
- buffer sizing and sample-rate reporting
- no UI-thread audio work

### P0-B Analyzer
- FFT ring/buffer
- logarithmic 20 Hz-20 kHz mapping
- smoothing/peak-hold policy
- efficient graph rendering
- frame-rate independent rendering

### P0-C Parametric EQ
- reusable biquad coefficient engine
- multiple bands
- frequency/gain/Q ranges and clamping
- Peak, low/high shelf, low/high pass, notch, band-pass, all-pass and tilt where supported
- atomic parameter updates from UI to audio thread

### P0-D Graph editor
- selectable nodes
- drag X=frequency, Y=gain
- Q gesture
- add/delete/reset/bypass
- numeric precision controls
- visual separation of FFT and EQ curve

### P1 Graphic EQ
- 31 fixed bands from 20 Hz to 20 kHz
- presets and band management
- parametric/graphic mode switching without audio glitches

### P1 Persistence
- local presets
- project/session state
- safe schema versioning

### P1 Performance
- profile on real ARM64 device
- audio-thread allocation audit
- dropped-frame and underrun counters
- target smooth 60 fps UI without compromising audio stability

### Release gates
1. automated tests pass
2. debug APK builds reproducibly
3. real-device smoke test passes
4. audio lifecycle passes
5. FFT responds correctly
6. EQ parameter changes audibly/measurably affect the signal
7. touch interactions pass
8. no audio underruns in acceptance test
9. release APK/AAB builds reproducibly
10. rollback checkpoint and release notes recorded

## Harness rule
Do not mark a feature PASSING without evidence. Each completed block gets a checkpoint before the next risky change.
