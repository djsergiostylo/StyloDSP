import { describe, expect, it } from 'vitest';
import { OfflineRenderer } from '../audio-engine/OfflineRenderer';
import { analyzeAudioBuffer } from '../analysis/AudioAnalyzer';
import { SaturationModule } from './SaturationModule';

function makeImpulse(context: BaseAudioContext, amplitude = 0.5): AudioBuffer {
  const buffer = context.createBuffer(1, 128, 48000);
  buffer.getChannelData(0)[0] = amplitude;
  return buffer;
}

describe('SaturationModule', () => {
  it('valida parámetros y serializa configuración', () => {
    const module = new SaturationModule();
    expect(module.validate()).toEqual([]);
    module.params.driveDb = 25;
    expect(module.validate()).toContain('driveDb fuera de rango [0, 24]');
    module.params.driveDb = 6;
    expect(module.serialize().params).toEqual({ curve: 'tanh', driveDb: 6, mix: 1, outputDb: 0 });
  });

  it('mantiene bypass transparente cuando está desactivado', async () => {
    const context = new OfflineAudioContext(1, 128, 48000);
    const input = makeImpulse(context, 0.5);
    const module = new SaturationModule();
    module.enabled = false;
    const output = await new OfflineRenderer().render(input, [module]);
    expect(output.getChannelData(0)[0]).toBeCloseTo(0.5, 5);
  });

  it('procesa la señal y no produce NaN o Infinity', async () => {
    const context = new OfflineAudioContext(1, 256, 48000);
    const input = makeImpulse(context, 1);
    const module = new SaturationModule();
    module.params.driveDb = 12;
    module.params.mix = 1;
    const output = await new OfflineRenderer().render(input, [module]);
    const data = output.getChannelData(0);
    expect(Array.from(data).every(Number.isFinite)).toBe(true);
    expect(Math.abs(data[0])).toBeLessThan(1);
    expect(analyzeAudioBuffer(output).peak).toBeGreaterThan(0);
  });

  it('permite curvas tanh, cubic y hard', async () => {
    for (const curve of ['tanh', 'cubic', 'hard'] as const) {
      const context = new OfflineAudioContext(1, 128, 48000);
      const input = makeImpulse(context, 0.8);
      const module = new SaturationModule();
      module.params.curve = curve;
      module.params.driveDb = 6;
      const output = await new OfflineRenderer().render(input, [module]);
      expect(Array.from(output.getChannelData(0)).every(Number.isFinite)).toBe(true);
    }
  });
});
