export interface AudioModuleContext {
  audioContext: AudioContext | OfflineAudioContext;
}

export interface AudioModule<TParams extends Record<string, unknown> = Record<string, unknown>> {
  readonly id: string;
  readonly type: string;
  enabled: boolean;
  params: TParams;
  validate(): string[];
  reset(): void;
  createNode(context: AudioModuleContext): AudioNode;
  serialize(): { id: string; type: string; enabled: boolean; params: TParams };
}
