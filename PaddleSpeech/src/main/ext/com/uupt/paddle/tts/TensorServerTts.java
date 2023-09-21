package com.uupt.paddle.tts;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Log;

import com.uupt.paddle.tts.service.TtsServices;
import com.uupt.paddletts.TtsBinderService;
import com.uupt.paddletts.TtsBinderServiceListener;

public class TensorServerTts {

    private Context context;

    private TtsBinderServiceListener speechListener;

    private boolean isRelease = false;

    public TensorServerTts(Context context, TtsBinderServiceListener speechListener) {
        this.context = context;
        this.speechListener = speechListener;
        this.isRelease = false;
    }

    private ServiceConnection connection;

    private TtsBinderService serviceImpl;

    private synchronized boolean connection() {
        if (this.connection == null) {
            this.connection = new ServiceConnection() {
                @Override
                public void onServiceConnected(ComponentName name, IBinder service) {
                    createBinder(service);
                    Log.e("Finals", "onServiceConnected: ");
                }

                @Override
                public void onServiceDisconnected(ComponentName name) {
                    releaseServiceImpl();
                    Log.e("Finals", "onServiceDisconnected: ");
                }
            };
        }
        Intent intent = new Intent(this.context, TtsServices.class);
        boolean success = false;
        try {
            success = this.context.bindService(intent, connection, Context.BIND_AUTO_CREATE);
        } catch (Exception exception) {
            exception.printStackTrace();
            this.reportError(exception);
        }
        return success;
    }

    private void createBinder(IBinder service) {
        try {
            this.serviceImpl = TtsBinderService.Stub.asInterface(service);
            this.serviceImpl.setListener(this.speechListener);
            if (!TextUtils.isEmpty(this.lastText)) {
                this.serviceImpl.speak(this.lastText);
            }
        } catch (Exception exception) {
            exception.printStackTrace();
            this.reportError(exception);
        }
    }

    private String lastText;

    public void speak(String text) {
        if (!this.isRelease) {
            if (this.serviceImpl == null) {
                this.lastText = text;
                this.connection();
            } else {
                try {
                    if (this.isAlive()) {
                        this.serviceImpl.speak(text);
                    }
                } catch (Exception exception) {
                    exception.printStackTrace();
                    this.reportError(exception);
                }
            }
        } else {
            Log.e("Finals", "资源已经释放");
        }
    }

    //region 暂停重启
    public boolean isSpeak() {
        boolean result = false;
        try {
            if (this.isAlive()) {
                result = this.serviceImpl.isSpeaking();
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return result;
    }

    public void pause() {
        try {
            if (this.isAlive()) {
                this.serviceImpl.pause();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void resume() {
        try {
            if (this.isAlive()) {
                this.serviceImpl.resume();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void stop() {
        try {
            if (this.isAlive()) {
                this.serviceImpl.stop();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //endregion

    public synchronized void release() {
        releaseServiceImpl();
        try {
            if (this.connection != null) {
                this.context.unbindService(this.connection);
            }
            this.isRelease = true;
        } catch (Exception exception) {
            exception.printStackTrace();
            this.reportError(exception);
        }
        this.speechListener = null;
    }

    private void releaseServiceImpl() {
        try {
            if (isAlive()) {
                this.serviceImpl.release();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.serviceImpl = null;
    }

    public boolean isRelease() {
        return this.isRelease;
    }

    private boolean isAlive() {
        boolean result = false;
        if (this.serviceImpl != null && this.serviceImpl.asBinder().isBinderAlive()) {
            result = true;
        }
        return result;
    }

    //region 上报异常
    private void reportError(Throwable throwable) {
        if (this.speechListener != null) {
            try {
                this.speechListener.onCrash(throwable.getMessage());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    //endregion
}