package com.uupt.paddle;

import com.baidu.paddle.lite.MobileConfig;
import com.baidu.paddle.lite.PaddlePredictor;
import com.baidu.paddle.lite.PowerMode;
import com.baidu.paddle.lite.Tensor;

import java.io.File;

public class Interpreter {

    private PaddlePredictor paddlePredictor;

    public Interpreter(File file, Options options) {
        MobileConfig config = new MobileConfig();
        config.setModelFromFile(file.getAbsolutePath());
        config.setPowerMode(PowerMode.LITE_POWER_NO_BIND);
        config.setThreads(options.getThreads());
        this.paddlePredictor = PaddlePredictor.createPaddlePredictor(config);
    }

    /**
     * 获取输入流
     *
     * @param id
     * @return
     */
    public Tensor getInputTensor(int id) {
        return this.paddlePredictor.getInput(id);
    }


    /**
     * 获取输出流
     *
     * @param id
     * @return
     */
    public Tensor getOutputTensor(int id) {
        return this.paddlePredictor.getOutput(id);
    }

    /**
     * 开始计算
     *
     * @return
     */
    public boolean run() {
        return this.paddlePredictor.run();
    }

    public Tensor getPhoneIdOutput(float[] phones) {
        long[] dims = {phones.length};
        // 处理Tensor
        Tensor phones_handle = this.getInputTensor(0);
        phones_handle.resize(dims);
        phones_handle.setData(phones);
        this.run();

        // 处理输出
        Tensor am_output_handle = this.getOutputTensor(0);
        float[] am_output_data = am_output_handle.getFloatData();
        return am_output_handle;
    }

    public float[] getVoiceOutput(Tensor input) {
        long[] dims = input.shape();
        float[] am_output_data = input.getFloatData();
        // 处理Tensor
        Tensor mel_handle = this.getInputTensor(0);
        mel_handle.resize(dims);
        mel_handle.setData(am_output_data);
        this.run();
        // 处理输出
        Tensor voc_output_handle = this.getOutputTensor(0);
        return voc_output_handle.getFloatData();
    }

    //region 是否可以取消
    private boolean cancelable = false;

    public void setCancelled(boolean cancelable) {
        this.cancelable = cancelable;
    }
    //endregion

    public static class Options {

        //region 设置线程数量
        private int threads = 1;

        public void setNumThreads(int threads) {
            this.threads = threads;
        }

        public int getThreads() {
            return threads;
        }
        //endregion

        //region 是否可以取消
        private boolean cancelable;

        public void setCancellable(boolean cancelable) {
            this.cancelable = cancelable;
        }

        public boolean isCancelable() {
            return cancelable;
        }
        //endregion
    }
}
