import type { AudioModule } from './AudioModule';
import { ChainManager } from './ChainManager';

export interface OfflineRenderOptions {
  sampleRate?: number;
  channels?: number;
}

// The chain is intentionally heterogeneous: concrete DSP modules keep their own parameter types.
type AnyAudioModule = AudioModule<any>;

export class OfflineRenderer {
  async render(buffer: AudioBuffer, modules: AnyAudioModule[], options: OfflineRenderOptions = {}): Promise<AudioBuffer> {
    const sampleRate = options.sampleRate ?? buffer.sampleRate;
    const channels = options.channels ?? buffer.numberOfChannels;
    if (!Number.isInteger(channels) || channels < 1 || channels > 32) {
      throw new Error('Número de canales fuera de rango');
    }

    const context = new OfflineAudioContext(channels, buffer.length, sampleRate);
    const chain = new ChainManager();
    chain.setModules(modules);
    const errors = chain.validate();
    if (errors.length) throw new Error(`Cadena inválida: ${errors.join('; ')}`);

    const source = context.createBufferSource();
    source.buffer = buffer;
    const built = chain.build({ audioContext: context });

    if (built) {
      source.connect(built.input);
      built.output.connect(context.destination);
    } else {
      source.connect(context.destination);
    }

    source.start(0);
    return context.startRendering();
  }
}
