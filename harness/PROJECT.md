# STYLO HARNESS · PILOTO

## Proyecto
StyloDSP

## NUEVO OBJETIVO PRODUCTO · V0.2
Construir una **APK Android móvil de audio con analizador de frecuencias en tiempo real + ecualizador gráfico/paramétrico interactivo**, inspirado en el flujo visual de herramientas profesionales como FabFilter Pro-Q, pero con implementación y diseño propios.

### Experiencia principal
- Gráfica FFT/espectro de frecuencia en tiempo real.
- Eje frecuencial logarítmico y escala de dB.
- Curva global de EQ superpuesta al espectro.
- Puntos/bandas EQ manipulables directamente sobre la gráfica.
- Arrastrar punto horizontal = frecuencia.
- Arrastrar vertical = ganancia.
- Gestos para ajustar Q/ancho de banda.
- Añadir/eliminar bandas.
- Selección y edición precisa de cada banda.
- Respuesta visual inmediata mientras se reproduce/procesa audio.
- Interfaz táctil optimizada para móvil.

## Referencia de producto
La referencia de interacción es el paradigma de visualizar espectro + curva EQ + nodos editables de ecualizadores profesionales. **No se copiará código, assets ni identidad visual de terceros.**

## Arquitectura objetivo
`Audio input → DSP/FFT → datos espectrales → renderer gráfico`

`Touch/UI → parámetros EQ → DSP EQ → audio output`

El Core DSP debe permanecer independiente de Android/UI. La interfaz móvil será un adaptador/renderer del Core.

## Objetivo técnico del piloto
Primero conseguir una **vertical slice funcional**, no construir todo el producto de golpe:
1. Entrada/reproducción de audio.
2. FFT en tiempo real.
3. Renderizado estable del espectro.
4. Una banda EQ editable con nodo táctil.
5. Curva de respuesta EQ superpuesta.
6. Audio procesado con el mismo parámetro mostrado en pantalla.
7. Verificación de latencia, estabilidad y comportamiento táctil.

## Criterio de éxito de la primera demo
Un usuario puede reproducir audio, observar el espectro moviéndose en tiempo real, tocar la gráfica para seleccionar una banda, mover el nodo de frecuencia/ganancia y escuchar inmediatamente el efecto del cambio mientras la curva EQ se actualiza visualmente.

## Entornos de IA
- ChatGPT
- Gemini
- Codex
- Agentes compatibles con Git/GitHub

## Principio central
El repositorio contiene el estado operativo del proyecto. La conversación es temporal; los documentos del harness son persistentes.

## Alcance del Harness
1. Instrucciones del agente.
2. Estado persistente.
3. Backlog verificable.
4. Criterios de aceptación.
5. Handoff entre sesiones.
6. Registro de decisiones.
7. Verificación antes de declarar PASSING.

## Fuera de alcance inicial
- Automatización autónoma continua.
- Subagentes complejos.
- Graph Engineering.
- Copiar literalmente interfaces/código de productos comerciales.
- Optimización prematura de decenas de módulos DSP.

## Gates del producto
1. Audio I/O móvil
2. FFT realtime
3. Renderer espectral
4. EQ paramétrico de 1 banda
5. Nodos táctiles
6. Curva EQ + espectro simultáneos
7. Múltiples bandas
8. Gestos/Q/presets
9. Rendimiento y latencia
10. APK reproducible

## Regla de oro
No avanzar de capa con una capa anterior en FAIL o sin evidencia suficiente.
