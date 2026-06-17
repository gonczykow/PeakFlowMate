package at.fhj.peakflowmate.camera;

import android.media.Image;

import androidx.annotation.OptIn;
import androidx.camera.core.ExperimentalGetImage;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;

import com.google.mlkit.vision.common.InputImage;

public class MeasurementAnalyzer implements ImageAnalysis.Analyzer{

    @OptIn(markerClass = ExperimentalGetImage.class)
    @Override
    public void analyze (ImageProxy imageProxy) {
        Image mediaImage = imageProxy.getImage();
        if (mediaImage != null) {
            InputImage image =
                    InputImage.fromMediaImage(mediaImage, imageProxy.getImageInfo().getRotationDegrees());
        }
    }
}
