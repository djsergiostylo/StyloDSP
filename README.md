# StyloDSP

StyloDSP es un proyecto Android orientado a crear una app de ecualizacion y mejora de audio basada en DSP por software.

El objetivo no es depender del ecualizador limitado de Android, sino construir progresivamente un motor propio capaz de procesar audio PCM con filtros matematicos reales.

## Objetivo principal

Crear una aplicacion Android con:

- Ecualizador grafico de 31 bandas.
- Motor DSP por software.
- Filtros Biquad IIR.
- Preamp.
- Limitador.
- Analizador FFT.
- Presets.
- Importacion futura de curvas AutoEQ.
- Evolucion futura hacia FIR Convolution e Impulse Responses.

## Estado actual del proyecto

Estado: prototipo inicial.

Incluido en la primera base:

- Proyecto Android en Kotlin.
- UI con Jetpack Compose.
- Pantalla inicial.
- Pantalla de ecualizador de 31 bandas.
- Modelo de bandas de frecuencia.
- ViewModel para controlar ganancias.
- Base de motor DSP por software.
- Filtro Biquad IIR inicial.
- Prueba prevista con AudioTrack.

## Arquitectura objetivo

```text
Archivo de audio
    -> Decodificacion PCM
    -> Buffer Float
    -> SoftwareDspEngine
    -> 31 filtros Biquad IIR
    -> Preamp
    -> Limitador
    -> AudioTrack / salida Android
```

## Por que DSP por software

La clase nativa `android.media.audiofx.Equalizer` depende del dispositivo y no garantiza 31 bandas reales. Algunos moviles solo exponen 5, 10 o un numero limitado de bandas.

StyloDSP debe usar el ecualizador nativo solo como modo de compatibilidad. El objetivo profesional es procesar el audio en software para que las 31 bandas funcionen de forma consistente.

## Stack tecnico previsto

- Kotlin.
- Jetpack Compose.
- Material 3.
- Android AudioTrack.
- MediaExtractor / MediaCodec para decodificacion futura.
- Filtros Biquad IIR.
- ViewModel.
- StateFlow.
- Room para presets futuros.

## Fases de desarrollo

### Fase 1: MVP visual

- Crear proyecto Android.
- Crear UI principal.
- Crear pantalla EQ 31 bandas.
- Controlar bandas desde ViewModel.

### Fase 2: DSP real

- Crear SoftwareDspEngine.
- Encadenar 31 filtros Biquad.
- Procesar audio PCM Float.
- Aplicar preamp.
- Aplicar limitador.

### Fase 3: Reproduccion funcional

- Decodificar archivos reales a PCM.
- Enviar audio procesado a AudioTrack.
- Mantener sincronizacion y estabilidad.

### Fase 4: Herramientas profesionales

- FFT en tiempo real.
- RMS Meter.
- Peak Meter.
- Presets.
- Importar/exportar configuraciones.

### Fase 5: Funciones avanzadas

- AutoEQ.
- Curvas Harman.
- FIR Convolution.
- Impulse Responses.
- Crossfeed.
- Correccion de sala.

## Limitaciones actuales

- Todavia no es una app final lista para Play Store.
- No procesa todo el audio global del sistema.
- No garantiza baja latencia hasta implementar correctamente el pipeline PCM.
- Bluetooth puede introducir latencia adicional.
- El procesamiento global tipo Wavelet puede requerir permisos, sesiones de audio o restricciones especificas de Android.

## Enfoque tecnico

El proyecto debe priorizar precision sobre efectos llamativos. Cada funcion debe tener una base DSP clara:

- Ganancia en dB.
- Frecuencia central.
- Q / ancho de banda.
- Filtros IIR.
- Limitacion de clipping.
- Analisis espectral.

## Licencia

Pendiente de definir.

Recomendacion inicial: MIT si se busca maxima facilidad de uso y contribucion.
