# STYLO Mastering Engine Web

Núcleo web del STYLO MASTERING ENGINE. La prioridad es el procesamiento real de audio antes de la UI avanzada.

## Pipeline actual

`WAV/AIFF -> decode -> AudioBuffer -> AudioEngine -> ChainManager -> DSP -> output`

## Módulos actuales

- `GainModule`: gain real en dB mediante `GainNode`.
- `EQModule`: filtro paramétrico basado en `BiquadFilterNode`.

## Motor

- `AudioEngine`: ciclo de vida del `AudioContext`, decodificación y reproducción.
- `ChainManager`: mantiene el orden de módulos, valida la cadena y crea el routing.
- `AudioModule`: contrato común para módulos DSP.

## Arquitectura prevista

```text
src/
├── audio-engine/
│   ├── AudioEngine.ts
│   ├── AudioModule.ts
│   └── ChainManager.ts
├── dsp/
│   ├── GainModule.ts
│   └── EQModule.ts
├── analysis/
├── validation/
└── export/
```

## Regla de evolución

No reescribir archivos completos si basta con un cambio localizado. Cada iteración debe inspeccionar el estado actual, modificar lo mínimo, comprobar el resultado y registrar la decisión en la memoria maestra.

## Próximo hito

Añadir una prueba automatizada del contrato y del routing de `GainModule`/`EQModule`. Después incorporar `OfflineAudioContext` para renderizar una cadena real antes de añadir más DSP.
