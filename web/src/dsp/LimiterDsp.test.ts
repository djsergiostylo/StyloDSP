import { describe, expect, it } from 'vitest';
import { processLimiter } from './LimiterDsp';

describe('limiter DSP core', () => {
  const base = { sampleRate: 48000, thresholdDb: -6, ceilingDb: -1, releaseMs: 100, lookaheadMs: 3, gainDb: 12 };

  it('limits sustained peaks to the configured ceiling', () => {
    const input = new Float32Array(4800).fill(1);
    const output = processLimiter(input, base);
    let peak = 0;
    for (const sample of output) peak = Math.max(peak, Math.abs(sample));
    expect(peak).toBeLessThanOrEqual(Math.pow(10, -1 / 20) + 1e-5);
  });

  it('produces finite output and performs gain reduction', () => {
    const input = new Float32Array(4800).fill(0.8);
    const output = processLimiter(input, base);
    expect(output.every(Number.isFinite)).toBe(true);
    expect(output.some((value) => Math.abs(value) < Math.abs(input[100]))).toBe(true);
  });

  it('supports zero lookahead', () => {
    const input = new Float32Array([0, 0.9, 0.9, 0]);
    const output = processLimiter(input, { ...base, lookaheadMs: 0 });
    expect(output.every(Number.isFinite)).toBe(true);
  });
});
