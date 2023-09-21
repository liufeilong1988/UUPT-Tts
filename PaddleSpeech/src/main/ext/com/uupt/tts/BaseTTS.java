package com.uupt.tts;

import android.content.Context;

public abstract class BaseTTS {

    protected Context context;

    public BaseTTS(Context context) {
        this.context = context;
    }

    public boolean Init() {
        return false;
    }

    public abstract void speakText(String text);

    public abstract boolean isSpeaking();

    public abstract void stopSpeaking();

    public abstract void Pause();

    public abstract void Resume();

    public abstract boolean isSupportChinese();

    public abstract void onDestroy();

    protected SynthesizerListener listener;

    public void setSynthesizerListener(SynthesizerListener listener) {
        this.listener = listener;
    }
}
