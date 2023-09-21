package com.uupt.paddle.tts;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;

import com.uupt.paddletts.TtsBinderServiceListener;
import com.uupt.tts.BaseTTS;
import com.uupt.tts.FSpeechError;
import com.uupt.tts.SynthesizerListener;

public class TensorTtsListener extends TtsBinderServiceListener.Stub {

    private TensorTtsCrashListener crashListener;

    private Handler handler;

    public TensorTtsListener(TensorTtsCrashListener crashListener) {
        super();
        this.handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                processCode(msg.what);
            }
        };
        this.crashListener = crashListener;
    }

    private void processCode(int what) {
        if (what == 1) {
            if (this.listener != null) {
                this.listener.onSpeechStart(this.baseTTS);
            }
        } else if (what == 2) {
            if (this.listener != null) {
                this.listener.onSpeechFinish(this.baseTTS);
            }
        }
    }

    private BaseTTS baseTTS;

    public void setBaseTTS(BaseTTS baseTTS) {
        this.baseTTS = baseTTS;
    }

    public SynthesizerListener listener;

    public void setListener(SynthesizerListener listener) {
        this.listener = listener;
    }

    @Override
    public void onStart() throws RemoteException {
        if (this.handler != null) {
            this.handler.sendEmptyMessage(1);
        }
    }

    @Override
    public void onProgressChange(int process) throws RemoteException {
        if (this.listener != null) {
            this.listener.onSpeechProgressChanged(this.baseTTS, process);
        }
    }

    @Override
    public void onFinish() throws RemoteException {
        if (this.handler != null) {
            this.handler.sendEmptyMessage(2);
        }
    }

    @Override
    public void onError(int code, String message) throws RemoteException {
        if (this.listener != null) {
            this.listener.onError(this.baseTTS, new FSpeechError(code, message));
        }
    }

    @Override
    public void onCrash(String message) throws RemoteException {
        if (this.crashListener != null) {
            this.crashListener.onCrash(message);
        }
    }

    public void release() {
        this.listener = null;
    }
}
