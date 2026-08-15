# STYLO Mastering Engine Web

Primer núcleo web del proyecto STYLO MASTERING ENGINE.

## Objetivo de esta fase

Construir el motor de audio antes de la interfaz avanzada.

Pipeline inicial:

`WAV/AIFF -> decode -> AudioBuffer -> AudioEngine -> módulos DSP -> salida`

## Primer módulo

`GainModule` usa Web Audio API y convierte dB a ganancia lineal. El bypass conserva la señal a ganancia unitaria.

## Arquitectura

- `src/audio-engine/` motor y contratos de módulos.
- `src/dsp/` procesamiento DSP.
- `src/analysis/` análisis independiente.
- `src/validation/` validación de parámetros.
- `src/export/` render offline y WAV.

## Regla

No añadir UI avanzada hasta verificar el procesamiento real de cada módulo.
