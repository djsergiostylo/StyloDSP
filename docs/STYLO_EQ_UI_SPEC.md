# STYLO EQ · UI SPEC v0.1

## Design language
**STYLO AUDIO DARK / PRO**

A restrained professional audio interface optimized for readability, touch precision and low rendering cost.

## Style recipe
- 40% direct-manipulation principles from professional EQs such as Pro-Q.
- 25% CamillaEQ-style graph interaction.
- 15% Bitwig-style organization.
- 10% lightweight analyser/rendering principles inspired by audioMotion.
- 10% original STYLO visual language.

## Palette roles
Use semantic roles rather than a decorative rainbow:
- Graph background: near-black graphite.
- Grid: low-contrast charcoal/gray.
- FFT: subdued cyan/blue so it reads as measurement.
- EQ curve: high-contrast light/cyan so it reads as control.
- Band nodes: distinct accent colors used only for band identity/state.
- Selected node: stronger accent and visible halo.
- Text: high-contrast primary plus muted secondary.

Exact color values are intentionally not frozen until the first device prototype.

## Graph
- Occupy roughly 55–65% of the usable screen area.
- Frequency axis: logarithmic, target 20 Hz–20 kHz.
- Magnitude axis: dB, with stable labels and sensible dynamic range.
- FFT and EQ response are drawn in the same graph coordinate system.
- Avoid unnecessary shadows, blur, 3D effects and continuous DOM layout work.

## Rendering architecture
Preferred first implementation:

`audio data → Float32/typed buffers → FFT → Canvas/native drawing surface`

The graph renderer should avoid React/DOM/SVG animation loops in the realtime path if profiling shows they create avoidable overhead. The final technology is decided by the actual Android stack after H-002 audit.

## Interaction
### Node
- Horizontal drag = frequency.
- Vertical drag = gain.
- Q gesture = bandwidth/Q.
- Tap = select.
- Selected node shows exact values.

### Precision
Touch editing must have a precision mode or numeric fallback. The user must be able to set exact frequency, gain and Q values.

### Audio controls
Keep play/pause, bypass and essential level/status controls persistently accessible without shrinking the graph excessively.

## States to prototype
1. Empty analyser.
2. One EQ band.
3. Six EQ bands.
4. Selected band with values.
5. Band being dragged.
6. Q gesture.
7. Bypass comparison.
8. Audio stopped/error state.

## Performance rules
- No avoidable allocations in the audio callback.
- Do not recompute expensive filter curves more often than necessary.
- Separate audio-rate processing from UI-rate rendering.
- Throttle or decimate visual updates only if needed after measurement.
- Measure before introducing WebGL or other complexity.
