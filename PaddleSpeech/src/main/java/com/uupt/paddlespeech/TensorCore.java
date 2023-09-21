package com.uupt.paddlespeech;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.baidu.paddle.lite.MobileConfig;
import com.baidu.paddle.lite.PowerMode;
import com.baidu.paddle.lite.Tensor;
import com.uupt.paddle.BuildConfig;
import com.uupt.paddle.Interpreter;
import com.uupt.paddlespeech.bean.AudioData;
import com.uupt.paddlespeech.bean.InputText;
import com.uupt.paddlespeech.listener.CrashListener;
import com.uupt.paddlespeech.listener.SpeechListener;
import com.uupt.paddlespeech.process.TTSMediaProcessor;
import com.uupt.paddlespeech.process.TensorProcessor;
import com.uupt.paddlespeech.process.ZhProcessor;
import com.uupt.paddlespeech.utils.TtsFileUtils;
import com.uupt.paddlespeech.utils.TtsResource;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TensorCore {

    private static final String TAG = "InputWorker";

    private Context context;

    private ZhProcessor zhProcessor;

    private TensorProcessor tensorProcess;

    private TTSMediaProcessor ttsPlayer;

    public TensorCore(Context context) {
        this.context = context;
        this.zhProcessor = new ZhProcessor(context);
        this.tensorProcess = new TensorProcessor();
        this.ttsPlayer = new TTSMediaProcessor();
        this.ttsPlayer.setOnProgress(this::onProgress);
    }

    private void onProgress(int progress, String text) {
        if (this.speechListener != null) {
            this.speechListener.onSpeechProgressChanged(text, progress);
        }
    }

    //region 初始化函数
    private boolean isInit = false;

    private TtsFileUtils fileUtils;

    public boolean init() {
        if (!isInit) {
            File textModel = TtsFileUtils.getTtsFile(context, TtsResource.TEXT_MODEL_NAME);
            File femaleModel = TtsFileUtils.getTtsFile(context, TtsResource.SPEECH_FEMALE_MODEL_NAME);
            if (textModel != null && femaleModel != null) {
                if (!textModel.exists() || textModel.length() == 0) {
                    this.fileUtils = new TtsFileUtils(context, TtsResource.TEXT_MODEL_NAME, textModel);
                    this.fileUtils.copyFile();
                }
                if (!femaleModel.exists() || femaleModel.length() == 0) {
                    this.fileUtils = new TtsFileUtils(context, TtsResource.SPEECH_FEMALE_MODEL_NAME, femaleModel);
                    this.fileUtils.copyFile();
                }
                if (textModel.exists() && femaleModel.exists()) {
                    //开始初始化Tts库
                    isInit = initProcess(textModel, femaleModel);
                }
            }
        }
        return isInit;
    }

    public boolean initProcess(File modelPath1, File modelPath2) {
        boolean result = true;
        try {
            this.zhProcessor.init();
            this.tensorProcess.init(modelPath1, modelPath2, this.getOption());
        } catch (Exception e) {
            e.printStackTrace();
            result = false;
        }
        return result;
    }
    //endregion

    //region 设置音频速度
    private float speed = 1.0F;

    public void setSpeed(float speed) {
        this.speed = speed;
    }
    //endregion

    public void process(InputText inputText) {
        String text = inputText.getInputText();
        String[] sentences = text.split("[\n，。？?！!,;；]");

        if (BuildConfig.DEBUG) {
            Log.d(TAG, "speak: " + Arrays.toString(sentences));
        }

        if (inputText.isInterrupt()) {
            Log.d(TAG, "proceed: interrupt");
            return;
        }

        List<AudioData> list = new ArrayList<>();
        for (String sentence : sentences) {
            // 去掉无用的空格
            if (TextUtils.isEmpty(sentence.trim())) {
                continue;
            }
            sentence = sentence.trim();
            long time = System.currentTimeMillis();

            //获取输入的ID信息
            float[] inputIds = this.zhProcessor.text2ids(sentence);
            if (inputText.isInterrupt()) {
                Log.d(TAG, "proceed: interrupt");
                return;
            }

            //获取输出的语音信息
            Tensor output = this.tensorProcess.getMelSpectrogram(inputIds, inputText.getSpeed());
            if (inputText.isInterrupt()) {
                Log.d(TAG, "proceed: interrupt");
                return;
            }

            long encoderTime = System.currentTimeMillis();

            float[] audioData = this.tensorProcess.getAudio(output);

            if (inputText.isInterrupt()) {
                Log.d(TAG, "proceed: interrupt");
                return;
            }

            long vocoderTime = System.currentTimeMillis();
            if (BuildConfig.DEBUG) {
                Log.d(TAG, "Time cost: " + (encoderTime - time) + "+" + (vocoderTime - encoderTime) + "=" + (vocoderTime - time));
            }
            list.add(new AudioData(sentence, audioData));
        }
        this.ttsPlayer.play(list, text);
    }

    private Interpreter.Options getOption() {
        Interpreter.Options options = new Interpreter.Options();
        options.setNumThreads(5);
        options.setCancellable(true);
        return options;
    }

    private boolean isSpeak = false;

    private InputText lastText;

    public void speakText(String text) {
        this.isSpeak = true;
        if (this.speechListener != null) {
            this.speechListener.onSpeechStart(text);
        }
        this.init();
        if (this.isInit) {
            if (this.isSpeak()) {
                this.stop();
            }
        }
        try {
            if (!TextUtils.isEmpty(text)) {
                this.lastText = new InputText(text, this.speed);
                InputText inputText = lastText;
                this.process(inputText);
                if (!inputText.isInterrupt()) {
                    if (this.speechListener != null) {
                        this.speechListener.onSpeechFinish(text);
                    }
                } else {
                    if (BuildConfig.DEBUG) {
                        Log.e(TAG, "操作取消，onSpeechFinish不执行");
                    }
                }
            } else {
                if (this.speechListener != null) {
                    this.speechListener.onSpeechFinish(text);
                }
            }
            isSpeak = false;
        } catch (Exception e) {
            this.isSpeak = false;
            e.printStackTrace();
            this.reportCrash(e);
            if (this.speechListener != null) {
                this.speechListener.onError(text, -1, e.getMessage());
            }
        }
    }

    public void stop() {
        if (this.lastText != null) {
            this.lastText.release();
        }
        if (this.tensorProcess != null) {
            this.tensorProcess.cancel();
        }
        if (this.ttsPlayer != null) {
            this.ttsPlayer.stop();
        }
    }

    public void pause() {

    }

    public void resume() {

    }

    public boolean isSpeak() {
        return this.isSpeak;
    }

    public void release() {
        if (this.ttsPlayer != null) {
            this.ttsPlayer.release();
        }
        if (this.fileUtils != null) {
            this.fileUtils.release();
        }
    }

    private SpeechListener speechListener;

    public void setSpeechListener(SpeechListener speechListener) {
        this.speechListener = speechListener;
    }

    //region 闪退处理
    private void reportCrash(Exception e) {
        if (this.crashListener != null) {
            this.crashListener.onCrash(e.getMessage());
        }
    }

    private CrashListener crashListener;

    public void setCrashListener(CrashListener crashListener) {
        this.crashListener = crashListener;
    }
    //endregion
}
