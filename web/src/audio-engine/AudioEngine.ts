import type { AudioModule } from './AudioModule';

export class AudioEngine {
  private context: AudioContext | null = null;
  private source: AudioBufferSourceNode | null = null;
  private inputGain: GainNode | null = null;
  private outputGain: GainNode | null = null;
  private modules: AudioModule[] = [];

  get isReady(): boolean {
    return this.context !== null;
  }

  async initialize(): Promise<void> {
    if (this.context) return;
    this.context = new AudioContext();
    this.inputGain = this.context.createGain();
    this.outputGain = this.context.createGain();
    this.inputGain.connect(this.outputGain);
    this.outputGain.connect(this.context.destination);
  }

  setModules(modules: AudioModule[]): void {
    this.modules = modules;
  }

  async play(buffer: AudioBuffer): Promise<void> {
    await this.initialize();
    if (!this.context || !this.inputGain) throw new Error('AudioEngine no inicializado');

    if (this.context.state === 'suspended') await this.context.resume();
    this.stop();

    this.source = this.context.createBufferSource();
    this.source.buffer = buffer;
    this.source.connect(this.inputGain);
    this.source.start();
  }

  stop(): void {
    if (!this.source) return;
    try {
      this.source.stop();
    } catch {
      // Already stopped.
    }
    this.source.disconnect();
    this.source = null;
  }

  async decode(file: Blob): Promise<AudioBuffer> {
    await this.initialize();
    if (!this.context) throw new Error('AudioEngine no inicializado');
    return this.context.decodeAudioData(await file.arrayBuffer());
  }

  dispose(): void {
    this.stop();
    this.inputGain?.disconnect();
    this.outputGain?.disconnect();
    this.inputGain = null;
    this.outputGain = null;
    this.context?.close();
    this.context = null;
  }
}
