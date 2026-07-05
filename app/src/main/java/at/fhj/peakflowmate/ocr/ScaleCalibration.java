package at.fhj.peakflowmate.ocr;

import java.util.ArrayList;
import java.util.List;

public class ScaleCalibration {

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
