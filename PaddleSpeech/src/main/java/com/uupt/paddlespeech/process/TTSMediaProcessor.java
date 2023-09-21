package com.uupt.paddlespeech.process;

import android.media.AudioAttributes;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.util.Log;

import com.uupt.paddlespeech.bean.AudioData;

import java.util.List;

public class TTSMediaProcessor {

    private static final String TAG = "TtsPlayer";

    private final static int SAMPLE_RATE = 24000;

    private final static int FORMAT = AudioFormat.ENCODING_PCM_FLOAT;

    private final static int CHANNEL = AudioFormat.CHANNEL_OUT_MONO;

    private final int BUFFER_SIZE = AudioTrack.getMinBufferSize(SAMPLE_RATE, CHANNEL, FORMAT);

    private final AudioTrack mAudioTrack;

    public TTSMediaProcessor() {
        AudioAttributes attributes = new AudioAttributes
                .Builder()
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .build();
        AudioFormat audioFormat = new AudioFormat
                .Builder()
                .setSampleRate(SAMPLE_RATE)
                .setChannelMask(CHANNEL)
                .setEncoding(FORMAT)
                .build();
        this.mAudioTrack = new AudioTrack(attributes, audioFormat, BUFFER_SIZE, AudioTrack.MODE_STREAM, AudioManager.AUDIO_SESSION_ID_GENERATE);
    }

    private AudioData mCurrentAudioData;

    public void play(List<AudioData> take, String text) {
        this.mAudioTrack.play();
        if (this.onProgress != null) {
            this.onProgress.onProgress(0, text);
        }
        for (int i = 0; i < take.size(); i++) {
            mCurrentAudioData = take.get(i);
            Log.d(TAG, "playing: " + mCurrentAudioData.getText());
            int index = 0;
            while (true) {
                int length = mCurrentAudioData.getAudio().length;
                if (this.onProgress != null) {
                    this.onProgress.onProgress((int) (index / (float) length * 100), text);
                }
                if (!(index < length && !mCurrentAudioData.isInterrupt())) {
                    break;
                }
                int buffer = Math.min(BUFFER_SIZE, length - index);
                mAudioTrack.write(mCurrentAudioData.getAudio(), index, buffer, AudioTrack.WRITE_BLOCKING);
                mAudioTrack.flush();
                index += BUFFER_SIZE;
            }
        }
        this.mAudioTrack.stop();
    }

    public void stop() {
        if (this.mCurrentAudioData != null) {
            this.mCurrentAudioData.interrupt();
        }
        if (this.mAudioTrack != null) {
            if (this.mAudioTrack.getState() != AudioTrack.STATE_INITIALIZED) {
                Log.e("Finals", "没有初始化音频");
            } else {
                try {
                    this.mAudioTrack.stop();
                } catch (IllegalStateException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void release() {
        if (this.mAudioTrack != null) {
            this.mAudioTrack.release();
        }
    }

    private OnProgress onProgress;

    public void setOnProgress(OnProgress onProgress) {
        this.onProgress = onProgress;
    }

    public static interface OnProgress {

        void onProgress(int progress, String text);
    }
}
