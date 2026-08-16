import type { ParameterMap } from './ParameterTypes';

export interface AudioModuleContext {
  audioContext: AudioContext | OfflineAudioContext;
}

export interface AudioModuleGraph {
  input: AudioNode;
  output: AudioNode;
}

// Heterogeneous DSP chains may receive legacy modules without a name.
// ChainManager assigns a stable editable name when a module enters a chain.
export interface AudioModule<TParams extends object = any> {
  readonly id: string;
  readonly type: string;
  name?: string;
  enabled: boolean;
  params: TParams;
  validate(): string[];
  reset(): void;
  createNode(context: AudioModuleContext): AudioNode | AudioModuleGraph;
  serialize(): { id: string; type: string; name?: string; enabled: boolean; params: TParams };
  getParameterDefinitions(): ParameterMap;
}
