package at.fhj.peakflowmate.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;

public class DebugUtils {
    public static void save(Context context, Bitmap bitmap, String name) {
        File file = new File(context.getExternalFilesDir(null), name);
        try (FileOutputStream fos = new FileOutputStream(file)) {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fos);
            Log.d("MLKit", "сохранено: " + file.getAbsolutePath());
        } catch (Exception e) {
            Log.e("MLKit", "ошибка: " + e.getMessage());
        }
    }
}
