import type { AudioModule, AudioModuleContext, AudioModuleGraph } from './AudioModule';
import { GainModule } from '../dsp/GainModule';
import { EQModule } from '../dsp/EQModule';
import { CompressorModule } from '../dsp/CompressorModule';
import { SaturationModule } from '../dsp/SaturationModule';
import { ClipperModule } from '../dsp/ClipperModule';
import { LimiterModule } from '../dsp/LimiterModule';

export class ChainManager {
  private modules: AudioModule[] = [];

  setModules(modules: AudioModule[]): void {
    this.modules = [...modules];
  }

  add(module: AudioModule, index?: number): void {
    if (index === undefined || index >= this.modules.length) this.modules.push(module);
    else this.modules.splice(Math.max(0, index), 0, module);
  }

  remove(id: string): boolean {
    const index = this.modules.findIndex((module) => module.id === id);
    if (index < 0) return false;
    this.modules.splice(index, 1);
    return true;
  }

  move(id: string, targetIndex: number): boolean {
    const currentIndex = this.modules.findIndex((module) => module.id === id);
    if (currentIndex < 0) return false;
    const [module] = this.modules.splice(currentIndex, 1);
    const destination = Math.max(0, Math.min(targetIndex, this.modules.length));
    this.modules.splice(destination, 0, module);
    return true;
  }

  duplicate(id: string, targetIndex?: number): AudioModule | null {
    const source = this.modules.find((module) => module.id === id);
    if (!source) return null;
    const factory = MODULE_FACTORIES[source.type];
    if (!factory) return null;
    const copy = factory();
    Object.assign(copy.params, structuredClone(source.params));
    copy.enabled = source.enabled;
    const sourceIndex = this.modules.indexOf(source);
    this.add(copy, targetIndex ?? sourceIndex + 1);
    return copy;
  }

  getModules(): readonly AudioModule[] {
    return this.modules;
  }

  validate(): string[] {
    return this.modules.flatMap((module) =>
      module.validate().map((error) => `${module.type}:${module.id}: ${error}`),
    );
  }

  build(context: AudioModuleContext): AudioModuleGraph | null {
    const active = this.modules.filter((module) => module.enabled);
    if (active.length === 0) return null;

    let input: AudioNode | null = null;
    let previous: AudioNode | null = null;

    for (const module of active) {
      const created = module.createNode(context);
      const graph = isGraph(created) ? created : { input: created, output: created };
      if (!input) input = graph.input;
      if (previous) previous.connect(graph.input);
      previous = graph.output;
    }

    return input && previous ? { input, output: previous } : null;
  }

  serialize() {
    return this.modules.map((module) => module.serialize());
  }
}

const MODULE_FACTORIES: Record<string, () => AudioModule> = {
  gain: () => new GainModule(),
  eq: () => new EQModule(),
  compressor: () => new CompressorModule(),
  saturation: () => new SaturationModule(),
  clipper: () => new ClipperModule(),
  limiter: () => new LimiterModule(),
};

function isGraph(value: AudioNode | AudioModuleGraph): value is AudioModuleGraph {
  return 'input' in value && 'output' in value;
}
