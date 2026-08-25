# STYLO-EQ Android Audio UI Architecture Guide

## Purpose

Reference architecture for the STYLO-EQ Android audio player, spectrum analyzer and graphic/parametric EQ UI. This guide favors code that remains responsive on phones such as the Redmi Note 9 Pro and scales to other Android window sizes.

## 1. UI architecture

Prefer Jetpack Compose for new UI work when practical. Android's current adaptive guidance recommends Compose and Material 3 Adaptive for layouts that respond to window size, orientation and form factor.

Useful primitives:
- `Column`, `Row`, `Box`, `LazyColumn` for predictable composition.
- `Modifier.weight()` for proportional regions instead of fixed pixel heights.
- `WindowSizeClass` / adaptive APIs for compact, medium and expanded layouts.
- Material 3 components for consistent touch targets and accessibility.

For STYLO-EQ, keep the audio/DSP engine independent from UI state. The UI should observe state and send user commands to a controller/view-model layer.

## 2. Recommended STYLO-EQ screen structure

Vertical compact phone:
1. App/header area: small fixed region.
2. Track/file information and transport controls.
3. Spectrum + EQ editor: roughly 50-60% of available height, but measured dynamically.
4. EQ controls: scrollable/adaptive region using rows or grids.
5. Master/utility controls at the bottom.

Do not position controls with absolute coordinates. Avoid hard-coded screen-pixel assumptions. Use `dp`, weights, constraints and window measurements.

For larger/landscape windows, allow the EQ/spectrum and controls to become side-by-side when useful.

## 3. Audio player architecture

Android Media3 provides the modern `Player` abstraction for play, pause, seek, playlist, repeat/shuffle, speed and volume. `ExoPlayer` is the standard implementation for general media playback.

STYLO-EQ currently has a custom audio path/DSP engine. Do not replace it merely to change the UI. Media3 is a reference for player state, commands and lifecycle separation. If Media3 is introduced later, keep DSP processing in its own layer and connect it through a well-defined audio pipeline.

For background playback, Media3 recommends `MediaSession` with a `MediaSessionService`/`MediaLibraryService` where appropriate.

## 4. Spectrum and EQ rendering

The spectrum graph and EQ curve are performance-sensitive custom rendering. Keep FFT/DSP calculations off the main/UI thread and publish only the data needed to render each frame.

For a custom Android view, use `Canvas`/custom drawing with careful allocation control. Avoid creating objects or large arrays on every frame. Reuse FFT buffers, paths and paint objects where possible.

For Compose, isolate the high-frequency visualizer into a dedicated drawing composable (`Canvas`) and feed it compact immutable/efficient frame data. Do not cause the whole control panel to recomcompose at audio/FFT frame rate.

Target smooth rendering by throttling visual updates to the display frame rate rather than pushing every DSP sample/update to the UI.

## 5. Touch interaction

EQ nodes need generous hit targets even when their visual circles are small. Use a larger invisible interaction region around each node. Keep gestures deterministic: drag = frequency/gain, pinch/other gestures only if explicitly needed.

Transport and EQ buttons should have comfortable touch targets and enough spacing to prevent accidental adjacent presses.

## 6. Adaptive layout rules for STYLO-EQ

Use these rules as defaults, not immutable numbers:
- Never let the spectrum consume all available height.
- Give the bottom controls a meaningful fraction of the screen on compact phones.
- Use a scrollable control area if controls cannot fit without shrinking touch targets.
- Prefer 2-5 controls per row depending on measured width.
- Use minimum touch sizes and spacing rather than squeezing labels.
- Detect orientation/window size and change composition when necessary.
- Account for edge-to-edge system bars and insets, especially on API 35+.

## 7. Performance rules

- DSP thread: audio processing only; never block it with UI work.
- UI thread: rendering and interaction only; never perform FFT/audio decoding there.
- Exchange data through lock-free/minimal-copy or carefully synchronized structures as appropriate.
- Reuse arrays and drawing resources.
- Avoid logging per audio buffer or per animation frame in release builds.
- Measure before optimizing. Use Android Studio Profiler and frame/render timing to identify actual bottlenecks.

## 8. Compatibility decision for this project

Compatible and useful now:
- Jetpack Compose/adaptive layout principles for future UI refactoring.
- Material 3 component/touch-target guidance.
- Android Canvas/custom drawing principles for spectrum/EQ.
- Media3 `Player`/`MediaSession` architecture as a reference for player state and external controls.
- Edge-to-edge/insets handling for modern Android.

Do not introduce a large dependency migration solely for documentation purposes. The existing custom DSP/audio engine should remain stable while the UI is incrementally improved.

## Official references

- Adaptive apps: https://developer.android.com/develop/ui/compose/build-adaptive-apps
- Compose layouts: https://developer.android.com/develop/ui/compose/layouts
- Different display sizes: https://developer.android.com/develop/adaptive-apps/guides/support-different-display-sizes
- Adaptive do's and don'ts: https://developer.android.com/develop/adaptive-apps/guides/adaptive-dos-and-donts
- Media3 Player: https://developer.android.com/media/media3/session/player
- Media3 ExoPlayer: https://developer.android.com/media/implement/playback-app
- MediaSession: https://developer.android.com/media/media3/session/control-playback
- Media3 releases: https://developer.android.com/jetpack/androidx/releases/media3
- Compose releases: https://developer.android.com/jetpack/androidx/releases/compose
- Edge-to-edge: https://developer.android.com/develop/ui/compose/system/setup-e2e

## Implementation principle

STYLO-EQ should evolve toward a separation of concerns:

`Audio/DSP Engine -> Player/State Controller -> UI State -> Adaptive UI -> Spectrum/EQ Renderer`

The renderer must never own audio processing, and audio processing must never depend on UI timing.
