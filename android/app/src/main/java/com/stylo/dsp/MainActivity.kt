package com.stylo.dsp

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.os.Bundle
import android.os.Process
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import org.json.JSONArray
import org.json.JSONObject
import kotlin.math.abs
import kotlin.math.exp
import kotlin.math.ln
import kotlin.math.max
import kotlin.math.min

class MainActivity : Activity() {
    private lateinit var graph: EqGraphView
    private lateinit var player: PlayerEngine
    private var nativeEq: NativeEqBridge? = null
    private var recorder: AudioRecord? = null
    private var audioThread: Thread? = null
    @Volatile private var audioRunning = false
    @Volatile private var latestSpectrum = FloatArray(1024)
    private var playing = false
    private var duration = 0
    private var position = 0
    private var bypass = false
    private var ab = false
    private var loop = false
    private var fileLabel = "Sin archivo"
    private val prefs by lazy { getSharedPreferences("stylo_eq", MODE_PRIVATE) }

    private val graphicFreqs = doubleArrayOf(20,25,31.5,40,50,63,80,100,125,160,200,250,315,400,500,630,800,1000,1250,1600,2000,2500,3150,4000,5000,6300,8000,10000,12500,16000,20000)
    private val graphicBands = graphicFreqs.map { EqBand(it, 0.0, 1.0, FilterType.PEAK) }.toMutableList()
    private val paramBands = MutableList(8) { i -> EqBand(graphicFreqs[(i+12).coerceAtMost(30)], 0.0, 1.0, FilterType.PEAK) }

    override fun onCreate(state: Bundle?) {
        super.onCreate(state)
        window.setSoftInputMode(3)
        player = PlayerEngine(this) { pos, dur, isPlaying ->
            position = pos; duration = dur; playing = isPlaying
            runOnUiThread { refreshTransport() }
        }
        buildUi()
        startAnalyzerWhenPermitted()
    }

    private fun buildUi() {
        val root = LinearLayout(this).apply { orientation=LinearLayout.VERTICAL; setBackgroundColor(Color.rgb(8,10,12)); setPadding(10,8,10,8) }
        graph = EqGraphView()
        root.addView(graph, LinearLayout.LayoutParams(-1,0,1f))

        val file = TextView(this).apply { text=fileLabel; setTextColor(0xffaebbc2.toInt()); textSize=12f; maxLines=1; ellipsize=android.text.TextUtils.TruncateAt.MIDDLE; gravity=Gravity.CENTER_VERTICAL; setPadding(8,2,8,2) }
        root.addView(file, LinearLayout.LayoutParams(-1,32))

        val seek = SeekBar(this).apply { max=1000; setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(s: SeekBar?, p: Int, fromUser: Boolean) { if(fromUser && duration>0) { position=(duration*p)/1000; player.seekTo(position); graph.invalidate() } }
            override fun onStartTrackingTouch(s: SeekBar?) {}
            override fun onStopTrackingTouch(s: SeekBar?) {}
        }) }
        root.addView(seek, LinearLayout.LayoutParams(-1,38))

        val time = TextView(this).apply { text="00:00 / 00:00"; tag="time"; setTextColor(0xff83939b.toInt()); textSize=11f; gravity=Gravity.CENTER }
        root.addView(time, LinearLayout.LayoutParams(-1,24))

        fun row(): LinearLayout = LinearLayout(this).apply { orientation=LinearLayout.HORIZONTAL; gravity=Gravity.CENTER; setPadding(0,3,0,3) }
        fun btn(label:String, action:()->Unit): Button = Button(this).apply { text=label; textSize=11f; isAllCaps=false; minHeight=48; minWidth=0; setPadding(4,0,4,0); setOnClickListener{action()} }
        fun addRow(r:LinearLayout, vararg buttons:Button) { buttons.forEach { r.addView(it, LinearLayout.LayoutParams(0,52,1f).apply { setMargins(3,0,3,0) }) }; root.addView(r) }

        val transport=row()
        val open=btn("📂") { pickAudio() }; val back=btn("⏮ 10s") { player.seekBy(-10_000) }; val play=btn("▶ / ⏸") { player.playPause() }; val fwd=btn("10s ⏭") { player.seekBy(10_000) }; val loopBtn=btn("↻ LOOP") { loop=!loop; player.setLoop(loop); loopBtnText(loopBtn,loop) }
        addRow(transport,open,back,play,fwd,loopBtn)

        val modes=row(); val graphic=btn("31 BAND") { graph.mode=0; graph.invalidate() }; val param=btn("PARAM 8") { graph.mode=1; graph.invalidate() }; val type=btn("TYPE") { graph.cycleType() }; val bypassBtn=btn("BYPASS") { bypass=!bypass; nativeEq?.setBypass(bypass); graph.invalidate() }; val abBtn=btn("A / B") { toggleAB(); abBtnText(abBtn,ab) }
        addRow(modes,graphic,param,type,bypassBtn,abBtn)

        val utility=row(); val save=btn("SAVE") { savePreset() }; val load=btn("LOAD") { loadPreset() }; val flat=btn("FLAT") { flatten() }; val reset=btn("RESET") { resetSelected() }; val prev=btn("◀ BAND") { graph.selected=(graph.selected-1).coerceAtLeast(0); graph.invalidate() }; val next=btn("BAND ▶") { graph.selected=(graph.selected+1).coerceAtMost(if(graph.mode==0)30 else 7); graph.invalidate() }
        addRow(utility,save,load,flat,reset,prev,next)

        val volumeRow=row(); val volumeLabel=TextView(this).apply{text="VOL";setTextColor(0xffaebbc2.toInt());gravity=Gravity.CENTER}
        val volume=SeekBar(this).apply{max=100;progress=100;setOnSeekBarChangeListener(object:SeekBar.OnSeekBarChangeListener{override fun onProgressChanged(s:SeekBar?,p:Int,fromUser:Boolean){player.setVolume(p/100f)};override fun onStartTrackingTouch(s:SeekBar?){ };override fun onStopTrackingTouch(s:SeekBar?){ }})}
        volumeRow.addView(volumeLabel,LinearLayout.LayoutParams(48,44)); volumeRow.addView(volume,LinearLayout.LayoutParams(0,44,1f)); root.addView(volumeRow)
        setContentView(root)
        this.seekBar=seek; this.timeView=time; this.fileView=file; this.playButton=play
    }

    private lateinit var seekBar: SeekBar; private lateinit var timeView: TextView; private lateinit var fileView: TextView; private lateinit var playButton: Button
    private fun refreshTransport(){ if(duration>0) seekBar.progress=(position*1000/duration).coerceIn(0,1000); timeView.text="${fmt(position)} / ${fmt(duration)}"; playButton.text=if(playing) "⏸ PAUSE" else "▶ PLAY"; graph.invalidate() }
    private fun fmt(ms:Int):String{val s=(ms/1000).coerceAtLeast(0);return "%02d:%02d".format(s/60,s%60)}
    private fun loopBtnText(b:Button,on:Boolean){b.text=if(on)"↻ LOOP ON" else "↻ LOOP"}
    private fun abBtnText(b:Button,on:Boolean){b.text=if(on)"A / B: B" else "A / B: A"}

    private fun pickAudio(){startActivityForResult(Intent(Intent.ACTION_OPEN_DOCUMENT).apply{type="audio/*";addCategory(Intent.CATEGORY_OPENABLE)},REQUEST_FILE)}
    override fun onActivityResult(requestCode:Int,resultCode:Int,data:Intent?){super.onActivityResult(requestCode,resultCode,data);if(requestCode==REQUEST_FILE&&resultCode==RESULT_OK){data?.data?.let{uri->runCatching{contentResolver.takePersistableUriPermission(uri,Intent.FLAG_GRANT_READ_URI_PERMISSION);player.load(uri);fileLabel=uri.lastPathSegment?:"Audio";fileView.text=fileLabel;handlerLoadEq()}.onFailure{Toast.makeText(this,"No se pudo abrir el audio",Toast.LENGTH_LONG).show()}}}}
    private fun handlerLoadEq(){window.decorView.postDelayed({ nativeEq?.release(); nativeEq=NativeEqBridge(player.audioSessionId(),31); applyAllBands() },600)

    private fun applyAllBands(){graphicBands.forEachIndexed{i,b->nativeEq?.setGraphicGain(i,if(b.enabled&&!bypass)b.gainDb.toFloat() else 0f)}}
    private fun startAnalyzerWhenPermitted(){if(checkSelfPermission(Manifest.permission.RECORD_AUDIO)!=PackageManager.PERMISSION_GRANTED)requestPermissions(arrayOf(Manifest.permission.RECORD_AUDIO),REQUEST_AUDIO)else startAnalyzer()}
    override fun onRequestPermissionsResult(code:Int,p:Array<out String>,r:IntArray){super.onRequestPermissionsResult(code,p,r);if(code==REQUEST_AUDIO&&r.firstOrNull()==PackageManager.PERMISSION_GRANTED)startAnalyzer()else Toast.makeText(this,"Se necesita el micrófono para el analizador",Toast.LENGTH_LONG).show()}
    private fun startAnalyzer(){if(audioRunning)return;val rate=44100;val n=2048;val minBuffer=AudioRecord.getMinBufferSize(rate,AudioFormat.CHANNEL_IN_MONO,AudioFormat.ENCODING_PCM_16BIT);if(minBuffer<=0)return;recorder=AudioRecord(MediaRecorder.AudioSource.DEFAULT,rate,AudioFormat.CHANNEL_IN_MONO,AudioFormat.ENCODING_PCM_16BIT,max(minBuffer,n*4));runCatching{recorder?.startRecording()}.onFailure{recorder?.release();recorder=null;return};audioRunning=true
        audioThread=Thread{Process.setThreadPriority(Process.THREAD_PRIORITY_AUDIO);val samples=ShortArray(n);val mags=FloatArray(n/2);val fft=Fft(n);var lastUi=0L;while(audioRunning){val got=recorder?.read(samples,0,n)?:0;if(got>0){fft.process(samples,got,mags);latestSpectrum=mags.copyOf();val now=System.nanoTime();if(now-lastUi>33_000_000L){lastUi=now;graph.post{graph.setSpectrum(latestSpectrum)}}}}}.also{it.start()}}

    private fun selectedBand():EqBand=if(graph.mode==0)graphicBands[graph.selected] else paramBands[graph.selected.coerceIn(0,7)]
    private fun resetSelected(){selectedBand().apply{gainDb=0.0;q=1.0;type=FilterType.PEAK;enabled=true};applyAllBands();graph.invalidate()}
    private fun flatten(){graphicBands.forEach{it.gainDb=0.0};paramBands.forEach{it.gainDb=0.0};applyAllBands();graph.invalidate()}
    private fun savePreset(){val arr=JSONArray();graphicBands.forEach{arr.put(JSONObject().apply{put("f",it.frequency);put("g",it.gainDb);put("q",it.q);put("t",it.type.name);put("e",it.enabled)})};prefs.edit().putString("preset",arr.toString()).apply();Toast.makeText(this,"Preset guardado",Toast.LENGTH_SHORT).show()}
    private fun loadPreset(){val raw=prefs.getString("preset",null)?:return;runCatching{val a=JSONArray(raw);for(i in 0 until min(a.length(),31)){val o=a.getJSONObject(i);graphicBands[i].gainDb=o.optDouble("g",0.0);graphicBands[i].q=o.optDouble("q",1.0);graphicBands[i].type=runCatching{FilterType.valueOf(o.optString("t","PEAK"))}.getOrDefault(FilterType.PEAK);graphicBands[i].enabled=o.optBoolean("e",true)};applyAllBands();graph.invalidate();Toast.makeText(this,"Preset cargado",Toast.LENGTH_SHORT).show()}}
    private fun toggleAB(){ab=!ab;if(ab){graph.snapshot=graphicBands.map{it.gainDb}.toDoubleArray()}else{val s=graph.snapshot;if(s!=null)graphicBands.forEachIndexed{i,b->b.gainDb=s[i]};applyAllBands()};graph.invalidate()}

    override fun onDestroy(){audioRunning=false;audioThread?.join(250);audioThread=null;runCatching{recorder?.stop()};recorder?.release();recorder=null;nativeEq?.release();player.release();super.onDestroy()}

    private inner class EqGraphView:View(this@MainActivity){private val p=Paint(Paint.ANTI_ALIAS_FLAG);private var spectrum=FloatArray(1024);var mode=0;var selected=17;var snapshot:DoubleArray?=null;private var dragging=false;private var qStart=1.0
        fun setSpectrum(v:FloatArray){spectrum=v;invalidate()}
        private fun xFreq(x:Float,w:Float)=20.0*exp(ln(1000.0)*x/w)
        private fun fX(f:Double,w:Float)=(ln(f/20.0)/ln(1000.0)*w).toFloat()
        private fun gainY(g:Double,h:Float)=(h*0.43f-(g/24.0*h*0.32f))
        private fun yGain(y:Float,h:Float)=((h*0.43f-y)/(h*0.32f)*24.0)
        private fun drawText(c:Canvas,s:String,x:Float,y:Float,size:Float){p.style=Paint.Style.FILL;p.color=0xff9eabb1.toInt();p.textSize=size;c.drawText(s,x,y,p)}
        override fun onDraw(c:Canvas){super.onDraw(c);val w=width.toFloat();val h=height.toFloat();c.drawColor(0xff080a0c.toInt());p.style=Paint.Style.STROKE;p.strokeWidth=1f;p.color=0x26343a40
            for(i in 0..8){val y=h*(0.08f+i*0.84f/8f);c.drawLine(0f,y,w,y,p)};for(f in doubleArrayOf(20.0,50.0,100.0,200.0,500.0,1000.0,2000.0,5000.0,10000.0,20000.0)){val x=fX(f,w);c.drawLine(x,h*.06f,x,h*.92f,p);drawText(c,if(f>=1000)"${(f/1000).toInt()}k" else "${f.toInt()}",x-10,h-6,10f)}
            p.color=0x995fd5e8.toInt();p.strokeWidth=2f;val sp=Path();var started=false;for(i in 1 until spectrum.size){val f=i.toDouble()/spectrum.size*22050.0;if(f<20||f>20000)continue;val x=fX(f,w);val db=spectrum[i].coerceIn(-90f,0f);val y=h*.9f-(db+90f)/90f*h*.78f;if(!started){sp.moveTo(x,y);started=true}else sp.lineTo(x,y)};c.drawPath(sp,p)
            val bands=if(mode==0)graphicBands else paramBands;bands.forEachIndexed{i,b->val x=fX(b.frequency,w);val y=gainY(b.gainDb,h);p.style=Paint.Style.FILL;p.color=if(i==selected)0xff7de7ff.toInt() else 0xff58717b.toInt();c.drawCircle(x,y,if(i==selected)11f else 7f,p)}
            if(mode==1){val b=paramBands[selected];p.style=Paint.Style.STROKE;p.strokeWidth=3f;p.color=0xffe8f2f5.toInt();val curve=Path();for(xi in 0..w.toInt() step 6){val f=xFreq(xi.toFloat(),w);val z=ln(f/b.frequency)/ln(2.0);val width=max(.15,b.q);val d=exp(-0.5*(z*width)*(z*width));val y=gainY(b.gainDb*d,h);if(xi==0)curve.moveTo(0f,y)else curve.lineTo(xi.toFloat(),y)};c.drawPath(curve,p)}
            drawText(c,"STYLO EQ",14f,24f,16f);val b=selectedBand();drawText(c,"${b.frequency.toInt()} Hz  ${if(b.gainDb>=0)"+" else ""}${"%.1f".format(b.gainDb)} dB  Q ${"%.2f".format(b.q)}  ${b.type.name}",14f,46f,12f);drawText(c,if(mode==0)"31-BAND GRAPHIC" else "PARAMETRIC 8",w-145f,24f,10f);if(bypass)drawText(c,"BYPASS",w-65f,46f,10f)}
        override fun onTouchEvent(e:MotionEvent):Boolean{val w=width.toFloat();val h=height.toFloat();when(e.actionMasked){MotionEvent.ACTION_DOWN->{val bands=if(mode==0)graphicBands else paramBands;selected=bands.indices.minByOrNull{i->abs(fX(bands[i].frequency,w)-e.x)}?:selected;dragging=true;qStart=selectedBand().q;invalidate();return true};MotionEvent.ACTION_MOVE->if(dragging){val b=selectedBand();b.frequency=xFreq(e.x.coerceIn(0f,w),w).coerceIn(20.0,20000.0);if(e.pointerCount>=2){b.q=(qStart*(1.0+((e.x-e.downXSafe())/max(80f,w)))).coerceIn(.1,18.0)}else b.gainDb=yGain(e.y,h).coerceIn(-24.0,24.0);if(mode==0){nativeEq?.setGraphicGain(selected,b.gainDb.toFloat())};invalidate();return true};MotionEvent.ACTION_UP,MotionEvent.ACTION_CANCEL->{dragging=false;return true}};return true}
        private fun MotionEvent.downXSafe():Float=0f
        fun cycleType(){val b=selectedBand();b.type=FilterType.values()[(b.type.ordinal+1)%FilterType.values().size];invalidate()}
    }
    companion object{private const val REQUEST_AUDIO=10;private const val REQUEST_FILE=11}
}
