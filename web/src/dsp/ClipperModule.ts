import type { AudioModule, AudioModuleContext, AudioModuleGraph } from '../audio-engine/AudioModule';
import type { ParameterMap } from '../audio-engine/ParameterTypes';

export type ClipperMode = 'hard' | 'soft';
export type ClipperOversampling = '1x' | '2x' | '4x';

export interface ClipperParams {
  mode: ClipperMode;
  thresholdDb: number;
  driveDb: number;
  ceilingDb: number;
  mix: number;
  oversampling: ClipperOversampling;
}

const dbToLinear = (db: number) => Math.pow(10, db / 20);

function shape(x: number, mode: ClipperMode): number {
  if (mode === 'hard') return Math.max(-1, Math.min(1, x));
  const a = Math.abs(x);
  if (a <= 1) return x - (x * x * x) / 3;
  return Math.sign(x) * (2 / 3);
}

export class ClipperModule implements AudioModule<ClipperParams> {
  readonly id = crypto.randomUUID();
  readonly type = 'clipper';
  enabled = true;
  params: ClipperParams = {
    mode: 'soft',
    thresholdDb: -3,
    driveDb: 0,
    ceilingDb: -1,
    mix: 1,
    oversampling: '2x',
  };

  validate(): string[] {
    const p = this.params;
    const errors: string[] = [];
    if (!['hard', 'soft'].includes(p.mode)) errors.push('mode no válido');
    if (!Number.isFinite(p.thresholdDb) || p.thresholdDb < -24 || p.thresholdDb > 0) errors.push('thresholdDb fuera de rango [-24, 0]');
    if (!Number.isFinite(p.driveDb) || p.driveDb < 0 || p.driveDb > 24) errors.push('driveDb fuera de rango [0, 24]');
    if (!Number.isFinite(p.ceilingDb) || p.ceilingDb < -24 || p.ceilingDb > 0) errors.push('ceilingDb fuera de rango [-24, 0]');
    if (p.ceilingDb < p.thresholdDb) errors.push('ceilingDb no puede estar por debajo de thresholdDb');
    if (!Number.isFinite(p.mix) || p.mix < 0 || p.mix > 1) errors.push('mix fuera de rango [0, 1]');
    if (!['1x', '2x', '4x'].includes(p.oversampling)) errors.push('oversampling no válido');
    return errors;
  }

  reset(): void {
    this.params = { mode: 'soft', thresholdDb: -3, driveDb: 0, ceilingDb: -1, mix: 1, oversampling: '2x' };
    this.enabled = true;
  }

  getParameterDefinitions(): ParameterMap {
    return {
      mode: { id: 'mode', label: 'Mode', kind: 'select', value: this.params.mode, options: [{ value: 'hard', label: 'Hard' }, { value: 'soft', label: 'Soft' }] },
      thresholdDb: { id: 'thresholdDb', label: 'Threshold', kind: 'number', value: this.params.thresholdDb, min: -24, max: 0, step: 0.1, unit: 'dB', automatable: true },
      driveDb: { id: 'driveDb', label: 'Drive', kind: 'number', value: this.params.driveDb, min: 0, max: 24, step: 0.1, unit: 'dB', automatable: true },
      ceilingDb: { id: 'ceilingDb', label: 'Ceiling', kind: 'number', value: this.params.ceilingDb, min: -24, max: 0, step: 0.1, unit: 'dB', automatable: true },
      mix: { id: 'mix', label: 'Mix', kind: 'number', value: this.params.mix, min: 0, max: 1, step: 0.01, unit: '%', automatable: true },
      oversampling: { id: 'oversampling', label: 'Oversampling', kind: 'select', value: this.params.oversampling, options: [{ value: '1x', label: '1x' }, { value: '2x', label: '2x' }, { value: '4x', label: '4x' }] },
    };
  }

  createNode({ audioContext }: AudioModuleContext): AudioModuleGraph {
    const input = audioContext.createGain();
    const processor = audioContext.createWaveShaper();
    const output = audioContext.createGain();
    if (!this.enabled) { input.connect(output); return { input, output }; }

    const threshold = dbToLinear(this.params.thresholdDb);
    const drive = dbToLinear(this.params.driveDb);
    const ceiling = dbToLinear(this.params.ceilingDb);
    const curve = new Float32Array(4097);

    for (let i = 0; i < curve.length; i++) {
      const x = (i / (curve.length - 1)) * 2 - 1;
      const sign = Math.sign(x);
      const magnitude = Math.abs(x) * drive;
      const normalized = threshold > 0 ? magnitude / threshold : magnitude;
      const clipped = shape(normalized, this.params.mode);
      curve[i] = Math.max(-ceiling, Math.min(ceiling, clipped * threshold));
      if (sign === 0) curve[i] = 0;
    }

    processor.curve = curve;
    processor.oversample = this.params.oversampling === '4x' ? '4x' : this.params.oversampling === '2x' ? '2x' : 'none';

    const dry = audioContext.createGain();
    const wet = audioContext.createGain();
    dry.gain.value = 1 - this.params.mix;
    wet.gain.value = this.params.mix;
    input.connect(dry);
    input.connect(processor);
    processor.connect(wet);
    dry.connect(output);
    wet.connect(output);
    return { input, output };
  }

  serialize() { return { id: this.id, type: this.type, enabled: this.enabled, params: { ...this.params } }; }
}
