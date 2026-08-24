# STYLO EQ · PRODUCT VISION v0.1

## North star
STYLO EQ is a mobile-first audio analysis and equalization tool. Its defining interaction is a realtime frequency spectrum with an EQ response curve drawn on the same graph, where the user edits EQ bands directly by touching and dragging nodes.

## MVP outcome
The first APK must prove one complete loop:

`audio → realtime FFT → spectrum renderer → interactive EQ node → DSP parameter change → audible result`

The MVP is successful when a user can reproduce that loop on a real Android phone without obvious UI stalls or unstable touch behavior.

## Product principles
1. The graph is the product, not decoration.
2. Spectrum and EQ curve share one coordinate system.
3. Direct manipulation is the primary interaction.
4. Numeric controls provide precision, not the main workflow.
5. The interface is dark, restrained and professional.
6. Performance beats visual effects.
7. The first release proves one vertical slice before expanding feature count.
8. Visual inspiration may come from professional EQ/DAW tools, but STYLO EQ uses original implementation, assets and identity.

## Visual direction
Primary reference blend:
- FabFilter Pro-Q: direct manipulation and information hierarchy.
- CamillaEQ: spectrum/curve/node interaction.
- Bitwig EQ+: graph-centered professional organization.
- audioMotion-analyzer: lightweight realtime spectrum rendering concepts.
- DSSSP: separation between EQ UI concepts and filter mathematics.

These are references for interaction and engineering patterns, not assets or code to copy.

## Target screen hierarchy
1. Header: project/preset/status controls.
2. Main graph: approximately 55–65% of usable screen height.
3. Selected-band detail strip: frequency, gain, Q, filter type.
4. Persistent audio controls: play/pause, bypass/monitoring and level/status.
5. Minimal secondary navigation.

## Interaction model
- Tap near a node: select band.
- Drag horizontally: frequency.
- Drag vertically: gain.
- Pinch or designated gesture: Q/bandwidth.
- Double tap: reset/disable or context action, to be validated in usability testing.
- Long press: contextual band/filter menu, if it proves discoverable without harming fast editing.

## MVP visual behavior
FFT is the live measurement layer. EQ curve is the control layer. Nodes are the direct manipulation layer. The selected node exposes precise values.

## Non-goals for MVP
- Full Pro-Q feature parity.
- Dozens of filters before the first vertical slice works.
- Heavy 3D effects, gradients or animated decoration.
- Large UI frameworks in the realtime graph path.
- Premature WebGL/GPU complexity unless profiling demonstrates the need.
