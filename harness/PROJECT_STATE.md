# STYLO DSP — PROJECT STATE

**State date:** 2026-08-25
**State type:** provisional, pending second deep reconciliation

## Current position

StyloDSP is in a repository-reconciliation and Android validation phase. The Android implementation is substantially beyond the original UI-only prototype, but the repository still contains historical branches and documentation from earlier architectural stages.

## Known implemented path on `main`

The current Android player engine contains a real audio path:

`MediaExtractor → MediaCodec → PCM → EQ → FFT/spectrum → SafetyLimiter → AudioTrack`

Known controls/features in the current engine include:

- load audio URI;
- play/pause;
- seek;
- loop;
- bypass;
- volume;
- dynamic EQ-band updates;
- per-channel EQ processing;
- FFT/spectrum callback;
- safety limiting;
- playback state/duration reporting.

## Build evidence

A GitHub Actions Android release artifact has been produced from `main` at commit `890a97a1d05448ff0d858aa46bb84fda8b4a4853`.

Artifact observed on 2026-08-25:
`stylo-eq-release-890a97a1d05448ff0d858aa46bb84fda8b4a4853`

Artifact size: approximately 2.23 MB.

## Runtime evidence

Runtime validation is not yet considered complete. Previous project testing exposed an Android native-library packaging/runtime issue involving `libstylo_dsp_core.so`. Therefore the current build must be installed and exercised before the release path can be called validated.

## Rust Core

A Rust-core direction exists in project history/branches and in architectural documentation. It is not currently the canonical DSP implementation in `main` and must not be described as integrated until source and runtime evidence confirm it.

## Repository condition

The repository contains multiple historical/prototype branches and documentation generations. Their unique value has not yet been fully reconciled. No branch should be deleted until the second audit records its unique commits/files/functionality.

## Current priority

1. Complete the second deep radiography.
2. Reconcile important branches against `main`.
3. Recover valuable historical work.
4. classify obsolete documentation.
5. establish real APK/runtime evidence.
6. update canonical Harness documents.
7. create one roadmap.
8. only then perform cleanup or production-code changes.

## Confidence

High confidence: current Android source/build observations.
Medium confidence: historical architecture and decisions recovered from previous project material.
Low/unknown: runtime behavior of the latest APK until tested on-device.
