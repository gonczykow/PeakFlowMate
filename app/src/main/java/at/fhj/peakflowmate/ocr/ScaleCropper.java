package at.fhj.peakflowmate.ocr;

import android.graphics.Bitmap;
import android.util.Log;

/**
 * Hilfsklasse zum Zuschneiden des Skalenbereichs eines
 * Peak-Flow-Messgeräts.
 * <p>
 * Basierend auf der Position der Führungsmarkierung wird ein
 * Bildausschnitt erzeugt, der die Skala für die weitere
 * Verarbeitung enthält.
 */
public class ScaleCropper {

    private static final float CROP_WIDTH_PERCENT = 0.30f;
    private static final int HORIZONTAL_OFFSET = 20;

    /**
     * Schneidet den Skalenbereich aus dem aufgenommenen Bild aus.
     * <p>
     * Die Position des Zuschnitts wird anhand der erkannten
     * Führungsmarkierung bestimmt. Neben dem zugeschnittenen Bild
     * wird auch dessen horizontale Position im Originalbild
     * zurückgegeben.
     *
     * @param bitmap Originalbild des Peak-Flow-Messgeräts.
     * @param guideX X-Koordinate der erkannten Führungsmarkierung.
     * @return das Ergebnis des Bildzuschnitts oder {@code null},
     *         falls kein gültiger Zuschnitt möglich ist.
     */
    public CropResult cropScale(Bitmap bitmap, int guideX) {

        if (bitmap == null || guideX < 0) {
            return null;
        }

        int bitmapWidth = bitmap.getWidth();
        int bitmapHeight = bitmap.getHeight();

        int cropWidth = (int) (bitmapWidth * CROP_WIDTH_PERCENT);

        if (cropWidth > bitmapWidth) {
            cropWidth = bitmapWidth;
        }

        int left = guideX - HORIZONTAL_OFFSET;
        left = Math.max(0, left);

        if (left + cropWidth > bitmapWidth) {
            left = bitmapWidth - cropWidth;
        }

        if (cropWidth <= 0 || bitmapHeight <= 0) {
            return null;
        }

        Log.d("OCR",
                "Scale crop: x=" + left +
                        " guide=" + guideX);

        Bitmap croppedBitmap = Bitmap.createBitmap(
                bitmap,
                left,
                0,
                cropWidth,
                bitmapHeight
        );

        return new CropResult(croppedBitmap, left);
    }
}
