# STYLO DSP — VALIDATION PROTOCOL

## Principle

`BUILD SUCCESS != FUNCTIONAL SUCCESS`

Validation must progress from artifact creation to observed runtime behavior.

## Android validation chain

1. Build release/debug artifact.
2. Confirm artifact exists and is tied to a known commit.
3. Install on target/reference device.
4. Launch application.
5. Load supported audio.
6. Confirm playback.
7. Confirm EQ changes affect output.
8. Confirm bypass behavior.
9. Confirm seek behavior.
10. Confirm loop behavior.
11. Confirm volume behavior.
12. Confirm spectrum/FFT behavior if enabled.
13. Exercise pause/resume.
14. Exercise repeated load/seek/play cycles.
15. Run a stability session.
16. Record failures and logs.

## Evidence record

For each validation session record:

- date/time;
- commit SHA;
- build artifact;
- device/environment;
- test steps;
- result;
- logs/errors;
- known limitations.

## Release gate

Do not call an APK release-ready unless the critical runtime path has evidence beyond compilation.
