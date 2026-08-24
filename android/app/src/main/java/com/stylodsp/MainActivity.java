package com.stylodsp;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Process;
import android.view.MotionEvent;
import android.view.View;
import android.graphics.*;
import java.util.Arrays;

public final class MainActivity extends Activity {
    EqView view;
    @Override public void onCreate(Bundle b) { super.onCreate(b); view = new EqView(); setContentView(view); }
    @Override protected void onResume() { super.onResume(); if (checkSelfPermission(Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) view.startAudio(); else requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO}, 7); }
    @Override protected void onPause() { view.stopAudio(); super.onPause(); }

    final class EqView extends View {
        final Paint p = new Paint(Paint.ANTI_ALIAS_FLAG); final Path spectrum = new Path(); final Path curve = new Path();
        final int n = 1024; final float[] fft = new float[n]; final float[] window = new float[n];
        volatile float[] mags = new float[n/2]; volatile boolean running; AudioRecord record; Thread audioThread;
        float bandFreq=1000f, bandGain=0f, bandQ=.707f; boolean selected; float downX, downY;
        EqView(){ super(MainActivity.this); p.setTypeface(Typeface.create("sans",Typeface.NORMAL)); for(int i=0;i<n;i++) window[i]=(float)(.5-.5*Math.cos(2*Math.PI*i/(n-1))); setBackgroundColor(Color.rgb(11,13,16)); }
        void startAudio(){ if(running || checkSelfPermission(Manifest.permission.RECORD_AUDIO)!=PackageManager.PERMISSION_GRANTED)return; int min=AudioRecord.getMinBufferSize(48000,AudioFormat.CHANNEL_IN_MONO,AudioFormat.ENCODING_PCM_16BIT); if(min<=0)return; record=new AudioRecord(MediaRecorder.AudioSource.DEFAULT,48000,AudioFormat.CHANNEL_IN_MONO,AudioFormat.ENCODING_PCM_16BIT,Math.max(min,n*2)); record.startRecording(); running=true; audioThread=new Thread(()->{ Process.setThreadPriority(Process.THREAD_PRIORITY_AUDIO); short[] buf=new short[n]; while(running){ int got=record.read(buf,0,n,AudioRecord.READ_BLOCKING); if(got>0){ for(int i=0;i<n;i++) fft[i]=(i<got?buf[i]/32768f:0)*window[i]; computeFFT(); postInvalidateOnAnimation(); } } }); audioThread.start(); }
        void stopAudio(){ running=false; if(record!=null){try{record.stop();}catch(Exception ignored){} record.release();record=null;} }
        void computeFFT(){ int j=0; for(int i=0;i<n;i++){ if(i<j){float t=fft[i];fft[i]=fft[j];fft[j]=t;} int bit=n>>1; while((j&bit)!=0){j^=bit;bit>>=1;} j^=bit;} for(int len=2;len<=n;len<<=1){double a=-2*Math.PI/len; for(int i=0;i<n;i+=len){for(int k=0;k<len/2;k++){double c=Math.cos(a*k),s=Math.sin(a*k);float re=fft[i+k+len/2]* (float)c; float im=fft[i+k+len/2]*(float)s; float u=fft[i+k]; fft[i+k]=u+re; fft[i+k+len/2]=u-re;}}} float[] out=new float[n/2]; for(int i=1;i<n/2;i++){float v=Math.abs(fft[i])/(n/2); out[i]=(float)Math.max(0,20*Math.log10(v+1e-7));} mags=out; }
        float fx(float f,float w){return (float)(Math.log10(f/20f)/3*Math.max(1,w));}
        float freqAt(float x,float w){return (float)(20*Math.pow(1000,x/w));}
        float gy(float db,float h){return h*.52f-db*(h*.72f/48f);}
        @Override protected void onDraw(Canvas c){ super.onDraw(c); float w=getWidth(), h=getHeight(); float top=70, bottom=h-150, gh=bottom-top; p.setStyle(Paint.Style.STROKE); p.setStrokeWidth(1); p.setColor(Color.rgb(42,46,52)); for(int db=-24;db<=24;db+=6){float y=gy(db,gh)+top;c.drawLine(0,y,w,y,p);} float[] fs={20,100,1000,10000,20000}; p.setStyle(Paint.Style.FILL);p.setTextSize(25);p.setColor(Color.rgb(150,156,165)); for(float f:fs){float x=fx(f,w);c.drawText(f>=1000?(f/1000)+"k":Integer.toString((int)f),x+3,bottom+32,p);} p.setTextSize(22); for(int db=-24;db<=24;db+=6)c.drawText(Integer.toString(db),8,gy(db,gh)+top-5,p);
            spectrum.reset(); boolean first=true; float sr=48000; for(int i=1;i<n/2;i++){float f=i*sr/n;if(f<20||f>20000)continue;float x=fx(f,w);float db=Math.max(-48,Math.min(0,mags[i]));float y=top+gh*(1-(db+48)/48f);if(first){spectrum.moveTo(x,y);first=false;}else spectrum.lineTo(x,y);} p.setColor(Color.rgb(70,150,190));p.setStrokeWidth(2.2f);c.drawPath(spectrum,p);
            curve.reset(); first=true; for(int i=0;i<=240;i++){float f=(float)(20*Math.pow(1000,i/240f));float x=fx(f,w);float d=bandGain/(1+Math.pow((f/bandFreq-bandFreq/f),2)*bandQ*bandQ);float y=top+gy(d,gh);if(first){curve.moveTo(x,y);first=false;}else curve.lineTo(x,y);} p.setColor(Color.rgb(230,235,240));p.setStrokeWidth(4);c.drawPath(curve,p);
            float bx=fx(bandFreq,w), by=top+gy(bandGain,gh);p.setStyle(Paint.Style.FILL);p.setColor(selected?Color.rgb(70,220,180):Color.rgb(90,150,200));c.drawCircle(bx,by,selected?18:14,p);p.setColor(Color.WHITE);p.setTextSize(26);c.drawText(String.format("%.0f Hz   %+.1f dB   Q %.2f",bandFreq,bandGain,bandQ),24,h-92,p);p.setTextSize(20);p.setColor(Color.rgb(145,150,160));c.drawText(running?"MIC / ANALYZER  •  REALTIME":"MIC / ANALYZER  •  STOPPED",24,h-58,p);p.setTextSize(18);c.drawText("STYLO EQ   •   FFT 1024   •   20 Hz—20 kHz",24,36,p);p.setStyle(Paint.Style.STROKE);p.setColor(Color.rgb(60,65,72));c.drawRoundRect(16,50,w-16,h-115,18,18,p);p.setStyle(Paint.Style.FILL); }
        @Override public boolean onTouchEvent(MotionEvent e){float w=getWidth(),h=getHeight(),top=70,gh=h-220;float bx=fx(bandFreq,w),by=top+gy(bandGain,gh);if(e.getAction()==MotionEvent.ACTION_DOWN){downX=e.getX();downY=e.getY();selected=Math.hypot(e.getX()-bx,e.getY()-by)<60;invalidate();return true;} if(e.getAction()==MotionEvent.ACTION_MOVE&&selected){bandFreq=Math.max(20,Math.min(20000,freqAt(e.getX(),w)));bandGain=Math.max(-24,Math.min(24,(top+gh*.52f-e.getY())/(gh*.72f/48f)));invalidate();return true;} if(e.getAction()==MotionEvent.ACTION_UP){selected=false;invalidate();return true;}return true; }
    }
}
