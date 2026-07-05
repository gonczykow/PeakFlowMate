package at.fhj.peakflowmate.ocr;

import android.graphics.Bitmap;
import android.util.Log;

/**
 * Ermittelt die Begrenzungen der Peak-Flow-Skala innerhalb eines Bildes.
 * <p>
 * Die Klasse sucht die obere und untere Markierung der Skala anhand
 * langer horizontaler Teilstriche und liefert deren Positionen für
 * die weitere Kalibrierung.
 */
public class ScaleNormalizer {

    private static final int DARK_THRESHOLD = 100;
    private static final int MIN_LINE_LENGTH = 20;
    private static final int MAX_ALLOWED_GAP = 4;

    /**
     * Bestimmt die obere und untere Begrenzung der erkannten Skala.
     *
     * @param bitmap binarisiertes Bild des Skalenbereichs.
     * @return ein {@link ScaleBounds}-Objekt mit den erkannten
     *         Skalenbegrenzungen.
     */
    public static ScaleBounds detect(Bitmap bitmap) {

        int top = findLongTick(
                bitmap,
                bitmap.getHeight() / 20,
                bitmap.getHeight() / 4,
                1);

        int bottom = findLongTick(
                bitmap,
                bitmap.getHeight() * 19 / 20,
                bitmap.getHeight() * 3 / 4,
                -1);

        Log.d("OCR",
                "Scale bounds: top=" + top +
                        " bottom=" + bottom);

        return new ScaleBounds(top, bottom);
    }

    /**
     * Sucht einen langen horizontalen Skalenstrich.
     * <p>
     * Die Suche erfolgt zeilenweise innerhalb eines definierten Bereichs.
     * Kurze Unterbrechungen der Linie werden toleriert, um kleine
     * Bildfehler auszugleichen.
     *
     * @param bitmap zu untersuchendes Bild.
     * @param startY Startposition der Suche.
     * @param endY Endposition der Suche.
     * @param step Suchrichtung ({@code 1} nach unten,
     *             {@code -1} nach oben).
     * @return Y-Koordinate des gefundenen Skalenstrichs oder
     *         {@code -1}, falls keiner gefunden wurde.
     */
    private static int findLongTick(
            Bitmap bitmap,
            int startY,
            int endY,
            int step) {

        int startX = (int) (bitmap.getWidth() * 0.15);
        int endX = (int) (bitmap.getWidth() * 0.45);

        int y = startY;
        int maxLineInThisRegion = 0;
        while ((step > 0 && y <= endY) || (step < 0 && y >= endY)) {

            int darkCount = 0;
            int currentGap = 0;

            for (int x = startX; x <= endX; x++) {
                if (isDark(bitmap, x, y)) {
                    darkCount++;
                    currentGap = 0;
                } else {
                    if (darkCount > 0) {
                        currentGap++;
                        if (currentGap > MAX_ALLOWED_GAP) {
                            if (darkCount > maxLineInThisRegion) {
                                maxLineInThisRegion = darkCount;
                            }
                            darkCount = 0;
                            currentGap = 0;
                        }
                    }
                }

                if (darkCount > MIN_LINE_LENGTH) {
                    return y;
                }

                if (darkCount >= maxLineInThisRegion) {
                    maxLineInThisRegion = darkCount;
                }
            }

            y += step;
        }
        Log.d("OCR", "Direction step=" + step + ". Max line piece found: " + maxLineInThisRegion + "px");
        return -1;
    }

    /**
     * Prüft, ob ein Pixelbereich als dunkel eingestuft wird.
     * <p>
     * Zur Erhöhung der Robustheit werden neben dem aktuellen Pixel auch
     * die benachbarten Pixel in vertikaler Richtung berücksichtigt.
     *
     * @param bitmap zu untersuchendes Bild.
     * @param x X-Koordinate des Pixels.
     * @param y Y-Koordinate des Pixels.
     * @return {@code true}, wenn der Bereich als dunkel erkannt wird,
     *         andernfalls {@code false}.
     */
    private static boolean isDark(Bitmap bitmap, int x, int y) {

        if (x < 0 || x >= bitmap.getWidth() || y < 0 || y >= bitmap.getHeight()) {
            return false;
        }

        int dark = 0;

        for (int dy = -1; dy <= 1; dy++) {
            int yy = y + dy;
            if (yy < 0 || yy >= bitmap.getHeight()) continue;

            int value = bitmap.getPixel(x, yy) & 0xFF;
            if (value < DARK_THRESHOLD) {
                dark++;
            }
        }
        return dark >= 2;
    }
}
