package com.stylo.dsp

import android.app.Activity
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.os.Bundle
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import org.json.JSONArray
import org.json.JSONObject
import kotlin.math.exp
import kotlin.math.ln
import kotlin.math.min

class MainActivity : Activity() {
    private lateinit var graph: EqGraphView
    private lateinit var player: PcmPlayerEngine
    private var playing = false
    private var duration = 0
    private var position = 0
    private var bypass = false
    private var ab = false
    private var loop = false
    private var fileLabel = "Sin archivo"
    private val prefs by lazy { getSharedPreferences("stylo_eq", MODE_PRIVATE) }
    private val graphicFreqs = doubleArrayOf(20,25,31.5,40,50,63,80,100,125,160,200,250,315,400,500,630,800,1000,1250,1600,2000,2500,3150,4000,5000,6300,8000,10000,12500,16000,20000)
    private val graphicBands = graphicFreqs.map { EqBand(it,0.0,1.0,FilterType.PEAK) }.toMutableList()
    private val paramBands = MutableList(8) { i -> EqBand(graphicFreqs[i+12],0.0,1.0,FilterType.PEAK) }
    private var abSnapshot: List<EqBand>? = null

    override fun onCreate(state: Bundle?) {
        super.onCreate(state)
        player = PcmPlayerEngine(this,
            onState = { pos,dur,isPlaying ->
                position=pos; duration=dur; playing=isPlaying
                runOnUiThread { refreshTransport() }
            },
            onSpectrum = { spectrum -> runOnUiThread { graph.setSpectrum(spectrum) } }
        )
        buildUi()
        applyDsp()
    }

    private fun buildUi() {
        val root=LinearLayout(this).apply{orientation=LinearLayout.VERTICAL;setBackgroundColor(Color.rgb(8,10,12));setPadding(10,8,10,8)}
        graph=EqGraphView(); root.addView(graph,LinearLayout.LayoutParams(-1,0,1f))
        val file=TextView(this).apply{text=fileLabel;setTextColor(0xffaebbc2.toInt());textSize=12f;maxLines=1;ellipsize=android.text.TextUtils.TruncateAt.MIDDLE;gravity=Gravity.CENTER_VERTICAL;setPadding(8,2,8,2)};root.addView(file,LinearLayout.LayoutParams(-1,32));fileView=file
        val seek=SeekBar(this).apply{max=1000;setOnSeekBarChangeListener(object:SeekBar.OnSeekBarChangeListener{override fun onProgressChanged(s:SeekBar?,p:Int,fromUser:Boolean){if(fromUser&&duration>0){position=duration*p/1000;player.seekTo(position);graph.invalidate()}};override fun onStartTrackingTouch(s:SeekBar?){ };override fun onStopTrackingTouch(s:SeekBar?){ }})};root.addView(seek,LinearLayout.LayoutParams(-1,38));seekBar=seek
        val time=TextView(this).apply{text="00:00 / 00:00";setTextColor(0xff83939b.toInt());textSize=11f;gravity=Gravity.CENTER};root.addView(time,LinearLayout.LayoutParams(-1,24));timeView=time
        fun row()=LinearLayout(this).apply{orientation=LinearLayout.HORIZONTAL;gravity=Gravity.CENTER;setPadding(0,3,0,3)}
        fun btn(label:String,action:()->Unit)=Button(this).apply{text=label;textSize=11f;isAllCaps=false;minHeight=48;minWidth=0;setPadding(3,0,3,0);setOnClickListener{action()}}
        fun addRow(r:LinearLayout,vararg bs:Button){bs.forEach{r.addView(it,LinearLayout.LayoutParams(0,52,1f).apply{setMargins(3,0,3,0)})};root.addView(r)}
        val transport=row();val open=btn("📂"){pickAudio()};val back=btn("⏮ 10s"){player.seekBy(-10000)};val play=btn("▶ PLAY"){player.playPause()};val fwd=btn("10s ⏭"){player.seekBy(10000)};val loopBtn=btn("↻ LOOP"){loop=!loop;player.setLoop(loop);loopBtn.text=if(loop)"↻ LOOP ON" else "↻ LOOP"};addRow(transport,open,back,play,fwd,loopBtn);playButton=play
        val modes=row();val graphic=btn("31 BAND"){graph.mode=0;graph.invalidate()};val param=btn("PARAM 8"){graph.mode=1;graph.invalidate()};val type=btn("TYPE"){graph.cycleType()};val bypassBtn=btn("BYPASS"){bypass=!bypass;player.setBypass(bypass);graph.invalidate()};val abBtn=btn("A / B"){toggleAB();abBtn.text=if(ab)"A / B: B" else "A / B: A"};addRow(modes,graphic,param,type,bypassBtn,abBtn)
        val utility=row();val save=btn("SAVE"){savePreset()};val load=btn("LOAD"){loadPreset()};val flat=btn("FLAT"){flatten()};val reset=btn("RESET"){resetSelected()};val prev=btn("◀ BAND"){graph.selected=(graph.selected-1).coerceAtLeast(0);graph.invalidate()};val next=btn("BAND ▶"){graph.selected=(graph.selected+1).coerceAtMost(if(graph.mode==0)30 else 7);graph.invalidate()};addRow(utility,save,load,flat,reset,prev,next)
        val volumeRow=row();val label=TextView(this).apply{text="VOL";setTextColor(0xffaebbc2.toInt());gravity=Gravity.CENTER};val volume=SeekBar(this).apply{max=100;progress=100;setOnSeekBarChangeListener(object:SeekBar.OnSeekBarChangeListener{override fun onProgressChanged(s:SeekBar?,p:Int,fromUser:Boolean){player.setVolume(p/100f)};override fun onStartTrackingTouch(s:SeekBar?){ };override fun onStopTrackingTouch(s:SeekBar?){ }})};volumeRow.addView(label,LinearLayout.LayoutParams(48,44));volumeRow.addView(volume,LinearLayout.LayoutParams(0,44,1f));root.addView(volumeRow);setContentView(root)
    }

    private lateinit var seekBar:SeekBar
    private lateinit var timeView:TextView
    private lateinit var fileView:TextView
    private lateinit var playButton:Button

    private fun refreshTransport(){if(duration>0)seekBar.progress=(position*1000/duration).coerceIn(0,1000);timeView.text="${fmt(position)} / ${fmt(duration)}";playButton.text=if(playing)"⏸ PAUSE" else "▶ PLAY";graph.invalidate()}
    private fun fmt(ms:Int):String{val s=(ms/1000).coerceAtLeast(0);return "%02d:%02d".format(s/60,s%60)}
    private fun pickAudio(){startActivityForResult(Intent(Intent.ACTION_OPEN_DOCUMENT).apply{type="audio/*";addCategory(Intent.CATEGORY_OPENABLE)},REQUEST_FILE)}
    override fun onActivityResult(requestCode:Int,resultCode:Int,data:Intent?){super.onActivityResult(requestCode,resultCode,data);if(requestCode==REQUEST_FILE&&resultCode==RESULT_OK)data?.data?.let{uri->runCatching{contentResolver.takePersistableUriPermission(uri,Intent.FLAG_GRANT_READ_URI_PERMISSION);player.load(uri);fileLabel=uri.lastPathSegment?:"Audio";fileView.text=fileLabel}.onFailure{Toast.makeText(this,"No se pudo abrir el audio",Toast.LENGTH_LONG).show()}}}
    private fun applyDsp(){player.setBands((graphicBands+paramBands).map{it.copy(enabled=it.enabled&&!bypass)});player.setBypass(bypass)}
    private fun selectedBand():EqBand=if(graph.mode==0)graphicBands[graph.selected]else paramBands[graph.selected.coerceIn(0,7)]
    private fun resetSelected(){selectedBand().apply{gainDb=0.0;q=1.0;type=FilterType.PEAK;enabled=true};applyDsp();graph.invalidate()}
    private fun flatten(){(graphicBands+paramBands).forEach{it.gainDb=0.0};applyDsp();graph.invalidate()}
    private fun savePreset(){fun enc(list:List<EqBand>)=JSONArray().also{a->list.forEach{b->a.put(JSONObject().apply{put("f",b.frequency);put("g",b.gainDb);put("q",b.q);put("t",b.type.name);put("e",b.enabled)})}};prefs.edit().putString("preset",JSONObject().apply{put("graphic",enc(graphicBands));put("param",enc(paramBands))}.toString()).apply();Toast.makeText(this,"Preset guardado",Toast.LENGTH_SHORT).show()}
    private fun loadPreset(){val raw=prefs.getString("preset",null)?:return;runCatching{val root=JSONObject(raw);fun dec(a:JSONArray,list:List<EqBand>){for(i in 0 until min(a.length(),list.size)){val o=a.getJSONObject(i);val b=list[i];b.frequency=o.optDouble("f",b.frequency);b.gainDb=o.optDouble("g",0.0);b.q=o.optDouble("q",1.0);b.type=runCatching{FilterType.valueOf(o.optString("t","PEAK"))}.getOrDefault(FilterType.PEAK);b.enabled=o.optBoolean("e",true)}};dec(root.optJSONArray("graphic")?:JSONArray(),graphicBands);dec(root.optJSONArray("param")?:JSONArray(),paramBands);applyDsp();graph.invalidate();Toast.makeText(this,"Preset cargado",Toast.LENGTH_SHORT).show()}}
    private fun toggleAB(){if(!ab){abSnapshot=(graphicBands+paramBands).map{it.copy()};(graphicBands+paramBands).forEach{it.gainDb=0.0}}else{abSnapshot?.forEachIndexed{i,b->(graphicBands+paramBands).getOrNull(i)?.apply{frequency=b.frequency;gainDb=b.gainDb;q=b.q;type=b.type;enabled=b.enabled}}};ab=!ab;applyDsp();graph.invalidate()}

    override fun onDestroy(){player.release();super.onDestroy()}

    private inner class EqGraphView:View(this@MainActivity){
        private val p=Paint(Paint.ANTI_ALIAS_FLAG)
        private var spectrum=FloatArray(1024)
        var mode=0
        var selected=17
        private var dragging=false
        private var qStart=1.0
        private var qY=0f
        private var graphScale=1f
        private var lastPinch=0f
        private val spectrumPaint=Paint(Paint.ANTI_ALIAS_FLAG).apply{style=Paint.Style.STROKE;strokeWidth=2f;color=0xff35d9ff.toInt()}
        private val curvePaint=Paint(Paint.ANTI_ALIAS_FLAG).apply{style=Paint.Style.STROKE;strokeWidth=3f;color=0xffffb84d.toInt()}
        private val gridPaint=Paint(Paint.ANTI_ALIAS_FLAG).apply{style=Paint.Style.STROKE;strokeWidth=1f;color=0x333b4d56}
        fun setSpectrum(v:FloatArray){spectrum=v;postInvalidateOnAnimation()}
        private fun xFreq(x:Float,w:Float)=20.0*exp(ln(1000.0)*x/(w.coerceAtLeast(1f)))
        private fun fX(f:Double,w:Float)=(ln(f/20.0)/ln(1000.0)*w).toFloat()
        private fun gainY(g:Double,h:Float)=h*.43f-(g/24.0*h*.32f)
        private fun yGain(y:Float,h:Float)=((h*.43f-y)/(h*.32f)*24.0).coerceIn(-24.0,24.0)
        private fun text(c:Canvas,s:String,x:Float,y:Float,size:Float){p.style=Paint.Style.FILL;p.color=0xffaebbc2.toInt();p.textSize=size;c.drawText(s,x,y,p)}
        override fun onDraw(c:Canvas){val w=width.toFloat();val h=height.toFloat();c.drawColor(0xff080a0c.toInt());for(i in 0..8){val y=h*(.08f+i*.84f/8f);c.drawLine(0f,y,w,y,gridPaint)};for(f in doubleArrayOf(20.0,50.0,100.0,200.0,500.0,1000.0,2000.0,5000.0,10000.0,20000.0)){val x=fX(f,w);c.drawLine(x,h*.06f,x,h*.92f,gridPaint);text(c,if(f>=1000)"${(f/1000).toInt()}k" else "${f.toInt()}",x-10,h-6,10f)};p.color=0xff7d8790.toInt();p.textSize=10f;c.drawText("+24 dB",5f,h*.10f,p);c.drawText("0 dB",5f,h*.43f,p);c.drawText("-24 dB",5f,h*.76f,p);spectrumPaint.strokeWidth=2f*graphScale;val path=Path();var started=false;for(i in 1 until spectrum.size){val f=i.toDouble()/spectrum.size*22050.0;if(f<20||f>20000)continue;val x=fX(f,w);val db=spectrum[i].coerceIn(-90f,0f);val y=h*.9f-(db+90f)/90f*h*.78f;if(!started){path.moveTo(x,y);started=true}else path.lineTo(x,y)};c.drawPath(path,spectrumPaint);val bands=if(mode==0)graphicBands else paramBands;bands.forEachIndexed{i,b->val x=fX(b.frequency,w);val y=gainY(b.gainDb,h);p.style=Paint.Style.FILL;p.color=if(i==selected)0xffff5c8a.toInt() else 0xff4fe3c1.toInt();c.drawCircle(x,y,if(i==selected)11f else 7f,p)};if(mode==1){val b=paramBands[selected];curvePaint.strokeWidth=2.5f*graphScale;val curve=Path();for(xi in 0..w.toInt() step 8){val f=xFreq(xi.toFloat(),w);val z=ln(f/b.frequency)/ln(2.0);val d=exp(-0.5*(z*b.q)*(z*b.q));val y=gainY(b.gainDb*d,h);if(xi==0)curve.moveTo(0f,y)else curve.lineTo(xi.toFloat(),y)};c.drawPath(curvePaint.apply{color=0xffffb84d.toInt()},curve)};text(c,"STYLO EQ",14f,24f,16f);val b=selectedBand();text(c,"${b.frequency.toInt()} Hz   ${if(b.gainDb>=0)"+" else ""}${"%.1f".format(b.gainDb)} dB   Q ${"%.2f".format(b.q)}   ${b.type.name}",14f,46f,12f);text(c,if(mode==0)"31-BAND GRAPHIC" else "PARAMETRIC 8",w-145f,24f,10f);if(bypass)text(c,"BYPASS",w-65f,46f,10f)}
        override fun onTouchEvent(e:MotionEvent):Boolean{val w=width.toFloat();val h=height.toFloat();when(e.actionMasked){MotionEvent.ACTION_DOWN->{selected=nearest(e.x,e.y,w,h);dragging=true;qStart=selectedBand().q;qY=e.y;invalidate();return true};MotionEvent.ACTION_POINTER_DOWN->{if(e.pointerCount>=2)lastPinch=distance(e);return true};MotionEvent.ACTION_MOVE->{if(e.pointerCount>=2){val d=distance(e);if(lastPinch>0){graphScale=(graphScale+d/lastPinch*.35f).coerceIn(.7f,1.8f)};lastPinch=d;postInvalidateOnAnimation();return true};if(dragging){val b=selectedBand();b.frequency=xFreq(e.x.coerceIn(0f,w),w).coerceIn(20.0,20000.0);b.gainDb=yGain(e.y,h);applyDsp();postInvalidateOnAnimation()};return true};MotionEvent.ACTION_POINTER_UP->{lastPinch=0f;return true};MotionEvent.ACTION_UP,MotionEvent.ACTION_CANCEL->{dragging=false;lastPinch=0f;return true}};return true}
        private fun distance(e:MotionEvent):Float{if(e.pointerCount<2)return 0f;val dx=e.getX(0)-e.getX(1);val dy=e.getY(0)-e.getY(1);return kotlin.math.sqrt(dx*dx+dy*dy)}
        private fun nearest(x:Float,y:Float,w:Float,h:Float):Int{val bands=if(mode==0)graphicBands else paramBands;var best=0;var dist=Float.MAX_VALUE;bands.forEachIndexed{i,b->{val dx=fX(b.frequency,w)-x;val dy=gainY(b.gainDb,h)-y;val d=dx*dx+dy*dy;if(d<dist){dist=d;best=i}}};return best}
        fun cycleType(){val b=selectedBand();b.type=FilterType.values()[(b.type.ordinal+1)%FilterType.values().size];applyDsp();invalidate()}
    }
    companion object{private const val REQUEST_FILE=701}
}
