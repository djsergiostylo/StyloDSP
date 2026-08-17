# STYLO DSP CORE — DSP Dependency Audit

## Objetivo

Reducir trabajo de implementación reutilizando librerías Rust maduras, sin introducir dependencias en el callback realtime hasta comprobar seguridad realtime, licencia, ARM64/NEON, Web/WASM y compatibilidad futura con VST3/CLAP.

## Decisiones

### Adoptar ahora

- **dasp**: candidato principal para primitivas PCM/DSP de bajo nivel. Su proyecto declara herramientas de alto rendimiento y sin asignaciones dinámicas en sus crates fundamentales. Se incorpora inicialmente como dependencia **opcional**, para migrar primitivas de forma controlada y medirlas antes de introducirlas en el camino realtime.

### Evaluar antes de adoptar

- **rustfft**: reservado para análisis espectral, FFT/STFT y futuras funciones de EQ/convolución. No se introduce todavía en el callback de audio.
- **FunDSP**: interesante para composición de grafos DSP, pero cada componente debe auditarse por allocations y coste realtime antes de usarlo en el core.
- **CPAL**: útil como abstracción multiplataforma de I/O y especialmente interesante para Web/WASM, pero Android seguirá utilizando Oboe como backend nativo de baja latencia.
- **NIH-plug / Clack**: se evaluarán en un adaptador independiente para VST3/CLAP. No deben contaminar el núcleo DSP portátil. NIH-plug requiere una revisión específica de licencia para VST3 antes de adoptarlo en una distribución comercial.

## Regla de integración

Una dependencia DSP solo puede entrar en el camino realtime después de pasar:

1. revisión de licencia;
2. revisión de dependencias transitivas;
3. comprobación de allocations en `process()`;
4. comprobación de locks y operaciones bloqueantes;
5. compatibilidad `f32`;
6. ARM64 Android;
7. posibilidad de vectorización/SIMD;
8. WASM cuando sea aplicable;
9. benchmark frente a la implementación STYLO;
10. tests de estabilidad y audio.

## Arquitectura resultante

```text
STYLO DSP CORE
├── Module API
├── Parameter Engine
├── Graph / Routing
├── Presets / State
├── Metering
└── Realtime Scheduler
       │
       ├── STYLO DSP implementations
       └── audited external primitives
              ├── dasp
              ├── rustfft (analysis)
              └── other audited crates

Adapters
├── Android → Oboe / AAudio
├── Web → WASM / Web Audio
└── Plugin → VST3 / CLAP adapter
```

## Estado

- Gain realtime: implementado y probado físicamente.
- `dasp`: preparado como dependencia opcional; todavía no se fuerza dentro del callback.
- FFT/análisis: pendiente de migración y benchmark.
- Plugin adapter: pendiente.
- Web adapter: pendiente.

## Fuentes consultadas

- crates.io — categoría Multimedia::Audio: https://crates.io/categories/multimedia::audio
- dasp: https://github.com/RustAudio/dasp
- CPAL: https://github.com/RustAudio/cpal
- Oboe: https://github.com/google/oboe
- NIH-plug: https://github.com/robbert-vdh/nih-plug
- Clack: https://github.com/prokopyl/clack
