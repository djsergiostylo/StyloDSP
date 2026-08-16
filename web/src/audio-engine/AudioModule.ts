export interface AudioModuleContext {
  audioContext: AudioContext | OfflineAudioContext;
}

export interface AudioModuleGraph {
  input: AudioNode;
  output: AudioNode;
}

export interface AudioModule<TParams extends object = Record<string, unknown>> {
  readonly id: string;
  readonly type: string;
  enabled: boolean;
  params: TParams;
  validate(): string[];
  reset(): void;
  createNode(context: AudioModuleContext): AudioNode | AudioModuleGraph;
  serialize(): { id: string; type: string; enabled: boolean; params: TParams };
}
