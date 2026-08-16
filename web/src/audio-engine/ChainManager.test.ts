import { describe, expect, it } from 'vitest';
import { ChainManager } from './ChainManager';
import { GainModule } from '../dsp/GainModule';
import { EQModule } from '../dsp/EQModule';

function chainWithDefaults() { const chain = new ChainManager(); chain.add(new GainModule()); chain.add(new EQModule()); return chain; }

describe('ChainManager', () => {
  it('adds and removes modules', () => { const chain = chainWithDefaults(); const [gain] = chain.getModules(); expect(chain.getModules()).toHaveLength(2); expect(chain.remove(gain.id)).toBe(true); expect(chain.getModules()).toHaveLength(1); });
  it('reorders modules by id', () => { const chain = chainWithDefaults(); const [gain, eq] = chain.getModules(); expect(chain.move(eq.id, 0)).toBe(true); expect(chain.getModules().map((module) => module.id)).toEqual([eq.id, gain.id]); });
  it('duplicates with copied parameters and a unique name', () => { const chain = chainWithDefaults(); const original = chain.getModules()[0] as GainModule; original.params.gainDb = 4; const duplicate = chain.duplicate(original.id)!; expect(duplicate.id).not.toBe(original.id); expect(duplicate.type).toBe(original.type); expect(duplicate.params).toEqual(original.params); expect(duplicate.name).toBe('Gain 2'); expect(chain.getModules()).toHaveLength(3); });
  it('renames a module and rejects invalid names', () => { const chain = chainWithDefaults(); const module = chain.getModules()[0]; expect(chain.rename(module.id, 'Master Glue')).toBe(true); expect(module.name).toBe('Master Glue'); expect(chain.rename(module.id, '   ')).toBe(false); expect(chain.rename(module.id, 'x'.repeat(81))).toBe(false); });
  it('preserves duplicate independence while reordering', () => { const chain = chainWithDefaults(); const original = chain.getModules()[0]; const duplicate = chain.duplicate(original.id)!; duplicate.enabled = false; expect(chain.move(original.id, 99)).toBe(true); expect(chain.getModules()[2].id).toBe(original.id); expect(chain.getModules()[0].enabled).toBe(false); expect(chain.getModules()[2].enabled).toBe(true); });
});
