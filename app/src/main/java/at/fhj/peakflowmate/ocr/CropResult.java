package at.fhj.peakflowmate.ocr;

import android.graphics.Bitmap;

public class CropResult {

    private final Bitmap bitmap;
    private final int left;

    public CropResult(Bitmap bitmap, int left) {
        this.bitmap = bitmap;
        this.left = left;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public int getLeft() {
        return left;
    }
}
