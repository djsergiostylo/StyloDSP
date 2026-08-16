import type { AudioModule, AudioModuleContext } from '../audio-engine/AudioModule';
import type { ParameterMap } from '../audio-engine/ParameterTypes';

export interface GainParams { gainDb: number; }

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
  reset(): void { this.params.gainDb = 0; this.enabled = true; }
  getParameterDefinitions(): ParameterMap {
    return { gainDb: { id: 'gainDb', label: 'Gain', kind: 'number', value: this.params.gainDb, min: -24, max: 24, step: 0.1, unit: 'dB', automatable: true } };
  }
  createNode({ audioContext }: AudioModuleContext): GainNode {
    const node = audioContext.createGain();
    node.gain.value = this.enabled ? Math.pow(10, this.params.gainDb / 20) : 1;
    return node;
  }
  serialize() { return { id: this.id, type: this.type, enabled: this.enabled, params: { ...this.params } }; }
}
