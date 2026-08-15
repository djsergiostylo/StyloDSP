import type { AudioModule, AudioModuleContext } from '../audio-engine/AudioModule';

export type EQFilterType = BiquadFilterType;

export interface EQParams {
  type: EQFilterType;
  frequencyHz: number;
  q: number;
  gainDb: number;
}

export class EQModule implements AudioModule<EQParams> {
  readonly id = crypto.randomUUID();
  readonly type = 'eq';
  enabled = true;
  params: EQParams = {
    type: 'peaking',
    frequencyHz: 1000,
    q: 0.707,
    gainDb: 0,
  };

  validate(): string[] {
    const errors: string[] = [];
    const validTypes: EQFilterType[] = [
      'lowpass', 'highpass', 'bandpass', 'lowshelf', 'highshelf', 'peaking', 'notch', 'allpass',
    ];

    if (!validTypes.includes(this.params.type)) errors.push('tipo de filtro no válido');
    if (!Number.isFinite(this.params.frequencyHz) || this.params.frequencyHz <= 0) {
      errors.push('frequencyHz debe ser mayor que 0');
    }
    if (!Number.isFinite(this.params.q) || this.params.q <= 0) errors.push('Q debe ser mayor que 0');
    if (!Number.isFinite(this.params.gainDb)) errors.push('gainDb debe ser finito');
    if (this.params.gainDb < -24 || this.params.gainDb > 24) errors.push('gainDb fuera de rango [-24, 24] dB');
    return errors;
  }

  reset(): void {
    this.params = { type: 'peaking', frequencyHz: 1000, q: 0.707, gainDb: 0 };
    this.enabled = true;
  }

  createNode({ audioContext }: AudioModuleContext): BiquadFilterNode {
    const node = audioContext.createBiquadFilter();
    node.type = this.params.type;
    node.frequency.value = this.params.frequencyHz;
    node.Q.value = this.params.q;
    node.gain.value = this.params.gainDb;
    if (!this.enabled) {
      node.type = 'allpass';
      node.frequency.value = 1000;
      node.Q.value = 0.707;
      node.gain.value = 0;
    }
    return node;
  }

  serialize() {
    return { id: this.id, type: this.type, enabled: this.enabled, params: { ...this.params } };
  }
}
