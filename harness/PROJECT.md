# STYLO HARNESS · PILOTO

## Proyecto
StyloDSP

## Propósito del piloto
Aplicar Harness Engineering al desarrollo del STYLO DSP CORE para comprobar que distintas IA pueden continuar el mismo proyecto usando una fuente de verdad persistente, estados explícitos y verificación objetiva.

## Objetivo técnico actual
Proteger y acelerar la transición hacia un Core DSP multiplataforma en Rust, manteniendo la implementación existente como referencia.

## Primer hito
**Core + Gain + Android APK experimental**.

## Entornos de IA
- ChatGPT
- Gemini
- Codex
- Cualquier agente compatible con Git/GitHub

## Principio central
El repositorio contiene el estado operativo del proyecto. La conversación es temporal; los documentos del harness son persistentes.

## Alcance del piloto
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
- Migración masiva de todos los módulos.
- UI final.

## Gates
1. Rust Core build
2. Gain tests
3. Realtime safety
4. Android APK
5. Web/WASM parity
6. Más módulos DSP
7. VST3 adapter
8. Presets/paridad completa

## Regla de oro
No avanzar de capa con una capa anterior en FAIL o sin evidencia suficiente.
