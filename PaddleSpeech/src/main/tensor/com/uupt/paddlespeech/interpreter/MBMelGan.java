package com.uupt.paddlespeech.interpreter;

import android.util.Log;

import com.baidu.paddle.lite.Tensor;
import com.uupt.paddle.BuildConfig;
import com.uupt.paddle.Interpreter;

import java.io.File;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 */
public class MBMelGan {

    private static final String TAG = "MBMelGan";

    //region 构造函数

    public MBMelGan() {
        super();
    }
    //endregion

    //region 初始化
    private Interpreter interpreter;

    public boolean init(File modelFile, Interpreter.Options options) {
        this.interpreter = new Interpreter(modelFile, options);
        if (BuildConfig.DEBUG) {
            this.showSensorInfo();
        }
        return true;
    }

    private void showSensorInfo() {

    }
    //endregion

    //region 获取音频数据
    private AtomicBoolean isRun = new AtomicBoolean(false);

    /**
     * 获取音频数据
     *
     * @param input
     * @return
     */
    public float[] getAudio(Tensor input) {
        //初始化Tensor
        return this.interpreter.getVoiceOutput(input);
    }
    //endregion

    public void cancel() {
        if (this.isRun.get()) {
            try {
                this.interpreter.setCancelled(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (BuildConfig.DEBUG) {
                Log.e("Finals", "取消调用MelGan");
            }
        } else {
            if (BuildConfig.DEBUG) {
                Log.d("Finals", "没有运行MelGan");
            }
        }
    }
}
