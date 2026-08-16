package com.stylo.dsp;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

public final class MainActivity extends Activity {
    static {
        System.loadLibrary("stylo_dsp_core");
        System.loadLibrary("stylo_android");
    }

    private TextView status;
    private TextView gainValue;

    private native int nativeStart();
    private native void nativeStop();
    private native void nativeSetGainDb(float db);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        status = findViewById(R.id.status);
        gainValue = findViewById(R.id.gain_value);
        Button start = findViewById(R.id.start_audio);
        Button stop = findViewById(R.id.stop_audio);
        SeekBar gain = findViewById(R.id.gain_slider);

        start.setOnClickListener(v -> startAudio());
        stop.setOnClickListener(v -> stopAudio());

        gain.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                float db = -60.0f + (72.0f * progress / 100.0f);
                gainValue.setText(String.format(java.util.Locale.US, "Gain: %.1f dB", db));
                nativeSetGainDb(db);
            }
            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        gain.setProgress(83); // approximately 0 dB
    }

    private void startAudio() {
        try {
            int result = nativeStart();
            status.setText(result == 0 ? "Estado: audio activo" : "Estado: error de audio (" + result + ")");
        } catch (Throwable t) {
            status.setText("Estado: error controlado al iniciar audio");
        }
    }

    private void stopAudio() {
        try {
            nativeStop();
            status.setText("Estado: detenido");
        } catch (Throwable t) {
            status.setText("Estado: error controlado al detener audio");
        }
    }

    @Override
    protected void onStop() {
        stopAudio();
        super.onStop();
    }
}
