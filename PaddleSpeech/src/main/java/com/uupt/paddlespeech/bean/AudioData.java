package com.uupt.paddlespeech.bean;

public class AudioData {

    private String text;

    private float[] audio;

    private boolean isInterrupt;

    public AudioData(String text, float[] audio) {
        this.text = text;
        this.audio = audio;
        this.isInterrupt = false;
    }

    public String getText() {
        return text;
    }

    public float[] getAudio() {
        return audio;
    }

    public boolean isInterrupt() {
        return isInterrupt;
    }

    public void interrupt() {
        this.isInterrupt = true;
    }

}
