package at.fhj.peakflowmate.ocr;

import android.graphics.Bitmap;

/**
 * Enthält das Ergebnis eines Bildzuschnitts.
 * <p>
 * Das Objekt speichert den zugeschnittenen Bildausschnitt sowie die
 * horizontale Position des Zuschnitts im ursprünglichen Bild.
 */
public class CropResult {

    private final Bitmap bitmap;
    private final int left;

    /**
     * Erstellt ein neues Ergebnis eines Bildzuschnitts.
     *
     * @param bitmap der zugeschnittene Bildausschnitt.
     * @param left die linke X-Koordinate des Zuschnitts im Originalbild.
     */
    public CropResult(Bitmap bitmap, int left) {
        this.bitmap = bitmap;
        this.left = left;
    }

    /**
     * Gibt den zugeschnittenen Bildausschnitt zurück.
     *
     * @return die zugeschnittene Bitmap.
     */
    public Bitmap getBitmap() {
        return bitmap;
    }

    /**
     * Gibt die linke X-Koordinate des Zuschnitts im Originalbild zurück.
     *
     * @return linke Position des Zuschnitts.
     */
    public int getLeft() {
        return left;
    }
}
