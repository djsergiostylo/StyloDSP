import type { AudioModule, AudioModuleContext } from '../audio-engine/AudioModule';
import type { ParameterMap } from '../audio-engine/ParameterTypes';
export type EQFilterType = BiquadFilterType;
export interface EQParams { type: EQFilterType; frequencyHz: number; q: number; gainDb: number; }
export class EQModule implements AudioModule<EQParams> {
  readonly id = crypto.randomUUID(); readonly type = 'eq'; enabled = true;
  params: EQParams = { type: 'peaking', frequencyHz: 1000, q: 0.707, gainDb: 0 };
  validate(): string[] {
    const errors: string[] = []; const validTypes: EQFilterType[] = ['lowpass','highpass','bandpass','lowshelf','highshelf','peaking','notch','allpass'];
    if (!validTypes.includes(this.params.type)) errors.push('tipo de filtro no válido');
    if (!Number.isFinite(this.params.frequencyHz) || this.params.frequencyHz <= 0) errors.push('frequencyHz debe ser mayor que 0');
    if (!Number.isFinite(this.params.q) || this.params.q <= 0) errors.push('Q debe ser mayor que 0');
    if (!Number.isFinite(this.params.gainDb) || this.params.gainDb < -24 || this.params.gainDb > 24) errors.push('gainDb fuera de rango [-24, 24] dB');
    return errors;
  }
  reset(): void { this.params = { type:'peaking', frequencyHz:1000, q:0.707, gainDb:0 }; this.enabled = true; }
  getParameterDefinitions(): ParameterMap { return {
    type:{id:'type',label:'Filter',kind:'select',value:this.params.type,options:['lowpass','highpass','bandpass','lowshelf','highshelf','peaking','notch','allpass'].map(value=>({value,label:value})),automatable:false},
    frequencyHz:{id:'frequencyHz',label:'Frequency',kind:'number',value:this.params.frequencyHz,min:20,max:20000,step:1,unit:'Hz',automatable:true},
    q:{id:'q',label:'Q',kind:'number',value:this.params.q,min:0.1,max:20,step:0.001,automatable:true},
    gainDb:{id:'gainDb',label:'Gain',kind:'number',value:this.params.gainDb,min:-24,max:24,step:0.1,unit:'dB',automatable:true},
  }; }
  createNode({audioContext}: AudioModuleContext): BiquadFilterNode { const node=audioContext.createBiquadFilter(); node.type=this.params.type; node.frequency.value=this.params.frequencyHz; node.Q.value=this.params.q; node.gain.value=this.params.gainDb; if(!this.enabled){node.type='allpass';node.frequency.value=1000;node.Q.value=.707;node.gain.value=0;} return node; }
  serialize(){return {id:this.id,type:this.type,enabled:this.enabled,params:{...this.params}};}
}
