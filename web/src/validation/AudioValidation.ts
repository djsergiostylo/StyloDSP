export interface AudioValidationReport {
  ok: boolean;
  peak: number;
  rms: number;
  nonFiniteSamples: number;
  outOfRangeSamples: number;
}

export function validateAudioBuffer(buffer: AudioBuffer): AudioValidationReport {
  let peak = 0;
  let sumSquares = 0;
  let sampleCount = 0;
  let nonFiniteSamples = 0;
  let outOfRangeSamples = 0;

  for (let channel = 0; channel < buffer.numberOfChannels; channel++) {
    const data = buffer.getChannelData(channel);
    for (let i = 0; i < data.length; i++) {
      const sample = data[i];
      sampleCount++;
      if (!Number.isFinite(sample)) {
        nonFiniteSamples++;
        continue;
      }
      const magnitude = Math.abs(sample);
      peak = Math.max(peak, magnitude);
      sumSquares += sample * sample;
      if (magnitude > 1) outOfRangeSamples++;
    }
  }

  const rms = sampleCount > 0 ? Math.sqrt(sumSquares / sampleCount) : 0;
  return {
    ok: nonFiniteSamples === 0 && outOfRangeSamples === 0,
    peak,
    rms,
    nonFiniteSamples,
    outOfRangeSamples,
  };
}
