package com.uupt.paddle.tts.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.uupt.paddlespeech.TensorTts;
import com.uupt.paddlespeech.listener.impl.SpeechCrashImplListener;
import com.uupt.paddletts.TtsBinderService;
import com.uupt.paddletts.TtsBinderServiceListener;

public class TtsServices extends Service {

    private TensorTts finalsTts;

    private SpeechCrashImplListener mSpeechListener;

    @Override
    public void onCreate() {
        super.onCreate();
        this.finalsTts = new TensorTts(this.getApplicationContext());
        this.mSpeechListener = new SpeechCrashImplListener();
        this.finalsTts.setSpeechListener(this.mSpeechListener);
        this.finalsTts.setCrashListener(this.mSpeechListener);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return new TtsBinder(this.finalsTts, this.mSpeechListener);
    }

    public static class TtsBinder extends TtsBinderService.Stub {

        private TensorTts tts;

        private SpeechCrashImplListener mListener;

        public TtsBinder(TensorTts tts, SpeechCrashImplListener listener) {
            this.tts = tts;
            this.mListener = listener;
        }

        @Override
        public void speak(String text) throws RemoteException {
            if (this.tts != null) {
                this.tts.speakText(text);
            }
        }

        @Override
        public void stop() throws RemoteException {
            if (this.tts != null) {
                this.tts.stop();
            }
        }

        @Override
        public boolean isSpeaking() throws RemoteException {
            boolean result = false;
            if (this.tts != null) {
                result = this.tts.isSpeak();
            }
            return result;
        }

        @Override
        public void pause() throws RemoteException {
            if (this.tts != null) {
                this.tts.pause();
            }
        }

        @Override
        public void resume() throws RemoteException {
            if (this.tts != null) {
                this.tts.resume();
            }
        }

        @Override
        public void setListener(TtsBinderServiceListener speechListener) throws RemoteException {
            if (this.mListener != null) {
                this.mListener.setListener(speechListener);
            }
        }

        @Override
        public void release() throws RemoteException {
            if (this.mListener != null) {
                this.mListener.release();
                this.mListener = null;
            }
            this.tts = null;
        }
    }

    @Override
    public void onDestroy() {
        if (this.finalsTts != null) {
            this.finalsTts.release();
            this.finalsTts = null;
        }
        if (this.mSpeechListener != null) {
            this.mSpeechListener.release();
            this.mSpeechListener = null;
        }
        Log.e("Finals", "服务器停止");
        super.onDestroy();
    }
}
