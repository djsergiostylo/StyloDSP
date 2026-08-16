import type { AudioModule } from './AudioModule';

export function setModuleParameter<T extends object>(module: AudioModule<T>, key: keyof T, value: T[keyof T]): string[] {
  const definitions = module.getParameterDefinitions();
  const definition = definitions[String(key)];
  if (!definition) return [`Parámetro desconocido: ${String(key)}`];

  if (definition.kind === 'number' && typeof value === 'number') {
    if (!Number.isFinite(value)) return [`${String(key)} debe ser finito`];
    if (definition.min !== undefined && value < definition.min) return [`${String(key)} por debajo del mínimo`];
    if (definition.max !== undefined && value > definition.max) return [`${String(key)} por encima del máximo`];
  }

  if (definition.kind === 'select' && typeof value === 'string' && definition.options) {
    if (!definition.options.some(option => option.value === value)) return [`Valor no permitido para ${String(key)}`];
  }

  if (definition.kind === 'boolean' && typeof value !== 'boolean') return [`${String(key)} debe ser booleano`];

  module.params = { ...module.params, [key]: value };
  return module.validate();
}
