package com.uupt.tts;

public interface SynthesizerListener {
    void onSpeechStart(BaseTTS baseTTS);

    void onSpeechProgressChanged(BaseTTS baseTTS, int process);

    void onSpeechFinish(BaseTTS baseTTS);

    void onError(BaseTTS baseTTS, FSpeechError fSpeechError);

}
