package at.fhj.peakflowmate.ocr;

import java.util.ArrayList;
import java.util.List;

/**
 * Hilfsklasse zur Kalibrierung der Peak-Flow-Skala.
 * <p>
 * Die Klasse wandelt die normierten Kalibrierungspunkte einer
 * Peak-Flow-Skala in Bildkoordinaten um und erstellt daraus
 * eine Liste von Skalenpunkten für die spätere Interpolation.
 */
public class ScaleCalibration {

    /**
     * Kalibrierungspunkte der Peak-Flow-Skala.
     * Jede Zeile enthält:
     * [0] relative Position (0.0–1.0),
     * [1] Peak-Flow-Wert in l/min.
     */
    private static final float[][] CALIBRATION = {
            {0.00f, 800},
            {0.12f, 700},
            {0.19f, 650},
            {0.26f, 600},
            {0.33f, 550},
            {0.40f, 500},
            {0.47f, 450},
            {0.54f, 400},
            {0.61f, 350},
            {0.68f, 300},
            {0.75f, 250},
            {0.82f, 200},
            {0.88f, 150},
            {0.93f, 100},
            {1.00f, 50},
    };

    /**
     * Berechnet die Positionen der Kalibrierungspunkte innerhalb einer
     * erkannten Skala.
     * <p>
     * Die normierten Kalibrierungsdaten werden auf die tatsächliche Höhe
     * der erkannten Skala abgebildet und als Liste von Skalenpunkten
     * zurückgegeben.
     *
     * @param bounds Begrenzungen der erkannten Skala.
     * @return Liste der berechneten Skalenpunkte.
     */
    public static List<ScalePoint> scaleToBitmap(ScaleBounds bounds) {

        if (bounds == null) {
            return new ArrayList<>(0);
        }

        int top = bounds.getTopY();
        int bottom = bounds.getBottomY();
        int height = bottom - top;

        List<ScalePoint> points = new ArrayList<>(CALIBRATION.length);

        for (float[] c : CALIBRATION) {

            float position = c[0];
            int value = (int) c[1];

            int y = Math.round(top + position * height);

            points.add(new ScalePoint(y, value));
        }

        return points;
    }
}
