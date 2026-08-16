import type { AudioModule, AudioModuleContext, AudioModuleGraph } from './AudioModule';

export class ChainManager {
  private modules: AudioModule[] = [];

  setModules(modules: AudioModule[]): void {
    this.modules = [...modules];
  }

  add(module: AudioModule): void {
    this.modules.push(module);
  }

  remove(id: string): boolean {
    const before = this.modules.length;
    this.modules = this.modules.filter((module) => module.id !== id);
    return this.modules.length !== before;
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

function isGraph(value: AudioNode | AudioModuleGraph): value is AudioModuleGraph {
  return 'input' in value && 'output' in value;
}
