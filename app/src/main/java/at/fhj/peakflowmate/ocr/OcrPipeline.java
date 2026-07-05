package at.fhj.peakflowmate.ocr;

import android.content.Context;
import android.graphics.Bitmap;

import at.fhj.peakflowmate.R;
import at.fhj.peakflowmate.utils.DebugUtils;

import android.util.Log;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;

import java.util.List;

/**
 * Führt die vollständige OCR-Verarbeitung eines Peak-Flow-Messgeräts durch.
 * <p>
 * Die Pipeline umfasst die Vorverarbeitung des Bildes, die Erkennung des
 * Schiebers, das Zuschneiden der Skala, die Bestimmung der Skalenbegrenzung
 * sowie die Berechnung des Peak-Flow-Werts anhand der Position des Schiebers.
 */
public class OcrPipeline {

    private final SliderDetector sliderDetector = new SliderDetector();
    private final ScaleCropper cropper = new ScaleCropper();
    private final ValueInterpolator interpolator = new ValueInterpolator();
    private final Context context;

    /**
     * Erstellt eine neue OCR-Pipeline.
     *
     * @param context Anwendungskontext zur Bereitstellung lokalisierter
     *                Fehlermeldungen.
     */
    public OcrPipeline(Context context) {

        this.context = context.getApplicationContext();
    }

    /**
     * Führt die vollständige Erkennung des Peak-Flow-Werts durch.
     * <p>
     * Das Bild wird zunächst vorverarbeitet. Anschließend werden die
     * Führungsmarkierung und der Schieber erkannt, die Skala zugeschnitten
     * und normalisiert. Abschließend wird der Peak-Flow-Wert anhand der
     * Position des Schiebers interpoliert.
     *
     * @param bitmap aufgenommenes Bild des Peak-Flow-Messgeräts.
     * @return ein {@link Task}, der den erkannten Peak-Flow-Wert liefert
     *         oder eine Exception enthält, falls die Erkennung fehlschlägt.
     */
    public Task<Integer> recognize(Bitmap bitmap) {

        TaskCompletionSource<Integer> source =
                new TaskCompletionSource<>();

        Bitmap detectionBitmap =
                ImagePreprocessor.prepareForDetection(bitmap);

        int guideX = sliderDetector.detectGuide(detectionBitmap);
        Log.d("OCR", "Original size: " + bitmap.getWidth() + "x" + bitmap.getHeight());
        Log.d("OCR", "guideX = " + guideX);

        if (guideX < 0) {
            source.setException(
                    new Exception(context.getString(R.string.guide_nicht_gefunden)));
            return source.getTask();
        }


        SliderPoint slider =
                sliderDetector.detect(bitmap, guideX);
        if (slider == null) {
            source.setException(
                    new Exception(context.getString(R.string.slider_nicht_gefunden)));
            return source.getTask();
        }
        Log.d("OCR", "Slider center global = " + slider.getX() + "," + slider.getY());


        CropResult crop = cropper.cropScale(bitmap, guideX);

        if (crop == null) {
            source.setException(new Exception(context.getString(R.string.crop_fehlgeschlagen)));
            return source.getTask();
        }

        SliderPoint sliderOnCrop =
                new SliderPoint(
                        slider.getX() - crop.getLeft(),
                        slider.getY()
                );
        Log.d("OCR", "Slider Y on Crop = " + sliderOnCrop.getY());

        Bitmap croppedScaleMonochrome = ImagePreprocessor.prepareForDetection(crop.getBitmap());

        ScaleBounds bounds =
                ScaleNormalizer.detect(croppedScaleMonochrome);
        if (bounds == null || bounds.getBottomY() <= bounds.getTopY() || bounds.getTopY() == -1) {
            source.setException(new Exception(context.getString(R.string.scale_normalization_failed)));
            return source.getTask();
        }
        Log.d("OCR",
                "Scale bounds local: " +
                        bounds.getTopY() +
                        " - " +
                        bounds.getBottomY());

        List<ScalePoint> points =
                ScaleCalibration.scaleToBitmap(bounds);
        Integer value = interpolator.interpolate(sliderOnCrop, points);

        if (value == null) {
            source.setException(new Exception(context.getString(R.string.interpolation_failed)));
        } else {
            source.setResult(value);
        }
        Log.d("OCR", "Interpolated value = " + value);

        return source.getTask();
    }
}
