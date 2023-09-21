// TtsBinderService.aidl
package com.uupt.paddletts;
import com.uupt.paddletts.TtsBinderServiceListener;
interface TtsBinderService {
    //播报
    void speak(String text);
    //停止
    void stop();
    //是否播报
    boolean isSpeaking();
    //暂停
    void pause();
    //重新开始
    void resume();
    //监听器
    void setListener(TtsBinderServiceListener listener);
    //销毁
    void release();
}