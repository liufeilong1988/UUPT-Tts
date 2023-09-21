package com.uupt.paddlespeech.listener.impl;


import com.uupt.paddlespeech.listener.CrashListener;
import com.uupt.paddlespeech.listener.SpeechListener;
import com.uupt.paddletts.TtsBinderServiceListener;

public class SpeechCrashImplListener implements SpeechListener, CrashListener {

    private TtsBinderServiceListener listener;

    public void setListener(TtsBinderServiceListener listener) {
        this.listener = listener;
    }

    //region 播报
    @Override
    public void onSpeechStart(String utteranceId) {
        if (this.listener != null) {
            try {
                this.listener.onStart();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onSpeechProgressChanged(String utteranceId, int progress) {
        if (this.listener != null) {
            try {
                this.listener.onProgressChange(progress);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onSpeechFinish(String utteranceId) {
        if (this.listener != null) {
            try {
                this.listener.onFinish();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onError(String utteranceId, int code, String message) {
        if (this.listener != null) {
            try {
                this.listener.onError(code, message);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    //endregion

    //region 闪退
    @Override
    public void onCrash(String message) {
        if (this.listener != null) {
            try {
                this.listener.onCrash(message);
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }
    }
    //endregion

    public void release() {
        this.listener = null;
    }
}
