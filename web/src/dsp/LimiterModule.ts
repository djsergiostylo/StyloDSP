import type { AudioModule, AudioModuleContext, AudioModuleGraph } from '../audio-engine/AudioModule';
import type { ParameterMap } from '../audio-engine/ParameterTypes';

export interface LimiterParams {
  thresholdDb: number;
  ceilingDb: number;
  releaseMs: number;
  lookaheadMs: number;
  gainDb: number;
  mix: number;
  truePeak: boolean;
  oversampling: '1x' | '2x' | '4x';
}

const dbToLinear = (db: number) => Math.pow(10, db / 20);

export class LimiterModule implements AudioModule<LimiterParams> {
  readonly id = crypto.randomUUID();
  readonly type = 'limiter';
  enabled = true;
  params: LimiterParams = {
    thresholdDb: -6,
    ceilingDb: -1,
    releaseMs: 100,
    lookaheadMs: 3,
    gainDb: 0,
    mix: 1,
    truePeak: true,
    oversampling: '2x',
  };

  validate(): string[] {
    const p = this.params;
    const errors: string[] = [];
    if (!Number.isFinite(p.thresholdDb) || p.thresholdDb < -24 || p.thresholdDb > 0) errors.push('thresholdDb fuera de rango [-24, 0]');
    if (!Number.isFinite(p.ceilingDb) || p.ceilingDb < -24 || p.ceilingDb > 0) errors.push('ceilingDb fuera de rango [-24, 0]');
    if (p.ceilingDb < p.thresholdDb) errors.push('ceilingDb no puede estar por debajo de thresholdDb');
    if (!Number.isFinite(p.releaseMs) || p.releaseMs < 5 || p.releaseMs > 1000) errors.push('releaseMs fuera de rango [5, 1000]');
    if (!Number.isFinite(p.lookaheadMs) || p.lookaheadMs < 0 || p.lookaheadMs > 10) errors.push('lookaheadMs fuera de rango [0, 10]');
    if (!Number.isFinite(p.gainDb) || p.gainDb < -24 || p.gainDb > 24) errors.push('gainDb fuera de rango [-24, 24]');
    if (!Number.isFinite(p.mix) || p.mix < 0 || p.mix > 1) errors.push('mix fuera de rango [0, 1]');
    if (!p.oversampling || !['1x', '2x', '4x'].includes(p.oversampling)) errors.push('oversampling no válido');
    return errors;
  }

  reset(): void {
    this.params = { thresholdDb: -6, ceilingDb: -1, releaseMs: 100, lookaheadMs: 3, gainDb: 0, mix: 1, truePeak: true, oversampling: '2x' };
    this.enabled = true;
  }

  getParameterDefinitions(): ParameterMap {
    return {
      thresholdDb: { id: 'thresholdDb', label: 'Threshold', kind: 'number', value: this.params.thresholdDb, min: -24, max: 0, step: 0.1, unit: 'dB', automatable: true },
      ceilingDb: { id: 'ceilingDb', label: 'Ceiling', kind: 'number', value: this.params.ceilingDb, min: -24, max: 0, step: 0.1, unit: 'dB', automatable: true },
      releaseMs: { id: 'releaseMs', label: 'Release', kind: 'number', value: this.params.releaseMs, min: 5, max: 1000, step: 1, unit: 'ms', automatable: true },
      lookaheadMs: { id: 'lookaheadMs', label: 'Lookahead', kind: 'number', value: this.params.lookaheadMs, min: 0, max: 10, step: 0.1, unit: 'ms', automatable: true },
      gainDb: { id: 'gainDb', label: 'Gain', kind: 'number', value: this.params.gainDb, min: -24, max: 24, step: 0.1, unit: 'dB', automatable: true },
      mix: { id: 'mix', label: 'Mix', kind: 'number', value: this.params.mix, min: 0, max: 1, step: 0.01, unit: '%', automatable: true },
      truePeak: { id: 'truePeak', label: 'True Peak', kind: 'boolean', value: this.params.truePeak },
      oversampling: { id: 'oversampling', label: 'Oversampling', kind: 'select', value: this.params.oversampling, options: [{ value: '1x', label: '1x' }, { value: '2x', label: '2x' }, { value: '4x', label: '4x' }] },
    };
  }

  createNode({ audioContext }: AudioModuleContext): AudioModuleGraph {
    const input = audioContext.createGain();
    const compressor = audioContext.createDynamicsCompressor();
    const output = audioContext.createGain();
    if (!this.enabled) { input.connect(output); return { input, output }; }

    compressor.threshold.value = this.params.thresholdDb;
    compressor.ratio.value = 20;
    compressor.knee.value = 0;
    compressor.attack.value = Math.max(0.001, this.params.lookaheadMs / 1000);
    compressor.release.value = this.params.releaseMs / 1000;

    const makeup = audioContext.createGain();
    makeup.gain.value = dbToLinear(this.params.gainDb);
    const dry = audioContext.createGain();
    const wet = audioContext.createGain();
    dry.gain.value = 1 - this.params.mix;
    wet.gain.value = this.params.mix;

    input.connect(dry);
    input.connect(compressor);
    compressor.connect(makeup);
    makeup.connect(wet);
    dry.connect(output);
    wet.connect(output);
    return { input, output };
  }

  serialize() { return { id: this.id, type: this.type, enabled: this.enabled, params: { ...this.params } }; }
}
