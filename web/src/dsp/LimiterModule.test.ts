import { describe, expect, it } from 'vitest';
import { LimiterModule } from './LimiterModule';

describe('LimiterModule', () => {
  it('exposes configurable metadata', () => {
    const module = new LimiterModule();
    const definitions = module.getParameterDefinitions();
    expect(Object.keys(definitions)).toEqual(expect.arrayContaining([
      'thresholdDb', 'ceilingDb', 'releaseMs', 'lookaheadMs', 'gainDb', 'mix', 'truePeak', 'oversampling'
    ]));
  });

  it('accepts default parameters', () => {
    const module = new LimiterModule();
    expect(module.validate()).toEqual([]);
  });

  it('rejects an invalid ceiling and timing values', () => {
    const module = new LimiterModule();
    module.params.ceilingDb = -10;
    module.params.thresholdDb = -6;
    module.params.releaseMs = 1;
    module.params.lookaheadMs = 20;
    expect(module.validate()).toEqual(expect.arrayContaining([
      'ceilingDb no puede estar por debajo de thresholdDb',
      'releaseMs fuera de rango [5, 1000]',
      'lookaheadMs fuera de rango [0, 10]'
    ]));
  });

  it('serializes the complete editable state', () => {
    const module = new LimiterModule();
    const serialized = module.serialize();
    expect(serialized.type).toBe('limiter');
    expect(serialized.params.truePeak).toBe(true);
    expect(serialized.params.oversampling).toBe('2x');
  });
});
