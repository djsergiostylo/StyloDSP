import type { AudioModule, AudioModuleContext } from './AudioModule';

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

  build(context: AudioModuleContext): { input: AudioNode; output: AudioNode } | null {
    const active = this.modules.filter((module) => module.enabled);
    if (active.length === 0) return null;

    let input: AudioNode | null = null;
    let previous: AudioNode | null = null;

    for (const module of active) {
      const node = module.createNode(context);
      if (!input) input = node;
      if (previous) previous.connect(node);
      previous = node;
    }

    return input && previous ? { input, output: previous } : null;
  }

  serialize() {
    return this.modules.map((module) => module.serialize());
  }
}
