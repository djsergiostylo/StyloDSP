import { describe, expect, it } from 'vitest';
import { ClipperModule } from './ClipperModule';

describe('ClipperModule', () => {
  it('exposes configurable metadata with valid ranges', () => {
    const module = new ClipperModule();
    const definitions = module.getParameterDefinitions();

    expect(definitions.mode.options).toHaveLength(2);
    expect(definitions.thresholdDb.min).toBe(-24);
    expect(definitions.thresholdDb.max).toBe(0);
    expect(definitions.driveDb.min).toBe(0);
    expect(definitions.driveDb.max).toBe(24);
    expect(definitions.ceilingDb.min).toBe(-24);
    expect(definitions.ceilingDb.max).toBe(0);
    expect(definitions.mix.min).toBe(0);
    expect(definitions.mix.max).toBe(1);
    expect(definitions.oversampling.options).toHaveLength(3);
  });

  it('rejects an invalid ceiling below threshold', () => {
    const module = new ClipperModule();
    module.params.thresholdDb = -1;
    module.params.ceilingDb = -3;
    expect(module.validate()).toContain('ceilingDb no puede estar por debajo de thresholdDb');
  });

  it('accepts the default configuration', () => {
    const module = new ClipperModule();
    expect(module.validate()).toEqual([]);
  });

  it('serializes the complete editable state', () => {
    const module = new ClipperModule();
    module.params.driveDb = 6;
    module.params.ceilingDb = -0.8;
    const serialized = module.serialize();
    expect(serialized.type).toBe('clipper');
    expect(serialized.params.driveDb).toBe(6);
    expect(serialized.params.ceilingDb).toBe(-0.8);
  });
});
