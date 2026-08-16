import { describe, expect, it } from 'vitest';
import { CompressorModule } from './CompressorModule';

describe('CompressorModule', () => {
  it('accepts its default parameters', () => {
    const module = new CompressorModule();
    expect(module.validate()).toEqual([]);
  });

  it('rejects invalid ratio and threshold', () => {
    const module = new CompressorModule();
    module.params.ratio = 0.5;
    module.params.thresholdDb = 3;
    expect(module.validate()).toEqual([
      'thresholdDb fuera de rango [-100, 0]',
      'ratio fuera de rango [1, 20]',
    ]);
  });
});
