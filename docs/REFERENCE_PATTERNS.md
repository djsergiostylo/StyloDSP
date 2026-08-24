# STYLO EQ · REFERENCE PATTERNS

## Purpose
Record the external references that informed the product direction so future AI sessions understand *why* the UI is shaped this way.

## Selected references
### FabFilter Pro-Q
Use as a reference for direct graph editing, node-based EQ interaction and information hierarchy. Do not copy proprietary assets, code or visual identity.

### CamillaEQ
Use as a reference for an EQ graph where spectrum and filter response are edited together. Particularly relevant to the desired direct-manipulation workflow.

### Bitwig EQ+
Use as a reference for graph-centered organization, band identity and professional information density.

### audioMotion-analyzer
Use as a reference for lightweight realtime spectrum visualization using Web Audio/Canvas concepts and for thinking about performance before adding heavy rendering infrastructure.

### DSSSP
Use as a reference for separating EQ UI concerns from filter mathematics and for log-frequency response visualization.

## Engineering interpretation
These references are not dependencies. They define a target class of user experience and useful implementation patterns. STYLO must remain an original implementation with its own visual language.

## Selection rule
When evaluating a new UI technology or dependency, prefer the option that:
1. preserves realtime audio responsiveness;
2. minimizes runtime overhead;
3. keeps the DSP layer independent;
4. works reliably on the target Android hardware;
5. can be tested and maintained by multiple AI agents from the repository.
