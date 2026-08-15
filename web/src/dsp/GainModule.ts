import type { AudioModule, AudioModuleContext } from '../audio-engine/AudioModule';

export interface GainParams {
  gainDb: number;
}

export class GainModule implements AudioModule<GainParams> {
  readonly id = crypto.randomUUID();
  readonly type = 'gain';
  enabled = true;
  params: GainParams = { gainDb: 0 };

  validate(): string[] {
    const errors: string[] = [];
    if (!Number.isFinite(this.params.gainDb)) errors.push('gainDb debe ser finito');
    if (this.params.gainDb < -24 || this.params.gainDb > 24) errors.push('gainDb fuera de rango [-24, 24] dB');
    return errors;
  }

  reset(): void {
    this.params.gainDb = 0;
    this.enabled = true;
  }

  createNode({ audioContext }: AudioModuleContext): GainNode {
    const node = audioContext.createGain();
    node.gain.value = this.enabled ? Math.pow(10, this.params.gainDb / 20) : 1;
    return node;
  }

  serialize() {
    return { id: this.id, type: this.type, enabled: this.enabled, params: { ...this.params } };
  }
}
