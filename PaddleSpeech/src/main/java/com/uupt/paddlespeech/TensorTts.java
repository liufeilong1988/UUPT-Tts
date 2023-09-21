package com.uupt.paddlespeech;

import android.content.Context;
import android.util.Log;

import com.uupt.paddlespeech.listener.CrashListener;
import com.uupt.paddlespeech.listener.SpeechListener;

public class TensorTts {

    private TensorCore core;

    private TtsHandlerThread handlerThread;

    public TensorTts(Context context) {
        this.core = new TensorCore(context);
        this.handlerThread = new TtsHandlerThread();
        this.handlerThread.init(this::processMessage);
    }

    private void processMessage(int what, Object text) {
        if (what == 0) {
            if (this.core != null) {
                this.core.init();
            }
        } else if (what == TensorTts.START) {
            if (this.core != null) {
                this.core.speakText(lastText);
            }
        } else if (what == TensorTts.STOP) {
            if (this.core != null) {
                this.core.stop();
            }
        } else if (what == TensorTts.PAUSE) {
            if (this.core != null) {
                this.core.pause();
            }
        } else if (what == TensorTts.RESUME) {
            if (this.core != null) {
                this.core.resume();
            }
        }
    }

    //region 功能函数
    private String lastText;

    public void speakText(String text) {
        this.lastText = text;
        if (this.handlerThread != null) {
            this.handlerThread.sendMessage(TensorTts.START);
        } else {
            Log.e("Finals", "资源已经释放");
        }
    }

    public void pause() {
        if (this.handlerThread != null) {
            this.handlerThread.sendMessage(TensorTts.PAUSE);
        }
    }

    public void resume() {
        if (this.handlerThread != null) {
            this.handlerThread.sendMessage(TensorTts.RESUME);
        }
    }

    public void stop() {
        this.core.stop();
    }

    public boolean isSpeak() {
        boolean result = false;
        if (this.core != null) {
            result = this.core.isSpeak();
        }
        return result;
    }
    //endregion

    public void setSpeechListener(SpeechListener speechListener) {
        if (this.core != null) {
            this.core.setSpeechListener(speechListener);
        }
    }

    public void setCrashListener(CrashListener crashListener) {
        if (this.core != null) {
            this.core.setCrashListener(crashListener);
        }
    }

    public synchronized void release() {
        if (this.core != null) {
            this.core.release();
            this.core = null;
        }
        if (this.handlerThread != null) {
            this.handlerThread.release();
            this.handlerThread = null;
        }
    }

    public boolean isRelease() {
        return this.core == null;
    }

    private static final int START = 1;
    private static final int STOP = 2;
    private static final int PAUSE = 4;
    private static final int RESUME = 5;
}
