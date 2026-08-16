import type { ParameterMap } from './ParameterTypes';

export interface AudioModuleContext {
  audioContext: AudioContext | OfflineAudioContext;
}

export interface AudioModuleGraph {
  input: AudioNode;
  output: AudioNode;
}

// Heterogeneous DSP chains need a broad default parameter type.
// Concrete modules keep their own strongly typed parameter interfaces.
export interface AudioModule<TParams extends object = any> {
  readonly id: string;
  readonly type: string;
  enabled: boolean;
  params: TParams;
  validate(): string[];
  reset(): void;
  createNode(context: AudioModuleContext): AudioNode | AudioModuleGraph;
  serialize(): { id: string; type: string; enabled: boolean; params: TParams };
  getParameterDefinitions(): ParameterMap;
}
