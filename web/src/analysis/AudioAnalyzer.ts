export interface ChannelMetrics {
  peak: number;
  rms: number;
  crestFactor: number;
  dcOffset: number;
}

export interface AudioAnalysisReport {
  sampleRate: number;
  channels: number;
  length: number;
  durationSeconds: number;
  peak: number;
  peakDbfs: number;
  rms: number;
  rmsDbfs: number;
  crestFactor: number;
  channelMetrics: ChannelMetrics[];
}

function linearToDbfs(value: number): number {
  if (value <= 0) return Number.NEGATIVE_INFINITY;
  return 20 * Math.log10(value);
}

/**
 * Deterministic offline metering. RMS is intentionally not labelled LUFS:
 * LUFS requires standardized K-weighting and gating.
 */
export function analyzeAudioBuffer(buffer: AudioBuffer): AudioAnalysisReport {
  const channelMetrics: ChannelMetrics[] = [];
  let globalPeak = 0;
  let globalSumSquares = 0;
  let globalSamples = 0;

  for (let channel = 0; channel < buffer.numberOfChannels; channel++) {
    const data = buffer.getChannelData(channel);
    let peak = 0;
    let sumSquares = 0;
    let sum = 0;

    for (const sample of data) {
      const magnitude = Math.abs(sample);
      peak = Math.max(peak, magnitude);
      sumSquares += sample * sample;
      sum += sample;
    }

    const rms = data.length > 0 ? Math.sqrt(sumSquares / data.length) : 0;
    channelMetrics.push({
      peak,
      rms,
      crestFactor: rms > 0 ? peak / rms : 0,
      dcOffset: data.length > 0 ? sum / data.length : 0,
    });

    globalPeak = Math.max(globalPeak, peak);
    globalSumSquares += sumSquares;
    globalSamples += data.length;
  }

  const rms = globalSamples > 0 ? Math.sqrt(globalSumSquares / globalSamples) : 0;

  return {
    sampleRate: buffer.sampleRate,
    channels: buffer.numberOfChannels,
    length: buffer.length,
    durationSeconds: buffer.sampleRate > 0 ? buffer.length / buffer.sampleRate : 0,
    peak: globalPeak,
    peakDbfs: linearToDbfs(globalPeak),
    rms,
    rmsDbfs: linearToDbfs(rms),
    crestFactor: rms > 0 ? globalPeak / rms : 0,
    channelMetrics,
  };
}
