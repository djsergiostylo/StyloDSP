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
import kotlin.math.sqrt

class MainActivity : Activity() {
    private lateinit var graph: EqGraphView
    private lateinit var player: PcmPlayerEngine
    private lateinit var seekBar: SeekBar
    private lateinit var timeView: TextView
    private lateinit var fileView: TextView
    private lateinit var playButton: Button
    private lateinit var loopButton: Button
    private lateinit var abButton: Button

    private var playing = false
    private var duration = 0
    private var position = 0
    private var bypass = false
    private var ab = false
    private var loop = false
    private var fileLabel = "Sin archivo"
    private val prefs by lazy { getSharedPreferences("stylo_eq", MODE_PRIVATE) }
    private val graphicFreqs = doubleArrayOf(20.0,25.0,31.5,40.0,50.0,63.0,80.0,100.0,125.0,160.0,200.0,250.0,315.0,400.0,500.0,630.0,800.0,1000.0,1250.0,1600.0,2000.0,2500.0,3150.0,4000.0,5000.0,6300.0,8000.0,10000.0,12500.0,16000.0,20000.0)
    private val graphicBands = graphicFreqs.map { EqBand(it, 0.0) }.toMutableList()
    private val paramBands = MutableList(8) { index -> EqBand(graphicFreqs[index + 12], 0.0) }
    private var abSnapshot: List<EqBand>? = null

    override fun onCreate(state: Bundle?) {
        super.onCreate(state)
        player = PcmPlayerEngine(this,
            onState = { pos, dur, isPlaying ->
                position = pos
                duration = dur
                playing = isPlaying
                runOnUiThread { refreshTransport() }
            },
            onSpectrum = { spectrum -> runOnUiThread { if (::graph.isInitialized) graph.setSpectrum(spectrum) } }
        )
        buildUi()
        applyDsp()
    }

    private fun buildUi() {
        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(Color.rgb(8, 10, 12))
            setPadding(8, 6, 8, 6)
        }
        graph = EqGraphView()
        root.addView(graph, LinearLayout.LayoutParams(-1, 0, 1f))

        fileView = TextView(this).apply {
            text = fileLabel
            setTextColor(0xffaebbc2.toInt())
            textSize = 12f
            maxLines = 1
            ellipsize = android.text.TextUtils.TruncateAt.MIDDLE
            gravity = Gravity.CENTER_VERTICAL
            setPadding(8, 0, 8, 0)
        }
        root.addView(fileView, LinearLayout.LayoutParams(-1, 30))

        seekBar = SeekBar(this).apply {
            max = 1000
            setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(s: SeekBar?, progress: Int, fromUser: Boolean) {
                    if (fromUser && duration > 0) player.seekTo(duration * progress / 1000)
                }
                override fun onStartTrackingTouch(s: SeekBar?) {}
                override fun onStopTrackingTouch(s: SeekBar?) {}
            })
        }
        root.addView(seekBar, LinearLayout.LayoutParams(-1, 34))

        timeView = TextView(this).apply {
            text = "00:00 / 00:00"
            setTextColor(0xff83939b.toInt())
            textSize = 11f
            gravity = Gravity.CENTER
        }
        root.addView(timeView, LinearLayout.LayoutParams(-1, 22))

        fun row(): LinearLayout = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER
            setPadding(0, 2, 0, 2)
        }
        fun button(label: String, action: () -> Unit): Button = Button(this).apply {
            text = label
            textSize = 10f
            isAllCaps = false
            minHeight = 44
            minWidth = 0
            setPadding(1, 0, 1, 0)
            setOnClickListener { action() }
        }
        fun addButtons(r: LinearLayout, buttons: List<Button>) {
            buttons.forEach { b ->
                r.addView(b, LinearLayout.LayoutParams(0, 48, 1f).apply { setMargins(2, 0, 2, 0) })
            }
            root.addView(r)
        }

        val transport = row()
        val openButton = button("📂 OPEN") { pickAudio() }
        val backButton = button("⏮ 10s") { player.seekBy(-10_000) }
        playButton = button("▶ PLAY") { player.playPause() }
        val forwardButton = button("10s ⏭") { player.seekBy(10_000) }
        loopButton = button("↻ LOOP") {
            loop = !loop
            player.setLoop(loop)
            loopButton.text = if (loop) "↻ LOOP ON" else "↻ LOOP"
        }
        addButtons(transport, listOf(openButton, backButton, playButton, forwardButton, loopButton))

        val modes = row()
        val graphicButton = button("31 BAND") { graph.mode = 0; graph.invalidate() }
        val paramButton = button("PARAM 8") { graph.mode = 1; graph.selected = graph.selected.coerceIn(0, 7); graph.invalidate() }
        val typeButton = button("TYPE") { graph.cycleType() }
        val bypassButton = button("BYPASS") {
            bypass = !bypass
            player.setBypass(bypass)
            graph.invalidate()
        }
        abButton = button("A / B: A") { toggleAB() }
        addButtons(modes, listOf(graphicButton, paramButton, typeButton, bypassButton, abButton))

        val utility = row()
        val saveButton = button("SAVE") { savePreset() }
        val loadButton = button("LOAD") { loadPreset() }
        val flatButton = button("FLAT") { flatten() }
        val resetButton = button("RESET") { resetSelected() }
        val previousButton = button("◀ BAND") { graph.selected = (graph.selected - 1).coerceAtLeast(0); graph.invalidate() }
        val nextButton = button("BAND ▶") {
            val maxIndex = if (graph.mode == 0) 30 else 7
            graph.selected = (graph.selected + 1).coerceAtMost(maxIndex)
            graph.invalidate()
        }
        addButtons(utility, listOf(saveButton, loadButton, flatButton, resetButton, previousButton, nextButton))

        val volumeRow = row()
        val volumeLabel = TextView(this).apply {
            text = "VOL"
            setTextColor(0xffaebbc2.toInt())
            gravity = Gravity.CENTER
        }
        volumeRow.addView(volumeLabel, LinearLayout.LayoutParams(42, 44))
        val volume = SeekBar(this).apply {
            max = 100
            progress = 100
            setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(s: SeekBar?, progress: Int, fromUser: Boolean) { player.setVolume(progress / 100f) }
                override fun onStartTrackingTouch(s: SeekBar?) {}
                override fun onStopTrackingTouch(s: SeekBar?) {}
            })
        }
        volumeRow.addView(volume, LinearLayout.LayoutParams(0, 44, 1f))
        root.addView(volumeRow)
        setContentView(root)
    }

    private fun refreshTransport() {
        if (duration > 0) seekBar.progress = (position * 1000 / duration).coerceIn(0, 1000)
        timeView.text = "${fmt(position)} / ${fmt(duration)}"
        playButton.text = if (playing) "⏸ PAUSE" else "▶ PLAY"
        graph.invalidate()
    }

    private fun fmt(ms: Int): String {
        val seconds = (ms / 1000).coerceAtLeast(0)
        return "%02d:%02d".format(seconds / 60, seconds % 60)
    }

    private fun pickAudio() {
        startActivityForResult(Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            type = "audio/*"
            addCategory(Intent.CATEGORY_OPENABLE)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION)
        }, REQUEST_FILE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode != REQUEST_FILE || resultCode != RESULT_OK) return
        data?.data?.let { uri ->
            runCatching {
                contentResolver.takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
                player.load(uri)
                fileLabel = uri.lastPathSegment ?: "Audio"
                fileView.text = fileLabel
            }.onFailure {
                Toast.makeText(this, "No se pudo abrir el audio", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun allBands(): List<EqBand> = graphicBands + paramBands

    private fun applyDsp() {
        player.setBands(allBands().map { it.copy(enabled = it.enabled && !bypass) })
        player.setBypass(bypass)
    }

    private fun selectedBand(): EqBand = if (graph.mode == 0) {
        graphicBands[graph.selected.coerceIn(0, graphicBands.lastIndex)]
    } else {
        paramBands[graph.selected.coerceIn(0, paramBands.lastIndex)]
    }

    private fun resetSelected() {
        selectedBand().apply { gainDb = 0.0; q = 1.0; type = FilterType.PEAK; enabled = true }
        applyDsp()
        graph.invalidate()
    }

    private fun flatten() {
        allBands().forEach { it.gainDb = 0.0 }
        applyDsp()
        graph.invalidate()
    }

    private fun savePreset() {
        fun encode(list: List<EqBand>): JSONArray = JSONArray().apply {
            list.forEach { band ->
                put(JSONObject().apply {
                    put("f", band.frequency)
                    put("g", band.gainDb)
                    put("q", band.q)
                    put("t", band.type.name)
                    put("e", band.enabled)
                })
            }
        }
        val root = JSONObject().apply {
            put("graphic", encode(graphicBands))
            put("param", encode(paramBands))
        }
        prefs.edit().putString("preset", root.toString()).apply()
        Toast.makeText(this, "Preset guardado", Toast.LENGTH_SHORT).show()
    }

    private fun loadPreset() {
        val raw = prefs.getString("preset", null) ?: return
        runCatching {
            val root = JSONObject(raw)
            fun decode(array: JSONArray?, list: List<EqBand>) {
                if (array == null) return
                for (i in 0 until min(array.length(), list.size)) {
                    val obj = array.getJSONObject(i)
                    val band = list[i]
                    band.frequency = obj.optDouble("f", band.frequency)
                    band.gainDb = obj.optDouble("g", 0.0)
                    band.q = obj.optDouble("q", 1.0)
                    band.type = runCatching { FilterType.valueOf(obj.optString("t", "PEAK")) }.getOrDefault(FilterType.PEAK)
                    band.enabled = obj.optBoolean("e", true)
                }
            }
            decode(root.optJSONArray("graphic"), graphicBands)
            decode(root.optJSONArray("param"), paramBands)
            applyDsp()
            graph.invalidate()
            Toast.makeText(this, "Preset cargado", Toast.LENGTH_SHORT).show()
        }.onFailure {
            Toast.makeText(this, "Preset no válido", Toast.LENGTH_SHORT).show()
        }
    }

    private fun toggleAB() {
        if (!ab) {
            abSnapshot = allBands().map { it.copy() }
            allBands().forEach { it.gainDb = 0.0 }
            ab = true
            abButton.text = "A / B: B"
        } else {
            abSnapshot?.forEachIndexed { index, band ->
                allBands().getOrNull(index)?.apply {
                    frequency = band.frequency; gainDb = band.gainDb; q = band.q; type = band.type; enabled = band.enabled
                }
            }
            ab = false
            abButton.text = "A / B: A"
        }
        applyDsp()
        graph.invalidate()
    }

    override fun onDestroy() {
        player.release()
        super.onDestroy()
    }

    private inner class EqGraphView : View(this@MainActivity) {
        private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        private val spectrumPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { style = Paint.Style.STROKE; strokeWidth = 2f; color = 0xff35d9ff.toInt() }
        private val curvePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { style = Paint.Style.STROKE; strokeWidth = 2.5f; color = 0xffffb84d.toInt() }
        private val gridPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { style = Paint.Style.STROKE; strokeWidth = 1f; color = 0x333b4d56 }
        private var spectrum = FloatArray(1024)
        var mode = 0
        var selected = 17
        private var dragging = false
        private var graphScale = 1f
        private var lastPinch = 0f

        fun setSpectrum(value: FloatArray) { spectrum = value; postInvalidateOnAnimation() }

        private fun xToFreq(x: Float, width: Float): Double = 20.0 * exp(ln(1000.0) * x / width.coerceAtLeast(1f))
        private fun freqToX(freq: Double, width: Float): Float = (ln(freq / 20.0) / ln(1000.0) * width).toFloat()
        private fun gainToY(gain: Double, height: Float): Float = height * 0.43f - (gain / 24.0 * height * 0.32f)
        private fun yToGain(y: Float, height: Float): Double = ((height * 0.43f - y) / (height * 0.32f) * 24.0).coerceIn(-24.0, 24.0)

        override fun onDraw(canvas: Canvas) {
            val width = width.toFloat()
            val height = height.toFloat()
            canvas.drawColor(0xff080a0c.toInt())
            for (i in 0..8) {
                val y = height * (0.08f + i * 0.84f / 8f)
                canvas.drawLine(0f, y, width, y, gridPaint)
            }
            val gridFreqs = doubleArrayOf(20.0, 50.0, 100.0, 200.0, 500.0, 1000.0, 2000.0, 5000.0, 10000.0, 20000.0)
            gridFreqs.forEach { frequency ->
                val x = freqToX(frequency, width)
                canvas.drawLine(x, height * 0.06f, x, height * 0.92f, gridPaint)
                val label = if (frequency >= 1000.0) "${(frequency / 1000.0).toInt()}k" else frequency.toInt().toString()
                drawText(canvas, label, x - 10f, height - 6f, 10f)
            }
            paint.color = 0xff7d8790.toInt(); paint.textSize = 10f
            canvas.drawText("+24 dB", 5f, height * 0.10f, paint)
            canvas.drawText("0 dB", 5f, height * 0.43f, paint)
            canvas.drawText("-24 dB", 5f, height * 0.76f, paint)

            spectrumPaint.strokeWidth = 2f * graphScale
            val spectrumPath = Path()
            var started = false
            for (i in 1 until spectrum.size) {
                val frequency = i.toDouble() / spectrum.size.toDouble() * 22050.0
                if (frequency < 20.0 || frequency > 20000.0) continue
                val x = freqToX(frequency, width)
                val db = spectrum[i].coerceIn(-90f, 0f)
                val y = height * 0.9f - (db + 90f) / 90f * height * 0.78f
                if (!started) { spectrumPath.moveTo(x, y); started = true } else spectrumPath.lineTo(x, y)
            }
            canvas.drawPath(spectrumPath, spectrumPaint)

            val bands = if (mode == 0) graphicBands else paramBands
            bands.forEachIndexed { index, band ->
                val x = freqToX(band.frequency, width)
                val y = gainToY(band.gainDb, height)
                paint.style = Paint.Style.FILL
                paint.color = if (index == selected) 0xffff5c8a.toInt() else 0xff4fe3c1.toInt()
                canvas.drawCircle(x, y, if (index == selected) 11f else 7f, paint)
            }

            if (mode == 1) {
                val band = paramBands[selected.coerceIn(0, paramBands.lastIndex)]
                val curve = Path()
                var first = true
                var x = 0
                while (x <= width.toInt()) {
                    val frequency = xToFreq(x.toFloat(), width)
                    val z = ln(frequency / band.frequency) / ln(2.0)
                    val response = exp(-0.5 * (z * band.q) * (z * band.q))
                    val y = gainToY(band.gainDb * response, height)
                    if (first) { curve.moveTo(x.toFloat(), y); first = false } else curve.lineTo(x.toFloat(), y)
                    x += 8
                }
                canvas.drawPath(curve, curvePaint)
            }

            drawText(canvas, "STYLO EQ", 14f, 24f, 16f)
            val band = selectedBand()
            val gain = if (band.gainDb >= 0) "+%.1f".format(band.gainDb) else "%.1f".format(band.gainDb)
            drawText(canvas, "${band.frequency.toInt()} Hz   $gain dB   Q ${"%.2f".format(band.q)}   ${band.type.name}", 14f, 46f, 12f)
            drawText(canvas, if (mode == 0) "31-BAND GRAPHIC" else "PARAMETRIC 8", width - 145f, 24f, 10f)
            if (bypass) drawText(canvas, "BYPASS", width - 65f, 46f, 10f)
        }

        private fun drawText(canvas: Canvas, text: String, x: Float, y: Float, size: Float) {
            paint.style = Paint.Style.FILL
            paint.color = 0xffaebbc2.toInt()
            paint.textSize = size
            canvas.drawText(text, x, y, paint)
        }

        override fun onTouchEvent(event: MotionEvent): Boolean {
            val width = width.toFloat()
            val height = height.toFloat()
            when (event.actionMasked) {
                MotionEvent.ACTION_DOWN -> {
                    selected = nearest(event.x, event.y, width, height)
                    dragging = true
                    invalidate()
                    return true
                }
                MotionEvent.ACTION_POINTER_DOWN -> {
                    if (event.pointerCount >= 2) lastPinch = distance(event)
                    return true
                }
                MotionEvent.ACTION_MOVE -> {
                    if (event.pointerCount >= 2) {
                        val distance = distance(event)
                        if (lastPinch > 0f) graphScale = (graphScale * (distance / lastPinch)).coerceIn(0.7f, 1.8f)
                        lastPinch = distance
                        postInvalidateOnAnimation()
                        return true
                    }
                    if (dragging) {
                        val band = selectedBand()
                        band.frequency = xToFreq(event.x.coerceIn(0f, width), width).coerceIn(20.0, 20000.0)
                        band.gainDb = yToGain(event.y, height)
                        applyDsp()
                        postInvalidateOnAnimation()
                    }
                    return true
                }
                MotionEvent.ACTION_POINTER_UP -> { lastPinch = 0f; return true }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> { dragging = false; lastPinch = 0f; return true }
            }
            return true
        }

        private fun distance(event: MotionEvent): Float {
            if (event.pointerCount < 2) return 0f
            val dx = event.getX(0) - event.getX(1)
            val dy = event.getY(0) - event.getY(1)
            return sqrt(dx * dx + dy * dy)
        }

        private fun nearest(x: Float, y: Float, width: Float, height: Float): Int {
            val bands = if (mode == 0) graphicBands else paramBands
            var best = 0
            var bestDistance = Float.MAX_VALUE
            bands.forEachIndexed { index, band ->
                val dx = freqToX(band.frequency, width) - x
                val dy = gainToY(band.gainDb, height) - y
                val distance = dx * dx + dy * dy
                if (distance < bestDistance) { bestDistance = distance; best = index }
            }
            return best
        }

        fun cycleType() {
            val band = selectedBand()
            val values = FilterType.values()
            band.type = values[(band.type.ordinal + 1) % values.size]
            applyDsp()
            invalidate()
        }
    }

    companion object { private const val REQUEST_FILE = 701 }
}
