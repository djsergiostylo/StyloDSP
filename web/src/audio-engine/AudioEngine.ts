import type { AudioModule } from './AudioModule';
import { ChainManager } from './ChainManager';

export class AudioEngine {
  private context: AudioContext | null = null;
  private source: AudioBufferSourceNode | null = null;
  private inputNode: GainNode | null = null;
  private outputGain: GainNode | null = null;
  private readonly chain = new ChainManager();

  get isReady(): boolean {
    return this.context !== null;
  }

  get audioContext(): AudioContext | null {
    return this.context;
  }

  async initialize(): Promise<void> {
    if (this.context) return;
    this.context = new AudioContext();
    this.inputNode = this.context.createGain();
    this.outputGain = this.context.createGain();
    this.outputGain.connect(this.context.destination);
    this.rebuildRouting();
  }

  setModules(modules: AudioModule[]): void {
    this.chain.setModules(modules);
    if (this.context) this.rebuildRouting();
  }

  validateChain(): string[] {
    return this.chain.validate();
  }

  async play(buffer: AudioBuffer): Promise<void> {
    await this.initialize();
    if (!this.context || !this.inputNode) throw new Error('AudioEngine no inicializado');
    const errors = this.validateChain();
    if (errors.length > 0) throw new Error(`Cadena inválida: ${errors.join('; ')}`);

    if (this.context.state === 'suspended') await this.context.resume();
    this.stop();

    this.source = this.context.createBufferSource();
    this.source.buffer = buffer;
    this.source.connect(this.inputNode);
    this.source.onended = () => {
      this.source?.disconnect();
      this.source = null;
    };
    this.source.start();
  }

  stop(): void {
    if (!this.source) return;
    try {
      this.source.stop();
    } catch {
      // Source may already have stopped naturally.
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
    this.inputNode?.disconnect();
    this.outputGain?.disconnect();
    this.inputNode = null;
    this.outputGain = null;
    void this.context?.close();
    this.context = null;
  }

  private rebuildRouting(): void {
    if (!this.context || !this.inputNode || !this.outputGain) return;

    this.inputNode.disconnect();
    const built = this.chain.build({ audioContext: this.context });

    if (built) {
      this.inputNode.connect(built.input);
      built.output.connect(this.outputGain);
    } else {
      this.inputNode.connect(this.outputGain);
    }
  }
}
