package com.uupt.paddlespeech.bean;

public class InputText {

    private String inputText;

    private float speed;

    private boolean isInterrupt;

    public InputText(String inputText, float speed) {
        this.inputText = inputText;
        this.speed = speed;
        this.isInterrupt = false;
    }

    public String getInputText() {
        return inputText;
    }

    public float getSpeed() {
        return speed;
    }

    public void release() {
        this.isInterrupt = true;
    }

    public boolean isInterrupt() {
        return isInterrupt;
    }
}
