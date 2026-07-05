package at.fhj.peakflowmate.audio;

import android.Manifest;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.util.Log;

import androidx.annotation.RequiresPermission;

public class AudioAnalyser {

    public interface OnExhaleDetected {
        void onSuccess(String quality);

        void onFailure();
    }

    private static final int SAMPLE_RATE = 44100;
    private static final int BUFFER_SIZE = AudioRecord.getMinBufferSize(
            SAMPLE_RATE,
            AudioFormat.CHANNEL_IN_MONO,
            AudioFormat.ENCODING_PCM_16BIT
    );

    private static final int THRESHOLD_GOOD = 4000;
    private static final int THRESHOLD_WEAK = 2000;

    private static final int MIN_DURATION_MS = 200;
    private static final int MAX_DURATION_MS = 3000;

    private AudioRecord audioRecord;
    private boolean isRecording = false;
    private final OnExhaleDetected listener;

    public AudioAnalyser(OnExhaleDetected listener) {
        this.listener = listener;
    }

    @RequiresPermission(Manifest.permission.RECORD_AUDIO)
    public void start() {
        audioRecord = new AudioRecord(
                MediaRecorder.AudioSource.MIC,
                SAMPLE_RATE,
                AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT,
                BUFFER_SIZE
        );

        audioRecord.startRecording();
        isRecording = true;

        new Thread(this::analyse).start();
    }

    private void analyse() {
        short[] buffer = new short[BUFFER_SIZE];
        long exhaleStart = -1;
        long lastActiveTime = -1;
        long listenStart = System.currentTimeMillis();
        int peakRms = 0;

        while (isRecording) {
            int read = audioRecord.read(buffer, 0, BUFFER_SIZE);
            if (read <= 0) continue;

            int rms = calculateRms(buffer, read);

            if (exhaleStart < 0) {
                long waitDuration = System.currentTimeMillis() - listenStart;
                if (waitDuration >= 10000) {
                    stop();
                    listener.onFailure();
                    return;
                }
            }

            if (rms >= THRESHOLD_WEAK) {
                if (exhaleStart < 0) {
                    exhaleStart = System.currentTimeMillis();
                }
                lastActiveTime = System.currentTimeMillis();
                if (rms > peakRms) peakRms = rms;

                long duration = System.currentTimeMillis() - exhaleStart;
                if (duration >= MAX_DURATION_MS) {
                    stop();
                    String quality = peakRms >= THRESHOLD_GOOD ? "good" : "weak";
                    listener.onSuccess(quality);
                }


            } else if (exhaleStart > 0) {

                long duration = System.currentTimeMillis() - exhaleStart;
                long silenceDuration = System.currentTimeMillis() - lastActiveTime;

                if (duration >= MIN_DURATION_MS) {
                    stop();
                    String quality = peakRms >= THRESHOLD_GOOD ? "good" : "weak";
                    listener.onSuccess(quality);
                } else if (silenceDuration >= 500){
                    exhaleStart = -1;
                    lastActiveTime = -1;
                    peakRms = 0;
                    listener.onFailure();
                }
            }
        }
    }

            private int calculateRms ( short[] buffer, int read){
                long sum = 0;
                for (int i = 0; i < read; i++) {
                    sum += buffer[i] * buffer[i];
                }
                return (int) Math.sqrt((double) sum / read);
            }

            public void stop () {
                isRecording = false;
                if (audioRecord != null) {
                    audioRecord.stop();
                    audioRecord.release();
                    audioRecord = null;
                }
            }

        }
