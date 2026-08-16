import { describe, expect, it } from 'vitest';
import { analyzeAudioBuffer } from './AudioAnalyzer';

describe('AudioAnalyzer', () => {
  it('measures peak, RMS, crest factor and DC offset per channel', () => {
    const context = new OfflineAudioContext(2, 4, 48000);
    const buffer = context.createBuffer(2, 4, 48000);

    buffer.getChannelData(0).set([0.5, -0.5, 0.5, -0.5]);
    buffer.getChannelData(1).set([0.25, 0.25, 0.25, 0.25]);

    const report = analyzeAudioBuffer(buffer);

    expect(report.sampleRate).toBe(48000);
    expect(report.channels).toBe(2);
    expect(report.length).toBe(4);
    expect(report.durationSeconds).toBeCloseTo(4 / 48000, 12);
    expect(report.peak).toBeCloseTo(0.5, 12);
    expect(report.rms).toBeCloseTo(Math.sqrt((4 * 0.25 + 4 * 0.0625) / 8), 12);
    expect(report.channelMetrics[0].peak).toBeCloseTo(0.5, 12);
    expect(report.channelMetrics[0].rms).toBeCloseTo(0.5, 12);
    expect(report.channelMetrics[0].crestFactor).toBeCloseTo(1, 12);
    expect(report.channelMetrics[0].dcOffset).toBeCloseTo(0, 12);
    expect(report.channelMetrics[1].dcOffset).toBeCloseTo(0.25, 12);
  });

  it('reports silence without NaN or invalid dB values', () => {
    const context = new OfflineAudioContext(1, 16, 48000);
    const buffer = context.createBuffer(1, 16, 48000);
    const report = analyzeAudioBuffer(buffer);

    expect(report.peak).toBe(0);
    expect(report.rms).toBe(0);
    expect(report.crestFactor).toBe(0);
    expect(report.peakDbfs).toBe(Number.NEGATIVE_INFINITY);
    expect(report.rmsDbfs).toBe(Number.NEGATIVE_INFINITY);
  });
});
