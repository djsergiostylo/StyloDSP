# STYLO HARNESS · PROJECT

## Proyecto
StyloDSP

## Propósito
Aplicar Harness Engineering para que ChatGPT, Gemini, Codex u otros agentes puedan continuar StyloDSP con una memoria técnica persistente, estados explícitos, decisiones registradas y verificación objetiva.

## Estado actual
El proyecto está en fase de **reconciliación profunda y validación Android**. La implementación Android real ha avanzado bastante más que el prototipo documentado inicialmente. Antes de nuevas expansiones arquitectónicas se debe reconciliar el repositorio completo.

## Principio central
El repositorio contiene la memoria operativa persistente del proyecto. Las conversaciones son evidencia histórica y fuente de decisiones, pero deben contrastarse con el código y la evidencia actual.

## Entrada obligatoria para IA
- `AI_START_HERE.md`
- `AI_CONTEXT.md`
- `AGENTS.md`

## Documentos operativos
- `harness/INSTRUCTIONS.md`
- `harness/STATE.md`
- `harness/PROJECT_STATE.md`
- `harness/ARCHITECTURE.md`
- `harness/ROADMAP.md`
- `harness/DECISION_LOG.md`
- `harness/RECONCILIATION_PROTOCOL.md`
- `harness/AUDIT_PROTOCOL.md`
- `harness/VALIDATION_PROTOCOL.md`
- `harness/SESSION_HANDOFF.md`
- `harness/audits/`

## Hito actual
**H-002 — second deep repository radiography and reconciliation.**

## Gate de progreso
No pasar a limpieza de ramas, migración de arquitectura o nuevas grandes funcionalidades hasta:
1. completar la auditoría;
2. reconciliar ramas y documentación;
3. reconstruir el estado real del APK/runtime;
4. actualizar la fuente de verdad;
5. registrar las decisiones.

## Arquitectura futura
Rust Core, WASM y VST3 son líneas de evolución, no deben considerarse implementaciones actuales sin evidencia verificable.
