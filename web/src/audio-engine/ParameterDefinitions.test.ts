import { describe, expect, it } from 'vitest';
import { GainModule } from '../dsp/GainModule';
import { EQModule } from '../dsp/EQModule';
import { CompressorModule } from '../dsp/CompressorModule';
import { SaturationModule } from '../dsp/SaturationModule';
import { ClipperModule } from '../dsp/ClipperModule';

const modules = [new GainModule(), new EQModule(), new CompressorModule(), new SaturationModule(), new ClipperModule()];

describe('parameter metadata contract', () => {
  it('exposes valid definitions for every current DSP module', () => {
    for (const module of modules) {
      const definitions = module.getParameterDefinitions();
      expect(Object.keys(definitions).length).toBeGreaterThan(0);
      for (const definition of Object.values(definitions)) {
        expect(definition.id).toBeTruthy();
        expect(definition.label).toBeTruthy();
        expect(['number', 'select', 'boolean']).toContain(definition.kind);
        if (definition.kind === 'number') {
          expect(definition.min).toBeTypeOf('number');
          expect(definition.max).toBeTypeOf('number');
          expect(definition.step).toBeTypeOf('number');
          expect(definition.min!).toBeLessThanOrEqual(definition.max!);
          expect(definition.step!).toBeGreaterThan(0);
        }
        if (definition.kind === 'select') expect(definition.options?.length).toBeGreaterThan(0);
      }
    }
  });

  it('uses metadata values that match the module defaults', () => {
    for (const module of modules) {
      const definitions = module.getParameterDefinitions();
      for (const [id, definition] of Object.entries(definitions)) {
        expect((module.params as unknown as Record<string, unknown>)[id]).toEqual(definition.value);
      }
    }
  });
});
