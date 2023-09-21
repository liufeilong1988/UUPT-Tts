package com.uupt.paddle.tts;

import android.content.Context;

import com.uupt.tts.BaseTTS;
import com.uupt.tts.SynthesizerListener;

public class UuTensorTts extends BaseTTS {

    TensorTtsListener ttsListener;

    TensorServerTts tts;

    public UuTensorTts(Context context, TensorTtsCrashListener crashListener) {
        super(context);
        this.ttsListener = new TensorTtsListener(crashListener);
        this.tts = new TensorServerTts(context, this.ttsListener);
    }

    @Override
    public void setSynthesizerListener(SynthesizerListener listener) {
        this.ttsListener.setListener(listener);
        super.setSynthesizerListener(listener);
    }

    @Override
    public boolean Init() {
        return true;
    }

    @Override
    public void speakText(String text) {
        this.tts.speak(text);
    }

    @Override
    public boolean isSpeaking() {
        return this.tts.isSpeak();
    }

    @Override
    public void stopSpeaking() {
        this.tts.stop();
    }

    @Override
    public void Pause() {
        this.tts.pause();
    }

    @Override
    public void Resume() {
        this.tts.resume();
    }

    @Override
    public boolean isSupportChinese() {
        return true;
    }

    @Override
    public void onDestroy() {
        this.tts.release();
        this.ttsListener.release();
    }
}
