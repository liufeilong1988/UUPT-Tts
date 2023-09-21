package com.uupt.paddlespeech;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;

public class TtsHandlerThread extends HandlerThread {

    private Handler handler;

    public TtsHandlerThread() {
        super("tensorThread");
    }

    public void init(OnMessageCallback callback) {
        this.onMessageCallback = callback;
        this.start();
        this.handler = new Handler(this.getLooper()) {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (onMessageCallback != null) {
                    onMessageCallback.handleMessage(msg.what, msg.obj);
                }
            }
        };
    }

    public void sendMessage(int messageId) {
        this.handler.removeMessages(messageId);
        this.handler.sendEmptyMessage(messageId);
    }

    public void sendMessage(int code, Object data) {
        this.handler.removeMessages(code);
        Message message = Message.obtain(this.handler, code, data);
        message.sendToTarget();
    }


    public void release() {
        this.handler.removeCallbacksAndMessages(null);
        this.onMessageCallback = null;
        this.quitSafely();
    }

    OnMessageCallback onMessageCallback;

    public void setMessageCallback(OnMessageCallback onMessageCallback) {
        this.onMessageCallback = onMessageCallback;
    }

    public static interface OnMessageCallback {
        void handleMessage(int code, Object data);
    }

}
