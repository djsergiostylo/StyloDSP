# Realtime Gain Gate

## Objetivo

Añadir control de Gain desde la UI sin bloquear el hilo de audio y sin allocations durante `process()`.

## Diseño

```text
UI / Kotlin
   │
   │ parameter update
   ▼
Atomic parameter state
   │
   ▼
Audio callback
   │
   ▼
Rust DSP Core
   │
   ▼
Gain f32
```

Para cambios ordenados de parámetros se podrá utilizar posteriormente un SPSC ring buffer. No se presupone una implementación genérica de `AtomicF32`; debe elegirse una representación compatible con el target y validarse en CI.

## Gate

Antes de generar un APK candidato:

- [ ] Gain configurable desde UI.
- [ ] El audio callback no asigna memoria.
- [ ] El callback no usa locks bloqueantes.
- [ ] El valor de Gain se actualiza sin bloquear UI ni audio.
- [ ] Audio output permanece finito.
- [ ] Start/Stop repetido no produce crash.
- [ ] Tests Rust PASS.
- [ ] Android build PASS.
- [ ] Verificación ELF PASS.
- [ ] APK instalado y probado físicamente.

La rama `android-v0.2.0-validated` permanece como referencia estable y no debe modificarse con este trabajo experimental.
