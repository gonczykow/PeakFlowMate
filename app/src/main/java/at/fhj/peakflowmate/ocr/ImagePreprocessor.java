package at.fhj.peakflowmate.ocr;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;

public class ImagePreprocessor {

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

    public static Bitmap prepareForDetection(Bitmap bitmap) {

        Bitmap gray = toGray(bitmap);

        return threshold(gray, 170);
    }
}
