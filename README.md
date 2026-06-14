# StyloDSP

Prototipo Android de app DSP con ecualizador grafico de 31 bandas por software.

## Estado
- UI Android con Jetpack Compose.
- Selector de audio local con ExoPlayer.
- Motor DSP propio con 31 filtros Biquad IIR.
- AudioTrack para pruebas DSP.
- Preamp y limitador suave.

## Arquitectura objetivo
Archivo audio -> MediaExtractor/MediaCodec -> PCM Float -> SoftwareDspEngine -> AudioTrack -> Salida Android
