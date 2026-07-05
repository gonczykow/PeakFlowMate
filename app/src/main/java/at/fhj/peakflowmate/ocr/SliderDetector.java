package at.fhj.peakflowmate.ocr;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.util.Log;

import java.util.ArrayDeque;

public class SliderDetector {

    private static final int DARK_THRESHOLD = 80;

    public SliderPoint detect(Bitmap bitmap, int guideX) {

        if (guideX < 0) {
            return null;
        }

        return findSlider(bitmap, guideX);
    }

    public int detectGuide(Bitmap gray) {

        int w = gray.getWidth();
        int h = gray.getHeight();

        int startX = w / 20;
        int endX = w / 2;

        int bestX = -1;
        int bestScore = 0;

        for (int x = startX; x < endX; x++) {

            int longestRun = 0;
            int currentRun = 0;
            int darkPixels = 0;


            for (int y = h / 10; y < h * 9 / 10; y++) {

                int value = gray.getPixel(x, y) & 255;

                if (value < DARK_THRESHOLD) {

                    currentRun++;
                    darkPixels++;

                    if (currentRun > longestRun) {
                        longestRun = currentRun;
                    }

                } else {
                    currentRun = 0;
                }
            }

            int score = longestRun * 5 + darkPixels;

            if (score > bestScore) {
                bestScore = score;
                bestX = x;
            }
        }

        Log.d("OCR",
                "Guide x=" + bestX +
                        " score=" + bestScore);

        return bestX;
    }

    private SliderPoint findSlider(Bitmap bitmap, int guideX) {

        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        int left = Math.max(0, guideX - 150);
        int right = Math.min(bitmap.getWidth() - 1, guideX + 150);

        int top = height / 5;
        int bottom = height * 9 / 10;

        boolean[][] visited =
                new boolean[height][width];

        int bestSize = 0;
        int bestMinX = 0;
        int bestMaxX = 0;
        int bestMinY = 0;
        int bestMaxY = 0;

        int[] dx = {-1, 1, 0, 0};
        int[] dy = {0, 0, -1, 1};

        Log.d("OCR",
                "Search area: left=" + left +
                        " right=" + right +
                        " top=" + top +
                        " bottom=" + bottom);

        for (int sy = top; sy < bottom; sy++) {

            for (int sx = left; sx <= right; sx++) {

                if (visited[sy][sx])
                    continue;

                visited[sy][sx] = true;

                if (!isRed(bitmap.getPixel(sx, sy)))
                    continue;

                if (guideX > 0 && Math.abs(sx - guideX) > 120) {
                    continue;
                }

                ArrayDeque<Point> queue = new ArrayDeque<>();
                queue.add(new Point(sx, sy));

                int size = 0;

                int minX = sx;
                int maxX = sx;
                int minY = sy;
                int maxY = sy;

                while (!queue.isEmpty()) {

                    Point p = queue.removeFirst();

                    size++;

                    if (p.x < minX) minX = p.x;
                    if (p.x > maxX) maxX = p.x;
                    if (p.y < minY) minY = p.y;
                    if (p.y > maxY) maxY = p.y;

                    for (int k = 0; k < 4; k++) {

                        int nx = p.x + dx[k];
                        int ny = p.y + dy[k];

                        if (nx < 0 || nx >= width || ny < 0 || ny >= height)
                            continue;


                        if (nx < left || nx > right || ny < top || ny >= bottom)
                            continue;

                        if (visited[ny][nx])
                            continue;

                        visited[ny][nx] = true;

                        if (isRed(bitmap.getPixel(nx, ny))) {
                            queue.add(new Point(nx, ny));
                        }
                    }
                }

                if (size > bestSize) {

                    bestSize = size;
                    bestMinX = minX;
                    bestMaxX = maxX;
                    bestMinY = minY;
                    bestMaxY = maxY;
                }
            }
        }

        Log.d("OCR", "Largest red blob = " + bestSize);

        if (bestSize < 15)
            return null;

        int centerX = (bestMinX + bestMaxX) / 2;
        int centerY = (bestMinY + bestMaxY) / 2;

        Log.d("OCR",
                "Slider bounds = "
                        + bestMinX + "," + bestMinY
                        + " - "
                        + bestMaxX + "," + bestMaxY);

        Log.d("OCR",
                "Slider center = "
                        + centerX + "," + centerY);

        return new SliderPoint(centerX, centerY);
    }

    private boolean isRed(int color) {
        int r = Color.red(color);
        int g = Color.green(color);
        int b = Color.blue(color);

        if (r <= g || r <= b) {
            return false;
        }

        if (r - b < 40 || r - g < 40) {
            return false;
        }

        float[] hsv = new float[3];
        Color.colorToHSV(color, hsv);

        float hue = hsv[0];
        float saturation = hsv[1];
        float value = hsv[2];

        boolean isRedHue = (hue >= 0f && hue <= 25f) || (hue >= 335f && hue <= 360f);

        boolean isSaturated = saturation > 0.35f;
        boolean isBrightEnough = value > 0.25f;

        return isRedHue && isSaturated && isBrightEnough;
    }

    public Bitmap createRedMask(Bitmap bitmap) {

        int left = 0;
        int right = bitmap.getWidth() - 1;

        int top = bitmap.getHeight() / 5;
        int bottom = bitmap.getHeight() * 9 / 10;

        Bitmap mask = Bitmap.createBitmap(
                bitmap.getWidth(),
                bitmap.getHeight(),
                Bitmap.Config.ARGB_8888
        );
        int redPixels = 0;

        for (int y = top; y < bottom; y++) {

            for (int x = left; x <= right; x++) {

                    if (isRed(bitmap.getPixel(x, y))) {
                        redPixels++;
                    }

                    if (isRed(bitmap.getPixel(x, y))) {
                        mask.setPixel(x, y, Color.WHITE);
                    } else {
                        mask.setPixel(x, y, Color.BLACK);
                    }

                    int logY = bitmap.getHeight() / 2;
                    if (y >= logY - 5 && y <= logY + 5) {
                        int pixel = bitmap.getPixel(x, y);
                        float[] hsv = new float[3];
                        Color.colorToHSV(pixel, hsv);
                    }
            }

        }
        Log.d("OCR", "Total red pixels = " + redPixels);
            return mask;
        }

}
