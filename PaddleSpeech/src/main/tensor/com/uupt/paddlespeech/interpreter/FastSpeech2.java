package com.uupt.paddlespeech.interpreter;

import android.util.Log;

import com.baidu.paddle.lite.Tensor;
import com.uupt.paddle.BuildConfig;
import com.uupt.paddle.Interpreter;

import java.io.File;
import java.util.concurrent.atomic.AtomicBoolean;

public class FastSpeech2 {

    private static final String TAG = "FastSpeech2";

    //region 构造函数

    public FastSpeech2() {
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

    //region 获取音频
    private AtomicBoolean isRun = new AtomicBoolean(false);

    /**
     * 获取音频
     *
     * @param inputIds
     * @param speed
     * @return
     */
    public Tensor getMelSpectrogram(float[] inputIds, float speed) {
        //初始化Tensor
       return this.interpreter.getPhoneIdOutput(inputIds);
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
                Log.e("Finals", "取消调用Speech");
            }
        } else {
            if (BuildConfig.DEBUG) {
                Log.d("Finals", "没有运行Speech");
            }
        }
    }
}
