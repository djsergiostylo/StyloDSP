package com.stylo.dsp;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

public final class MainActivity extends Activity {
    static {
        System.loadLibrary("stylo_dsp_core");
        System.loadLibrary("stylo_android");
    }

    private TextView status;

    private native int nativeStart();
    private native void nativeStop();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        status = findViewById(R.id.status);
        Button start = findViewById(R.id.start_audio);
        Button stop = findViewById(R.id.stop_audio);

        start.setOnClickListener(v -> startAudio());
        stop.setOnClickListener(v -> stopAudio());
    }

    private void startAudio() {
        try {
            int result = nativeStart();
            if (result == 0) {
                status.setText("Estado: audio activo");
            } else {
                status.setText("Estado: error de audio (" + result + ")");
            }
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
