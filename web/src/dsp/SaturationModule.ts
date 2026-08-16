import type { AudioModule, AudioModuleContext, AudioModuleGraph } from '../audio-engine/AudioModule';

export type SaturationCurve = 'tanh' | 'cubic' | 'hard';

export interface SaturationParams {
  curve: SaturationCurve;
  driveDb: number;
  mix: number;
  outputDb: number;
}

function dbToLinear(db: number): number {
  return Math.pow(10, db / 20);
}

function shape(sample: number, curve: SaturationCurve): number {
  if (curve === 'tanh') return Math.tanh(sample);
  if (curve === 'hard') return Math.max(-1, Math.min(1, sample));
  const x = Math.max(-1, Math.min(1, sample));
  return x - (x * x * x) / 3;
}

export class SaturationModule implements AudioModule<SaturationParams> {
  readonly id = crypto.randomUUID();
  readonly type = 'saturation';
  enabled = true;
  params: SaturationParams = { curve: 'tanh', driveDb: 0, mix: 1, outputDb: 0 };

  validate(): string[] {
    const p = this.params;
    const errors: string[] = [];
    if (!['tanh', 'cubic', 'hard'].includes(p.curve)) errors.push('curve no válida');
    if (!Number.isFinite(p.driveDb) || p.driveDb < 0 || p.driveDb > 24) errors.push('driveDb fuera de rango [0, 24]');
    if (!Number.isFinite(p.mix) || p.mix < 0 || p.mix > 1) errors.push('mix fuera de rango [0, 1]');
    if (!Number.isFinite(p.outputDb) || p.outputDb < -24 || p.outputDb > 24) errors.push('outputDb fuera de rango [-24, 24]');
    return errors;
  }

  reset(): void {
    this.params = { curve: 'tanh', driveDb: 0, mix: 1, outputDb: 0 };
    this.enabled = true;
  }

  createNode({ audioContext }: AudioModuleContext): AudioModuleGraph {
    const input = audioContext.createGain();
    const processor = audioContext.createWaveShaper();
    const output = audioContext.createGain();

    if (!this.enabled) {
      input.connect(output);
      return { input, output };
    }

    const drive = dbToLinear(this.params.driveDb);
    const curve = new Float32Array(2049);
    for (let i = 0; i < curve.length; i++) {
      const x = (i / (curve.length - 1)) * 2 - 1;
      curve[i] = shape(x * drive, this.params.curve);
    }

    processor.curve = curve;
    processor.oversample = '2x';
    input.connect(processor);

    const dry = audioContext.createGain();
    const wet = audioContext.createGain();
    dry.gain.value = 1 - this.params.mix;
    wet.gain.value = this.params.mix;
    input.connect(dry);
    processor.connect(wet);
    dry.connect(output);
    wet.connect(output);
    output.gain.value = dbToLinear(this.params.outputDb);
    return { input, output };
  }

  serialize() {
    return { id: this.id, type: this.type, enabled: this.enabled, params: { ...this.params } };
  }
}
