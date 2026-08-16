# STYLO DSP Core

Primer núcleo Rust multiplataforma.

## Primer objetivo

Validar el camino mínimo:

```text
PCM buffer
   ↓
STYLO DSP Core
   ↓
Gain (f32)
   ↓
PCM buffer
```

El procesamiento es **in-place** y no realiza asignaciones durante `process()`.

## Estado

- Rust crate: creado.
- Gain `f32`: implementado.
- Tests unitarios: incluidos.
- Android adapter: pendiente.
- Web/WASM adapter: pendiente.
- VST3 adapter: pendiente.

La migración de módulos existentes será incremental y se hará con pruebas de paridad antes de retirar las implementaciones TypeScript de referencia.
