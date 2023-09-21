package com.uupt.paddlespeech.process;

import com.baidu.paddle.lite.MobileConfig;
import com.baidu.paddle.lite.Tensor;
import com.uupt.paddle.Interpreter;
import com.uupt.paddlespeech.interpreter.FastSpeech2;
import com.uupt.paddlespeech.interpreter.MBMelGan;

import java.io.File;

public class TensorProcessor {

    private FastSpeech2 speech2;

    private MBMelGan mbMelGan;

    public TensorProcessor() {
        this.speech2 = new FastSpeech2();
        this.mbMelGan = new MBMelGan();
    }

    public void init(File modelPath1, File modelPath2, Interpreter.Options options) {
        this.speech2.init(modelPath1, options);
        this.mbMelGan.init(modelPath2, options);
    }

    public Tensor getMelSpectrogram(float[] inputIds, float speed) {
        return this.speech2.getMelSpectrogram(inputIds, speed);
    }

    public void cancel() {
        this.speech2.cancel();
        this.mbMelGan.cancel();
    }

    public float[] getAudio(Tensor input) {
        return this.mbMelGan.getAudio(input);
    }
}
