package at.fhj.peakflowmate.ocr;

import android.graphics.Bitmap;
import android.util.Log;

public class ScaleNormalizer {

    private static final int DARK_THRESHOLD = 100;
    private static final int MIN_LINE_LENGTH = 20;
    private static final int MAX_ALLOWED_GAP = 4;

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
        Log.d("OCR_Debug", "Direction step=" + step + ". Max line piece found: " + maxLineInThisRegion + "px");
        return -1;
    }

    private static boolean isDark(Bitmap bitmap, int x, int y) {

        if (x < 0 || x >= bitmap.getWidth() || y < 0 || y >= bitmap.getHeight()) {
            return false;
        }

        int dark = 0;

        for (int dy = -1; dy <= 1; dy++) {
            int yy = y + dy;
            if (yy < 0 || yy >= bitmap.getHeight()) continue;

            int value = bitmap.getPixel(x, yy) & 255;
            if (value < DARK_THRESHOLD) {
                dark++;
            }
        }
        return dark >= 2;
    }
}
