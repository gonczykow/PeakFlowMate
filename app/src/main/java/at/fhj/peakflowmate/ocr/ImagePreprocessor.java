package at.fhj.peakflowmate.ocr;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;

/**
 * Hilfsklasse zur Vorverarbeitung von Bildern für die OCR-Erkennung.
 * <p>
 * Die Klasse stellt Methoden zur Graustufenumwandlung sowie zur
 * Binärisierung eines Bildes bereit, um die Erkennung von
 * Peak-Flow-Skalen und Messwerten zu verbessern.
 */
public class ImagePreprocessor {

    private static final int DEFAULT_THRESHOLD = 170;

    /**
     * Wandelt ein Farbbild in ein Graustufenbild um.
     *
     * @param source das ursprüngliche Bild.
     * @return die Graustufenversion des Bildes.
     */
    public static Bitmap toGray(Bitmap source) {

        Bitmap gray = Bitmap.createBitmap(
                source.getWidth(),
                source.getHeight(),
                Bitmap.Config.ARGB_8888
        );

        Canvas canvas = new Canvas(gray);

        Paint paint = new Paint();

        ColorMatrix matrix = new ColorMatrix();
        matrix.setSaturation(0);

        paint.setColorFilter(new ColorMatrixColorFilter(matrix));

        canvas.drawBitmap(source, 0, 0, paint);

        return gray;
    }

    /**
     * Wandelt ein Graustufenbild mithilfe eines festen Schwellenwerts
     * in ein Schwarz-Weiß-Bild um.
     *
     * @param gray Graustufenbild.
     * @param threshold Schwellenwert für die Binärisierung.
     * @return das binarisierte Bild.
     */
    public static Bitmap threshold(Bitmap gray, int threshold) {

        Bitmap out = Bitmap.createBitmap(
                gray.getWidth(),
                gray.getHeight(),
                Bitmap.Config.ARGB_8888
        );

        for (int y = 0; y < gray.getHeight(); y++) {

            for (int x = 0; x < gray.getWidth(); x++) {

                int pixel = gray.getPixel(x, y);

                int value = pixel & 0xff;

                if (value < threshold)
                    out.setPixel(x, y, 0xff000000);
                else
                    out.setPixel(x, y, 0xffffffff);
            }
        }

        return out;
    }

    /**
     * Führt die Standardvorverarbeitung für die Objekterkennung durch.
     * <p>
     * Das Bild wird zunächst in Graustufen umgewandelt und anschließend
     * binarisiert.
     *
     * @param bitmap das zu verarbeitende Bild.
     * @return das vorverarbeitete Bild.
     */
    public static Bitmap prepareForDetection(Bitmap bitmap) {

        Bitmap gray = toGray(bitmap);

        return threshold(gray, DEFAULT_THRESHOLD);
    }
}
