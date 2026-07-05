package at.fhj.peakflowmate.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;

/**
 * Hilfsklasse mit Debug-Funktionen.
 * <p>
 * Stellt Methoden zum Speichern von Bitmaps im anwendungsspezifischen
 * externen Speicher bereit. Die Klasse dient ausschließlich der
 * Fehleranalyse und Entwicklung.
 */
public class DebugUtils {

    /**
     * Speichert eine Bitmap als JPEG-Datei im anwendungsspezifischen
     * externen Speicher.
     * <p>
     * Die Methode überschreibt eine vorhandene Datei mit demselben Namen.
     * Der Speicherort sowie mögliche Fehler werden über Logcat protokolliert.
     *
     * @param context Kontext der Anwendung, der für den Zugriff auf den
     *                externen Speicher verwendet wird.
     * @param bitmap die zu speichernde Bitmap.
     * @param name Dateiname der Ausgabedatei.
     */
    public static void save(Context context, Bitmap bitmap, String name) {
        File file = new File(context.getExternalFilesDir(null), name);
        try (FileOutputStream fos = new FileOutputStream(file)) {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fos);
            Log.d("MLKit", "сохранено: " + file.getAbsolutePath());
        } catch (Exception e) {
            Log.e("MLKit", "ошибка: " + e.getMessage());
        }
    }
}
