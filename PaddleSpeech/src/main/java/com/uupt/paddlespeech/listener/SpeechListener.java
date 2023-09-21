package com.uupt.paddlespeech.listener;

public interface SpeechListener {

    void onSpeechStart(String utteranceId);

    void onSpeechProgressChanged(String utteranceId, int progress);

    void onSpeechFinish(String utteranceId);

    void onError(String utteranceId, int code, String message);

}
