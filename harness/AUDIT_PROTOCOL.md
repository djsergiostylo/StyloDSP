# STYLO DSP — AUDIT PROTOCOL

## Objective

Determine what is true about the project now, using evidence rather than assumptions.

## Evidence hierarchy

1. Current source code
2. Reproducible test/runtime evidence
3. CI/build artifacts
4. Current Harness state
5. Current architecture/decisions
6. Historical commits and branches
7. Historical AI conversations and old documents

## Audit rules

- Never mark a feature complete from prose alone.
- Never treat CI success as runtime success.
- Record commit SHA for important build evidence.
- Record device/environment for manual validation.
- Distinguish current code from experimental branches.
- Preserve contradictory evidence until resolved.

## Feature status vocabulary

`IMPLEMENTED`
`BUILT`
`RUNTIME-VALIDATED`
`PARTIAL`
`PLANNED`
`EXPERIMENTAL`
`OBSOLETE`
`UNKNOWN`

## Minimum audit matrix

| Feature | Source | Main | Build | Runtime | Evidence | Status |
|---|---|---|---|---|---|---|
| Android app | | | | | | |
| PCM playback | | | | | | |
| EQ | | | | | | |
| FFT/spectrum | | | | | | |
| Seek | | | | | | |
| Bypass | | | | | | |
| Loop | | | | | | |
| Volume | | | | | | |
| Native/Rust core | | | | | | |
| Release packaging | | | | | | |

Fill unknown cells rather than guessing.
