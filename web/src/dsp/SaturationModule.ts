import type { AudioModule, AudioModuleContext, AudioModuleGraph } from '../audio-engine/AudioModule';
import type { ParameterMap } from '../audio-engine/ParameterTypes';
export type SaturationCurve='tanh'|'cubic'|'hard';
export interface SaturationParams{curve:SaturationCurve;driveDb:number;mix:number;outputDb:number;}
const db=(v:number)=>Math.pow(10,v/20);
const shape=(x:number,c:SaturationCurve)=>c==='tanh'?Math.tanh(x):c==='hard'?Math.max(-1,Math.min(1,x)):(()=>{const y=Math.max(-1,Math.min(1,x));return y-(y*y*y)/3;})();
export class SaturationModule implements AudioModule<SaturationParams>{
 readonly id=crypto.randomUUID();readonly type='saturation';enabled=true;params:SaturationParams={curve:'tanh',driveDb:0,mix:1,outputDb:0};
 validate():string[]{const p=this.params,e:string[]=[];if(!['tanh','cubic','hard'].includes(p.curve))e.push('curve no válida');if(!Number.isFinite(p.driveDb)||p.driveDb<0||p.driveDb>24)e.push('driveDb fuera de rango [0, 24]');if(!Number.isFinite(p.mix)||p.mix<0||p.mix>1)e.push('mix fuera de rango [0, 1]');if(!Number.isFinite(p.outputDb)||p.outputDb<-24||p.outputDb>24)e.push('outputDb fuera de rango [-24, 24]');return e;}
 reset(){this.params={curve:'tanh',driveDb:0,mix:1,outputDb:0};this.enabled=true;}
 getParameterDefinitions():ParameterMap{return{curve:{id:'curve',label:'Curve',kind:'select',value:this.params.curve,options:[{value:'tanh',label:'Soft / Tanh'},{value:'cubic',label:'Cubic'},{value:'hard',label:'Hard'}]},driveDb:{id:'driveDb',label:'Drive',kind:'number',value:this.params.driveDb,min:0,max:24,step:.1,unit:'dB',automatable:true},mix:{id:'mix',label:'Mix',kind:'number',value:this.params.mix,min:0,max:1,step:.01,unit:'%',automatable:true},outputDb:{id:'outputDb',label:'Output',kind:'number',value:this.params.outputDb,min:-24,max:24,step:.1,unit:'dB',automatable:true}};}
 createNode({audioContext}:AudioModuleContext):AudioModuleGraph{const input=audioContext.createGain(),processor=audioContext.createWaveShaper(),output=audioContext.createGain();if(!this.enabled){input.connect(output);return{input,output};}const curve=new Float32Array(2049),drive=db(this.params.driveDb);for(let i=0;i<curve.length;i++){const x=i/(curve.length-1)*2-1;curve[i]=shape(x*drive,this.params.curve);}processor.curve=curve;processor.oversample='2x';const dry=audioContext.createGain(),wet=audioContext.createGain();dry.gain.value=1-this.params.mix;wet.gain.value=this.params.mix;input.connect(dry);input.connect(processor);processor.connect(wet);dry.connect(output);wet.connect(output);output.gain.value=db(this.params.outputDb);return{input,output};}
 serialize(){return{id:this.id,type:this.type,enabled:this.enabled,params:{...this.params}};}
}
