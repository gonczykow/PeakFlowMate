package at.fhj.peakflowmate.ocr;

import android.graphics.Bitmap;
import android.graphics.Rect;
import android.util.Log;

public class ScaleCropper {

    private static final float CROP_WIDTH_PERCENT = 0.30f;
    private static final int HORIZONTAL_OFFSET = 20;

    public CropResult cropScale(Bitmap bitmap, int guideX) {

        if (guideX < 0 || bitmap == null) {
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
