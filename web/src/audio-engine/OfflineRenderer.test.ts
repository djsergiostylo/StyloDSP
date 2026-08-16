import { describe, expect, it } from 'vitest';
import { OfflineRenderer } from './OfflineRenderer';
import { GainModule } from '../dsp/GainModule';
import { EQModule } from '../dsp/EQModule';
import { CompressorModule } from '../dsp/CompressorModule';
import { validateAudioBuffer } from '../validation/AudioValidation';

function createImpulse(context: OfflineAudioContext): AudioBuffer {
  const buffer = context.createBuffer(2, 48000, 48000);
  buffer.getChannelData(0)[0] = 0.5;
  buffer.getChannelData(1)[0] = 0.5;
  return buffer;
}

describe('STYLO offline mastering pipeline', () => {
  it('renders a real AudioBuffer through Gain → EQ → Compressor without invalid samples', async () => {
    const sourceContext = new OfflineAudioContext(2, 48000, 48000);
    const source = createImpulse(sourceContext);

    const gain = new GainModule();
    gain.params.gainDb = 3;

    const eq = new EQModule();
    eq.params.type = 'peaking';
    eq.params.frequencyHz = 1000;
    eq.params.q = 0.707;
    eq.params.gainDb = 2;

    const compressor = new CompressorModule();
    compressor.params.thresholdDb = -18;
    compressor.params.ratio = 4;
    compressor.params.attackMs = 5;
    compressor.params.releaseMs = 100;
    compressor.params.makeupGainDb = 0;

    const rendered = await new OfflineRenderer().render(source, [gain, eq, compressor]);
    const report = validateAudioBuffer(rendered);

    expect(rendered.numberOfChannels).toBe(2);
    expect(rendered.sampleRate).toBe(48000);
    expect(rendered.length).toBe(48000);
    expect(report.nonFiniteSamples).toBe(0);
    expect(report.outOfRangeSamples).toBe(0);
    expect(report.peak).toBeGreaterThan(0);
    expect(report.rms).toBeGreaterThan(0);
  });

  it('preserves signal when the chain is empty', async () => {
    const context = new OfflineAudioContext(1, 128, 48000);
    const source = context.createBuffer(1, 128, 48000);
    source.getChannelData(0).fill(0.25);

    const rendered = await new OfflineRenderer().render(source, []);
    const samples = rendered.getChannelData(0);

    expect(Math.max(...samples)).toBeCloseTo(0.25, 5);
    expect(Math.min(...samples)).toBeCloseTo(0.25, 5);
  });
});
