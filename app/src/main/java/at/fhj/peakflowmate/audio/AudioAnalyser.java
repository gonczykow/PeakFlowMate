package at.fhj.peakflowmate.audio;

import android.Manifest;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.util.Log;

import androidx.annotation.RequiresPermission;

/**
 * Analysiert Audiodaten des Mikrofons, um ein Ausatemmanöver
 * während einer Peak-Flow-Messung zu erkennen.
 * <p>
 * Die Klasse überwacht kontinuierlich die Lautstärke des
 * Mikrofonsignals und bewertet anhand der Signalstärke und
 * der Dauer des Ausatmens dessen Qualität.
 */
public class AudioAnalyser {

    /**
     * Callback-Schnittstelle zur Benachrichtigung über das Ergebnis
     * der Audioanalyse.
     */
    public interface OnExhaleDetected {
        /**
         * Wird aufgerufen, wenn ein gültiges Ausatemmanöver erkannt wurde.
         *
         * @param quality Bewertung der Ausatemtechnik
         *                (z. B. {@code "good"} oder {@code "weak"}).
         */
        void onSuccess(String quality);

        /**
         * Wird aufgerufen, wenn kein gültiges Ausatemmanöver erkannt wurde.
         */
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

    private static final int LISTEN_TIMEOUT_MS = 10_000;
    private static final int SILENCE_TIMEOUT_MS = 500;

    private static final String QUALITY_GOOD = "good";
    private static final String QUALITY_WEAK = "weak";

    private AudioRecord audioRecord;
    private boolean isRecording = false;
    private final OnExhaleDetected listener;

    /**
     * Erstellt einen neuen Audioanalysator.
     *
     * @param listener Empfänger der Analyseergebnisse.
     */
    public AudioAnalyser(OnExhaleDetected listener) {
        this.listener = listener;
    }

    /**
     * Startet die Audioaufnahme und beginnt mit der Analyse
     * des Mikrofonsignals.
     */
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

    /**
     * Analysiert kontinuierlich die aufgenommenen Audiodaten.
     * <p>
     * Die Methode erkennt Beginn und Ende eines Ausatemmanövers,
     * bestimmt dessen Dauer sowie die maximale Signalstärke und
     * bewertet anschließend die Qualität des Ausatmens.
     */
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
                if (waitDuration >= LISTEN_TIMEOUT_MS) {
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
                    String quality = peakRms >= THRESHOLD_GOOD ? QUALITY_GOOD : QUALITY_WEAK;
                    listener.onSuccess(quality);
                }


            } else if (exhaleStart > 0) {

                long duration = System.currentTimeMillis() - exhaleStart;
                long silenceDuration = System.currentTimeMillis() - lastActiveTime;

                if (duration >= MIN_DURATION_MS) {
                    stop();
                    String quality = peakRms >= THRESHOLD_GOOD ? QUALITY_GOOD : QUALITY_WEAK;
                    listener.onSuccess(quality);
                } else if (silenceDuration >= SILENCE_TIMEOUT_MS){
                    exhaleStart = -1;
                    lastActiveTime = -1;
                    peakRms = 0;
                    listener.onFailure();
                }
            }
        }
    }

            /**
            * Berechnet den RMS-Wert (Root Mean Square) eines Audiosignals.
            *
            * @param buffer Audiopuffer mit PCM-Daten.
            * @param read Anzahl der gelesenen Samples.
            * @return RMS-Wert des Audiosignals.
            */
            private int calculateRms ( short[] buffer, int read){
                long sum = 0;
                for (int i = 0; i < read; i++) {
                    sum += buffer[i] * buffer[i];
                }
                return (int) Math.sqrt((double) sum / read);
            }

            /**
            * Beendet die Audioaufnahme und gibt alle belegten Ressourcen frei.
            */
            public void stop () {
                isRecording = false;
                if (audioRecord != null) {
                    audioRecord.stop();
                    audioRecord.release();
                    audioRecord = null;
                }
            }

        }
