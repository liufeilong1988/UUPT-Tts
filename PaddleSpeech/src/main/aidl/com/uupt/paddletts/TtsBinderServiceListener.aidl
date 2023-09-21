// TtsBinderServiceListener.aidl
package com.uupt.paddletts;
// Declare any non-default types here with import statements

interface TtsBinderServiceListener {

    void onStart();

    void onProgressChange(int process);

    void onFinish();

    void onError(int code, String message);

    void onCrash(String message);
}