# Android Gain Realtime CI Trigger

This marker intentionally triggers the Android workflow for the `android-gain-realtime` branch.

Validation gate requested:

- Rust Core tests
- Android ARM64 build
- APK generation
- ELF SONAME/DT_NEEDED verification
- APK artifact upload

No production DSP behavior is changed by this marker.
