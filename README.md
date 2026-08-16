# StyloDSP

Motor DSP modular para procesamiento de audio y futura aplicacion de mastering configurable.

> **Estado:** núcleo DSP en desarrollo activo. La prioridad actual es que cada módulo funcione de forma verificable antes de construir las capas superiores de interfaz.

## Objetivo

Construir progresivamente un motor propio capaz de procesar audio PCM mediante módulos DSP independientes, configurables y encadenables.

La arquitectura está diseñada para que el usuario pueda construir su propia cadena de procesamiento, no para imponer una cadena fija.

## Arquitectura actual

```text
Audio
  ↓
ChainManager
  ↓
┌──────────────────────────────────────────────┐
│ módulos DSP configurables                   │
│                                              │
│ Gain → EQ → Compressor → Saturation          │
│          → Clipper → Limiter → Analyzer      │
└──────────────────────────────────────────────┘
  ↓
Output / render
```

La cadena es dinámica. Los módulos pueden:

- añadirse;
- eliminarse;
- duplicarse;
- reordenarse;
- activarse/desactivarse;
- renombrarse;
- serializarse para guardar su estado.

Un mismo tipo de módulo puede existir varias veces en una cadena. Cada instancia conserva su propio `id`, nombre, estado y parámetros.

## Módulos DSP

### Gain

Control de ganancia como módulo independiente y configurable.

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

Además existe un núcleo DSP determinista (`LimiterDsp`) que verifica reducción de ganancia, ceiling, lookahead y salida finita.

**Importante:** True Peak y oversampling todavía no deben considerarse implementaciones DSP finales. Están preparados en el contrato y requieren una fase posterior de procesamiento real y validación específica.

### Analyzer

Infraestructura de análisis y validación de audio para comprobar que el pipeline no genera `NaN`, `Infinity` ni muestras inválidas.

## ChainManager

`ChainManager` es la capa responsable de construir y administrar la cadena modular.

Operaciones principales:

```text
add(module, index?)
remove(id)
move(id, targetIndex)
duplicate(id, targetIndex?)
rename(id, name)
getModules()
validate()
build(context)
serialize()
```

### Nombres de módulos

Cada módulo puede tener un nombre editable por el usuario.

Ejemplo:

```text
Gain        → Input Trim
EQ          → Low Cut + Air
Compressor  → Glue
Saturation  → Analog Color
Clipper     → Transient Clip
Limiter     → Final Ceiling
```

Los nombres personalizados son independientes del `type` técnico y del `id` de la instancia.

Los duplicados reciben nombres únicos automáticamente, por ejemplo:

```text
Glue
Glue 2
Glue 3
```

El nombre está contemplado dentro del estado serializado para permitir posteriormente guardar y reconstruir presets.

## Contrato AudioModule

El contrato común de los módulos contempla:

- `id` técnico único;
- `type` DSP;
- `name` editable;
- `enabled`;
- `params`;
- validación;
- reset;
- creación del nodo de audio;
- metadata de parámetros;
- serialización.

La arquitectura mantiene los parámetros específicos fuertemente tipados dentro de cada módulo mientras permite administrar una cadena heterogénea desde `ChainManager`.

## Estado de calidad / CI

GitHub Actions valida actualmente:

```text
Node 24
npm 11
engine-strict
npm install
Chromium
TypeScript
Vitest
```

Las últimas validaciones han demostrado que el núcleo puede pasar instalación, typecheck y tests de forma reproducible.

### Historial reciente relevante

- **Clipper V1:** integrado y validado.
- **Limiter V1:** módulo y núcleo DSP inicial integrados y validados.
- **ChainManager:** añadir, eliminar, duplicar, reordenar y activar/desactivar módulos.
- **Nombres personalizados:** añadido soporte para renombrar módulos y mantener nombres durante la serialización.
- **CI Node 24/npm 11:** toolchain actualizada y validada.

> Nota de CI: durante la incorporación de nombres hubo una ejecución intermedia fallida (`6b2200a8`). La corrección posterior (`dd3a18cc`) dejó el estado acumulado de `main` en verde. Las ejecuciones deben evaluarse por commit y no únicamente por el número de la última Action.

## Tests

Los tests cubren, entre otros:

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

La política de desarrollo es **no avanzar de capa cuando la base funcional no está en PASS**.

## Próxima fase

### 1. Estado y presets

Implementar un formato de estado/preset que permita guardar y restaurar exactamente:

- módulos presentes;
- orden;
- duplicados;
- nombres personalizados;
- bypass;
- parámetros;
- versión del formato.

### 2. Integración de audio

Conectar la cadena modular a un pipeline de audio real y ampliar las pruebas de procesamiento.

### 3. Limiter DSP avanzado

Completar y verificar de forma específica:

- lookahead real;
- true peak real;
- oversampling real;
- comportamiento de ceiling bajo señales complejas.

### 4. Interfaz modular

Construir la interfaz sobre el contrato del motor, permitiendo al usuario:

- añadir módulos;
- eliminarlos;
- duplicarlos;
- arrastrarlos para reordenarlos;
- activar/desactivar;
- renombrarlos;
- editar todos los parámetros expuestos;
- guardar y cargar presets.

La UI será una capa superior del motor, no la responsable de la lógica DSP.

## Arquitectura objetivo

```text
┌──────────────────────────────────────────┐
│                  UI                      │
│ knobs · sliders · meters · rack · presets│
└────────────────────┬─────────────────────┘
                     ↓
┌──────────────────────────────────────────┐
│            Chain / State Layer            │
│ add · remove · duplicate · move · rename │
│ serialize · presets                       │
└────────────────────┬─────────────────────┘
                     ↓
┌──────────────────────────────────────────┐
│                 DSP Layer                 │
│ Gain · EQ · Comp · Sat · Clip · Limit    │
│ Analyzer / validation                     │
└────────────────────┬─────────────────────┘
                     ↓
┌──────────────────────────────────────────┐
│              Audio Pipeline               │
│ PCM · Offline render · realtime output     │
└──────────────────────────────────────────┘
```

## Principios de desarrollo

1. **Funcionalidad antes que apariencia.**
2. Cada módulo debe ser verificable de forma independiente.
3. Los parámetros deben estar disponibles para la futura UI mediante metadata.
4. La cadena debe ser dinámica y no depender de un número fijo de módulos.
5. Duplicar una instancia nunca debe compartir accidentalmente su estado con el original.
6. El estado debe poder serializarse de forma reproducible.
7. No etiquetar una función DSP como terminada cuando solo existe su control de interfaz.
8. Mantener CI verde antes de avanzar a una capa superior.

## Stack técnico

- TypeScript.
- Web Audio API.
- Vitest.
- Node 24.
- npm 11.
- GitHub Actions.

La documentación histórica del proyecto Android se conserva como referencia de la visión original, pero el desarrollo actual está centrado en el motor modular Web Audio/DSP.

## Limitaciones actuales

- El motor continúa en desarrollo.
- True Peak y oversampling del Limiter requieren implementación DSP avanzada antes de considerarse definitivos.
- La interfaz visual modular todavía está pendiente.
- El sistema de presets completo todavía está pendiente.
- La integración realtime completa requiere validación adicional.

## Licencia

Pendiente de definir.
