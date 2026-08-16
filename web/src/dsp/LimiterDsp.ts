export interface LimiterProcessOptions {
  sampleRate: number;
  thresholdDb: number;
  ceilingDb: number;
  releaseMs: number;
  lookaheadMs: number;
  gainDb: number;
}

const dbToLinear = (db: number) => Math.pow(10, db / 20);

export function processLimiter(input: Float32Array, options: LimiterProcessOptions): Float32Array {
  const threshold = dbToLinear(options.thresholdDb);
  const ceiling = dbToLinear(options.ceilingDb);
  const gain = dbToLinear(options.gainDb);
  const delay = Math.max(0, Math.round(options.lookaheadMs * options.sampleRate / 1000));
  const releaseCoeff = Math.exp(-1 / Math.max(1, options.releaseMs * options.sampleRate / 1000));
  const output = new Float32Array(input.length);
  const delayLine = new Float32Array(delay + 1);
  let gainReduction = 1;

  for (let n = 0; n < input.length; n++) {
    delayLine[n % delayLine.length] = input[n];
    const delayed = delayLine[(n - delay + delayLine.length) % delayLine.length] * gain;
    const peak = Math.abs(delayed);
    const target = peak > threshold ? threshold / peak : 1;
    gainReduction = target < gainReduction ? target : gainReduction + (1 - gainReduction) * (1 - releaseCoeff);
    output[n] = Math.max(-ceiling, Math.min(ceiling, delayed * gainReduction));
  }
  return output;
}
