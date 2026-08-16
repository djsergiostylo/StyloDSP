# StyloDSP

Motor DSP modular para procesamiento de audio y futura aplicación de mastering configurable.

> **Estado:** transición arquitectónica hacia un **STYLO DSP CORE multiplataforma**. El objetivo es implementar el procesamiento una sola vez y poder alojarlo posteriormente en Web, Android y VST3.

## Objetivo

Construir un núcleo DSP independiente de plataforma capaz de procesar audio PCM mediante módulos independientes, configurables y encadenables.

El mismo Core será la base de:

```text
                 STYLO DSP CORE
                      │
          ┌───────────┼───────────┐
          ↓           ↓           ↓
         WEB       ANDROID       VST3
          │           │           │
       WASM/JS     Native/FFI    Native
          │           │           │
         WEB          APK         DAW
```

Web, Android y VST3 son **hosts/adaptadores**, no implementaciones independientes de los algoritmos DSP.

## Arquitectura objetivo

La especificación completa está en [`docs/ARCHITECTURE.md`](docs/ARCHITECTURE.md).

```text
┌──────────────────────────────────────────────┐
│              STYLO DSP CORE                 │
│                   Rust                       │
├──────────────────────────────────────────────┤
│ DSP algorithms · modules · chain · state     │
│ parameters · presets · analyzer · metering   │
│ f32 · selective f64 · SIMD · realtime rules  │
└───────────────────────┬──────────────────────┘
                        │
          ┌─────────────┼─────────────┐
          ↓             ↓             ↓
      Web/WASM       Android/FFI      VST3
          │             │             │
    AudioWorklet     Oboe/AAudio       DAW
          │             │             │
         WEB            APK            VST3
```

### Principios de plataforma

- `core/` no debe depender de Web Audio, Android, Kotlin, React ni VST SDK.
- DSP general en `f32`.
- `f64` únicamente donde las pruebas demuestren beneficio numérico.
- SIMD mediante una abstracción común con objetivo ARM NEON, WASM SIMD y fallback escalar.
- Cero asignaciones de memoria durante el procesamiento realtime.
- Parámetros UI → DSP mediante atomics y eventos SPSC lock-free cuando corresponda.
- Metering mediante snapshots consultados por la UI a 30/60 FPS.
- Protección contra valores subnormales/denormales.
- Audio Android mediante Oboe/AAudio con preferencia por baja latencia y fallback seguro.

## Módulos DSP actuales

### Gain

Control de ganancia independiente y configurable. Será el primer módulo migrado al nuevo Core Rust.

### EQ

Base de ecualización DSP preparada para parámetros editables.

### Compressor

Compresión dinámica con metadata de parámetros para integración con la futura interfaz.

### Saturation

Módulo de saturación configurable y validado mediante tests.

### Clipper V1

Incluye:

- Hard / Soft;
- Threshold;
- Drive;
- Ceiling;
- Mix;
- Oversampling configurable;
- bypass;
- validación y serialización.

### Limiter V1

El módulo dispone de:

- Threshold;
- Ceiling;
- Release;
- Lookahead;
- Gain;
- Mix;
- True Peak como parámetro de configuración;
- Oversampling como parámetro de configuración.

Existe un núcleo DSP determinista (`LimiterDsp`) que verifica reducción de ganancia, ceiling, lookahead y salida finita.

**Importante:** True Peak y oversampling todavía no deben considerarse implementaciones DSP finales. Están preparados en el contrato y requieren una fase posterior de procesamiento real y validación específica.

### Analyzer

Infraestructura de análisis y validación para comprobar que el pipeline no genera `NaN`, `Infinity` ni muestras inválidas.

## ChainManager

`ChainManager` es la capa de Core/Engine responsable de administrar la cadena modular.

Operaciones:

```text
add(module, index?)
remove(id)
move(id, targetIndex)
duplicate(id, targetIndex?)
rename(id, name)
enable/disable
getModules()
validate()
build(context)
serialize()
restore()
```

Cada instancia conserva su `id`, nombre, estado y parámetros. Un mismo tipo puede aparecer varias veces.

### Nombres personalizados

Los nombres son independientes de `type` e `id`.

```text
Gain        → Input Trim
EQ          → Low Cut + Air
Compressor  → Glue
Saturation  → Analog Color
Clipper     → Transient Clip
Limiter     → Final Ceiling
```

Los duplicados reciben nombres únicos automáticamente (`Glue`, `Glue 2`, `Glue 3`).

## Realtime y rendimiento

El Core deberá respetar una política estricta de realtime:

- no heap allocation/deallocation dentro del callback;
- no locks bloqueantes;
- no I/O;
- no llamadas de UI;
- buffers preasignados;
- transporte de parámetros lock-free;
- metering desacoplado del callback.

La política numérica será `f32` por defecto, con `f64` selectivo. La vectorización se diseñará mediante una abstracción SIMD para aprovechar ARM NEON sin crear algoritmos específicos por plataforma.

## Android: primer APK experimental

La primera rama Android tendrá como objetivo **validar la arquitectura**, no construir todavía la aplicación final.

```text
PCM / WAV
   ↓
Android
   ↓
Oboe / AAudio
   ↓
Native bridge / FFI
   ↓
STYLO DSP CORE
   ↓
Gain
   ↓
Audio output
```

### Criterios de aceptación

1. El Core Rust compila para Android.
2. Se genera un APK reproducible.
3. Android instancia el Core.
4. Los buffers PCM llegan al Core.
5. Gain produce el resultado numérico esperado.
6. No aparecen `NaN`/`Infinity`.
7. El callback realtime no realiza allocations.
8. Los cambios de parámetros no bloquean el audio thread.
9. Existe fallback cuando la configuración low-latency/exclusive no está disponible.

## Web

La ruta prevista es:

```text
Rust Core
  ↓
WASM
  ↓
AudioWorklet
  ↓
Web Audio API
  ↓
Stylo Web
```

La implementación Web no debe convertirse en la fuente de verdad del DSP.

## VST3

La ruta prevista es:

```text
DAW
 ↓
VST3 Adapter
 ↓
STYLO DSP CORE
```

El adaptador VST3 gestionará el ciclo de vida del plugin, parámetros, automatización y buffers del host. Los algoritmos permanecerán en el Core.

## Presets multiplataforma

El estado se diseñará para poder viajar entre plataformas:

```json
{
  "format": "stylo-preset",
  "version": 1,
  "name": "Techno Master 01",
  "sampleRate": 48000,
  "modules": []
}
```

Debe conservar módulos, orden, duplicados, nombres, bypass, parámetros y versión del formato.

## Paridad multiplataforma

Una misma batería de vectores PCM deberá poder comparar:

```text
Rust scalar reference
Rust SIMD
Web/WASM
Android
VST3
```

Los resultados se compararán con tolerancias numéricas documentadas. El objetivo es validar que las plataformas ejecutan el mismo procesamiento, no simplemente que todas "funcionan".

## CI y calidad

GitHub Actions valida actualmente el proyecto existente con:

```text
Node 24
npm 11
engine-strict
npm install
Chromium
TypeScript
Vitest
```

Durante la incorporación de nombres hubo una ejecución intermedia fallida (`6b2200a8`) y una corrección posterior (`dd3a18cc`) que dejó el estado acumulado validado. Las ejecuciones deben evaluarse por commit y no únicamente por el número de Action.

Con la nueva arquitectura, CI deberá incorporar progresivamente:

- Rust formatting/lint/build;
- tests DSP;
- tests de seguridad realtime;
- target Android;
- WASM/paridad;
- posteriormente VST3.

## Tests

La base actual cubre, entre otros:

- metadata de parámetros;
- validación de rangos;
- serialización;
- procesamiento offline;
- validación de audio;
- Clipper;
- Limiter;
- ChainManager;
- duplicación independiente;
- reordenación;
- nombres personalizados;
- protección frente a valores no finitos.

La política es **no avanzar de capa cuando la base funcional no está en PASS**.

## Roadmap arquitectónico

```text
1. Rust DSP Core
       ↓
2. Gain + tests de referencia
       ↓
3. Realtime safety / zero-allocation
       ↓
4. Primer APK Android
       ↓
5. Web/WASM + AudioWorklet
       ↓
6. Migración progresiva de módulos
       ↓
7. Presets/paridad multiplataforma
       ↓
8. VST3 Adapter
       ↓
9. UI final Web + Android + plugin
```

La primera prueba de plataforma será **Core + Gain + Android APK**.

## Estructura objetivo

```text
StyloDSP/
├── core/
│   ├── dsp/
│   ├── engine/
│   ├── modules/
│   ├── parameters/
│   ├── state/
│   └── presets/
├── adapters/
│   ├── web/
│   ├── android/
│   └── vst3/
├── apps/
│   ├── web/
│   └── android/
├── plugins/
│   └── vst3/
├── tests/
└── docs/
```

La migración será incremental. El código TypeScript existente se conserva como referencia hasta que cada módulo tenga una implementación Core equivalente y tests de paridad.

## Limitaciones actuales

- La migración a Rust todavía no se ha ejecutado.
- El primer APK Android todavía está pendiente.
- True Peak y oversampling del Limiter requieren implementación DSP avanzada antes de considerarse definitivos.
- La interfaz visual modular final está pendiente.
- El sistema de presets completo está pendiente.
- La integración VST3 está pendiente.

## Licencia

Pendiente de definir.
