package com.stylo.dsp

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.*
import android.media.*
import android.net.Uri
import android.os.Bundle
import android.os.Process
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.widget.*
import java.util.Locale
import kotlin.math.*

class MainActivity : Activity() {
    private val model=EqModel()
    private lateinit var graph:EqView
    private var recorder:AudioRecord?=null
    private var audioThread:Thread?=null
    @Volatile private var running=false
    private var player:MediaPlayer?=null
    private lateinit var audioEq:AudioEqProcessor
    private var seek:SeekBar?=null
    private var timeText:TextView?=null
    private var titleText:TextView?=null
    private val prefs by lazy{getSharedPreferences("stylo_eq",0)}

    override fun onCreate(state:Bundle?){super.onCreate(state);audioEq=AudioEqProcessor(model);loadPreset();buildUi();if(checkSelfPermission(Manifest.permission.RECORD_AUDIO)!=PackageManager.PERMISSION_GRANTED)requestPermissions(arrayOf(Manifest.permission.RECORD_AUDIO),10)else startAnalyzer()}
    private fun button(label:String,action:()->Unit)=Button(this).apply{text=label;setOnClickListener{action()}}
    private fun buildUi(){
        val root=LinearLayout(this).apply{orientation=LinearLayout.VERTICAL;setBackgroundColor(0xff090a0c.toInt())};graph=EqView();root.addView(graph,LinearLayout.LayoutParams(-1,0,1f))
        titleText=TextView(this).apply{text="Sin archivo · STYLO EQ";setTextColor(0xffb8c3c8.toInt());textSize=13f;setPadding(18,2,18,0)};root.addView(titleText)
        seek=SeekBar(this).apply{max=1000;setPadding(18,0,18,0);setOnSeekBarChangeListener(object:SeekBar.OnSeekBarChangeListener{override fun onProgressChanged(s:SeekBar?,p:Int,from:Boolean){if(from)player?.let{if(it.duration>0)it.seekTo(it.duration*p/1000)}};override fun onStartTrackingTouch(s:SeekBar?){ };override fun onStopTrackingTouch(s:SeekBar?){}})};root.addView(seek,LinearLayout.LayoutParams(-1,42))
        timeText=TextView(this).apply{text="00:00 / 00:00";gravity=Gravity.CENTER;setTextColor(0xffd9e4e8.toInt());textSize=12f};root.addView(timeText,LinearLayout.LayoutParams(-1,25))
        val transport=LinearLayout(this).apply{gravity=Gravity.CENTER};transport.addView(button("📂"){openAudio()});transport.addView(button("⏮"){player?.seekTo(max(0,(player?.currentPosition?:0)-10000))});transport.addView(button("▶/⏸"){togglePlay()});transport.addView(button("⏭"){player?.seekTo(min(player?.duration?:0,(player?.currentPosition?:0)+10000))});transport.addView(button("A/B"){model.ab=!model.ab;refresh()});root.addView(transport,LinearLayout.LayoutParams(-1,52))
        val eq=LinearLayout(this).apply{gravity=Gravity.CENTER};eq.addView(button("31-BAND"){model.parametricMode=false;model.selected=15;refresh()});eq.addView(button("PARAM 8"){model.parametricMode=true;model.selected=0;refresh()});eq.addView(button("TYPE"){cycleType();refresh()});eq.addView(button("BYPASS"){model.bypass=!model.bypass;refresh()});eq.addView(button("RESET"){resetActive();refresh()});root.addView(eq,LinearLayout.LayoutParams(-1,52))
        val presets=LinearLayout(this).apply{gravity=Gravity.CENTER};presets.addView(button("SAVE"){savePreset()});presets.addView(button("LOAD"){loadPreset();refresh()});presets.addView(button("FLAT"){resetActive();refresh()});root.addView(presets,LinearLayout.LayoutParams(-1,48));setContentView(root)
    }
    private fun refresh(){graph.invalidate();audioEq.apply()}
    private fun resetActive(){model.active.forEach{it.gain=0f;it.type=EqModel.Type.PEAK;it.q=1f}}
    private fun cycleType(){val b=model.active[model.selected];val types=EqModel.Type.values();b.type=types[(b.type.ordinal+1)%types.size]}
    private fun savePreset(){val e=prefs.edit();e.putBoolean("param",model.parametricMode).putBoolean("bypass",model.bypass);model.active.forEachIndexed{i,b->e.putFloat("g$i",b.gain).putFloat("q$i",b.q).putFloat("f$i",b.freq.toFloat()).putInt("t$i",b.type.ordinal)};e.apply();Toast.makeText(this,"Preset guardado",Toast.LENGTH_SHORT).show()}
    private fun loadPreset(){model.parametricMode=prefs.getBoolean("param",false);model.bypass=prefs.getBoolean("bypass",false);val list=model.active;for(i in list.indices){val b=list[i];if(prefs.contains("g$i")){b.gain=prefs.getFloat("g$i",0f);b.q=prefs.getFloat("q$i",1f);b.freq=prefs.getFloat("f$i",b.freq.toFloat()).toDouble();b.type=EqModel.Type.values()[prefs.getInt("t$i",0).coerceIn(0,EqModel.Type.values().lastIndex)]}}}
    private fun openAudio(){startActivityForResult(Intent(Intent.ACTION_OPEN_DOCUMENT).apply{type="audio/*";addCategory(Intent.CATEGORY_OPENABLE)},42)}
    override fun onActivityResult(r:Int,c:Int,d:Intent?){super.onActivityResult(r,c,d);if(r==42&&c==RESULT_OK)d?.data?.let{playUri(it)}}
    private fun playUri(uri:Uri){releasePlayer();player=MediaPlayer.create(this,uri);player?.setOnCompletionListener{seek?.progress=1000};titleText?.text="STYLO EQ · ${uri.lastPathSegment?:"audio"}";player?.audioSessionId?.let{audioEq.attach(it)};player?.start();updateTime()}
    private fun togglePlay(){player?.let{if(it.isPlaying)it.pause()else it.start();updateTime()}}
    private fun updateTime(){val p=player?:return;val cur=p.currentPosition;val dur=max(1,p.duration);seek?.progress=(cur*1000/dur).coerceIn(0,1000);timeText?.text="${fmt(cur)} / ${fmt(dur)}";seek?.postDelayed({if(player?.isPlaying==true)updateTime()},250)}
    private fun fmt(ms:Int)=String.format(Locale.US,"%02d:%02d",ms/60000,(ms/1000)%60)
    private fun startAnalyzer(){if(running)return;val rate=44100;val n=2048;val minBuf=AudioRecord.getMinBufferSize(rate,AudioFormat.CHANNEL_IN_MONO,AudioFormat.ENCODING_PCM_16BIT);if(minBuf<=0)return;recorder=AudioRecord(MediaRecorder.AudioSource.DEFAULT,rate,AudioFormat.CHANNEL_IN_MONO,AudioFormat.ENCODING_PCM_16BIT,max(minBuf,n*2));try{recorder?.startRecording()}catch(_:Throwable){return};running=true;audioThread=Thread{Process.setThreadPriority(Process.THREAD_PRIORITY_AUDIO);val samples=ShortArray(n);val mags=FloatArray(n/2);val fft=FastFft(n);while(running){val got=recorder?.read(samples,0,n)?:0;if(got>0){fft.magnitudeDb(samples,got,mags);graph.setSpectrum(mags)}}}.also{it.start()}}
    override fun onRequestPermissionsResult(r:Int,p:Array<out String>,g:IntArray){super.onRequestPermissionsResult(r,p,g);if(r==10&&g.firstOrNull()==PackageManager.PERMISSION_GRANTED)startAnalyzer()}
    override fun onDestroy(){running=false;try{recorder?.stop()}catch(_:Throwable){};recorder?.release();audioThread?.join(300);releasePlayer();audioEq.release();super.onDestroy()}
    private fun releasePlayer(){try{player?.stop()}catch(_:Throwable){};player?.release();player=null;audioEq.release()}

    private inner class EqView:View(this){
        private val p=Paint(Paint.ANTI_ALIAS_FLAG);private val spectrumPath=Path();private val eqPath=Path();private val spectrum=FloatArray(1024);private var drag=false;private var pinch=false;private var pinchStart=1f;private var qStart=1f
        fun setSpectrum(v:FloatArray){System.arraycopy(v,0,spectrum,0,min(v.size,spectrum.size));postInvalidateOnAnimation()}
        private fun xToF(x:Float)=20.0*exp(ln(1000.0)*x/max(1f,width.toFloat()));private fun fToX(f:Double)=(ln(f/20.0)/ln(1000.0)*width).toFloat();private fun yToG(y:Float)=12f-24f*(y-height*.08f)/(height*.80f);private fun gToY(g:Double)=(height*.88f-(g+12.0)/24.0*height*.80f).toFloat()
        override fun onDraw(c:Canvas){c.drawColor(0xff090a0c.toInt());val w=width.toFloat();val h=height.toFloat();p.style=Paint.Style.STROKE;p.strokeWidth=1f;p.color=0x30363c42;for(i in 0..10)c.drawLine(0f,h*.08f+i*h*.80f/10,w,h*.08f+i*h*.80f/10,p);for(f in doubleArrayOf(20.0,50.0,100.0,500.0,1000.0,5000.0,10000.0,20000.0)){val x=fToX(f);c.drawLine(x,h*.06f,x,h*.90f,p)};p.color=0xaa67ddf2.toInt();p.strokeWidth=1.5f;spectrumPath.reset();var started=false;for(i in 1 until spectrum.size){val f=i.toDouble()/spectrum.size*22050.0;if(f<20.0||f>20000.0)continue;val x=fToX(f);val db=spectrum[i].coerceIn(-80f,0f);val y=h*.88f-(db+80f)/80f*h*.76f;if(!started){spectrumPath.moveTo(x,y);started=true}else spectrumPath.lineTo(x,y)};c.drawPath(spectrumPath,p);p.color=0xffe9f4f7.toInt();p.strokeWidth=3f;eqPath.reset();for(x in 0..w.toInt() step 4){val y=gToY(model.responseDb(xToF(x.toFloat())));if(x==0)eqPath.moveTo(0f,y)else eqPath.lineTo(x.toFloat(),y)};c.drawPath(eqPath,p);p.style=Paint.Style.FILL;model.active.forEachIndexed{idx,b->p.color=if(idx==model.selected)0xff7de7ff.toInt()else 0xff789099.toInt();c.drawCircle(fToX(b.freq),gToY(b.gain.toDouble()),if(idx==model.selected)9f else 4f,p)};p.color=0xffb8c3c8.toInt();p.textSize=24f;c.drawText("STYLO EQ",20f,30f,p);p.textSize=13f;val mode=if(model.parametricMode)"PARAMETRIC · 8 BANDS" else "GRAPHIC · 31 BANDS";c.drawText(mode,20f,49f,p);val b=model.active[model.selected];c.drawText("${b.freq.roundToInt()} Hz   ${"%+.1f".format(b.gain)} dB   Q ${"%.2f".format(b.q)}   ${b.type.name}",20f,h-14f,p)}
        override fun onTouchEvent(e:MotionEvent):Boolean{val f=xToF(e.x);when(e.actionMasked){MotionEvent.ACTION_POINTER_DOWN->{if(e.pointerCount>=2){pinch=true;pinchStart=distance(e);qStart=model.active[model.selected].q};return true};MotionEvent.ACTION_DOWN->{model.selected=model.nearestBand(f);val b=model.active[model.selected];drag=abs(e.x-fToX(b.freq))<44&&abs(e.y-gToY(b.gain.toDouble()))<44;return true};MotionEvent.ACTION_MOVE->{if(pinch&&e.pointerCount>=2){val b=model.active[model.selected];b.q=(qStart*(distance(e)/max(1f,pinchStart))).coerceIn(.25f,10f);invalidate();audioEq.apply();return true};if(drag){val b=model.active[model.selected];b.freq=xToF(e.x).coerceIn(20.0,20000.0);b.gain=yToG(e.y).coerceIn(-12f,12f);invalidate();audioEq.apply();return true}};MotionEvent.ACTION_POINTER_UP->{pinch=false;return true};MotionEvent.ACTION_UP,MotionEvent.ACTION_CANCEL->{drag=false;pinch=false;return true}};return true}
        private fun distance(e:MotionEvent):Float{if(e.pointerCount<2)return 1f;val dx=e.getX(0)-e.getX(1);val dy=e.getY(0)-e.getY(1);return sqrt(dx*dx+dy*dy)}
    }
}
