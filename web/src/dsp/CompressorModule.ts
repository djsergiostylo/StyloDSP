import type { AudioModule, AudioModuleContext, AudioModuleGraph } from '../audio-engine/AudioModule';

export interface CompressorParams {
  thresholdDb: number;
  kneeDb: number;
  ratio: number;
  attackMs: number;
  releaseMs: number;
  makeupGainDb: number;
}

export class CompressorModule implements AudioModule<CompressorParams> {
  readonly id = crypto.randomUUID();
  readonly type = 'compressor';
  enabled = true;
  params: CompressorParams = {
    thresholdDb: -12,
    kneeDb: 12,
    ratio: 2,
    attackMs: 10,
    releaseMs: 100,
    makeupGainDb: 0,
  };

  validate(): string[] {
    const p = this.params;
    const errors: string[] = [];
    if (!Number.isFinite(p.thresholdDb) || p.thresholdDb < -100 || p.thresholdDb > 0) errors.push('thresholdDb fuera de rango [-100, 0]');
    if (!Number.isFinite(p.kneeDb) || p.kneeDb < 0 || p.kneeDb > 40) errors.push('kneeDb fuera de rango [0, 40]');
    if (!Number.isFinite(p.ratio) || p.ratio < 1 || p.ratio > 20) errors.push('ratio fuera de rango [1, 20]');
    if (!Number.isFinite(p.attackMs) || p.attackMs < 0 || p.attackMs > 1000) errors.push('attackMs fuera de rango [0, 1000]');
    if (!Number.isFinite(p.releaseMs) || p.releaseMs < 0 || p.releaseMs > 3000) errors.push('releaseMs fuera de rango [0, 3000]');
    if (!Number.isFinite(p.makeupGainDb) || p.makeupGainDb < -24 || p.makeupGainDb > 24) errors.push('makeupGainDb fuera de rango [-24, 24]');
    return errors;
  }

  reset(): void {
    this.params = { thresholdDb: -12, kneeDb: 12, ratio: 2, attackMs: 10, releaseMs: 100, makeupGainDb: 0 };
    this.enabled = true;
  }

  createNode({ audioContext }: AudioModuleContext): AudioModuleGraph {
    const input = audioContext.createGain();
    const compressor = audioContext.createDynamicsCompressor();
    const makeup = audioContext.createGain();

    if (this.enabled) {
      compressor.threshold.value = this.params.thresholdDb;
      compressor.knee.value = this.params.kneeDb;
      compressor.ratio.value = this.params.ratio;
      compressor.attack.value = this.params.attackMs / 1000;
      compressor.release.value = this.params.releaseMs / 1000;
      makeup.gain.value = Math.pow(10, this.params.makeupGainDb / 20);
    } else {
      compressor.ratio.value = 1;
      makeup.gain.value = 1;
    }

    input.connect(compressor);
    compressor.connect(makeup);
    return { input, output: makeup };
  }

  serialize() {
    return { id: this.id, type: this.type, enabled: this.enabled, params: { ...this.params } };
  }
}
