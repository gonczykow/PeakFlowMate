package at.fhj.peakflowmate.ui.measurement;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import androidx.core.graphics.Insets;
import android.graphics.Matrix;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import com.google.common.util.concurrent.ListenableFuture;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import at.fhj.peakflowmate.R;
import at.fhj.peakflowmate.ocr.OcrPipeline;
import at.fhj.peakflowmate.ui.result.ResultActivity;
import at.fhj.peakflowmate.utils.DebugUtils;

public class CameraActivity extends AppCompatActivity {

    private PreviewView previewView;
    private ImageCapture imageCapture;
    private ExecutorService cameraExecutor;
    private String techniqueQuality;
    private ProcessCameraProvider cameraProvider;
    private OcrPipeline ocrPipeline;
    private Button btnCapture;
    private Button btnManual;

    private final ActivityResultLauncher<String> requestCamera =
            registerForActivityResult(
                    new ActivityResultContracts.RequestPermission(),
                    granted -> {
                        if (granted) {
                            startCamera();
                        } else {
                            Toast.makeText(this,
                                    R.string.kamera_wird_ben_tigt,
                                    Toast.LENGTH_LONG).show();
                            finish();
                        }
                    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_camera);
        WindowInsetsControllerCompat windowInsetsController =
                WindowCompat.getInsetsController(getWindow(), getWindow().getDecorView());
        windowInsetsController.setSystemBarsBehavior(
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        );
        View root = findViewById(R.id.root);

        ViewCompat.setOnApplyWindowInsetsListener(root, (view, insets) -> {

            Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());

            view.setPadding(
                    bars.left,
                    bars.top,
                    bars.right,
                    bars.bottom
            );

            return insets;
        });

        ocrPipeline = new OcrPipeline(this);
        techniqueQuality = getIntent().getStringExtra("technique_quality");
        previewView = findViewById(R.id.previewView);
        btnCapture = findViewById(R.id.btnCapture);
        btnManual = findViewById(R.id.btnManual);
        cameraExecutor = Executors.newSingleThreadExecutor();

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            == PackageManager.PERMISSION_GRANTED) {
            startCamera();
        } else {
            requestCamera.launch(Manifest.permission.CAMERA);
        }

        btnCapture.setOnClickListener(v -> takePhoto());
        btnManual.setOnClickListener(v -> openManualInput());
    }

    private void startCamera() {
        ListenableFuture<ProcessCameraProvider> future =
                ProcessCameraProvider.getInstance(this);

        future.addListener(() -> {
            try {
                cameraProvider = future.get();

                Preview preview = new Preview.Builder().build();
                preview.setSurfaceProvider(previewView.getSurfaceProvider());

                imageCapture = new ImageCapture.Builder()
                        .setCaptureMode(ImageCapture.CAPTURE_MODE_MAXIMIZE_QUALITY)
                        .setFlashMode(ImageCapture.FLASH_MODE_AUTO)
                        .build();

                cameraProvider.unbindAll();
                cameraProvider.bindToLifecycle(
                        this,
                        CameraSelector.DEFAULT_BACK_CAMERA,
                        preview,
                        imageCapture
                );
            } catch (ExecutionException | InterruptedException e) {
                Log.e("CameraX", getString(R.string.fehler), e);
            }
        }, ContextCompat.getMainExecutor(this));
    }

    private void takePhoto() {
        if (imageCapture == null) return;

        btnCapture.setEnabled(false);
        btnCapture.setText(R.string.bitte_warten);

        imageCapture.takePicture(cameraExecutor,
                new ImageCapture.OnImageCapturedCallback() {
                    @Override
                    public void onCaptureSuccess(@NonNull ImageProxy image) {
                        runOnUiThread(() -> btnCapture.setText(R.string.verarbeitung));

                        int rotation = image.getImageInfo().getRotationDegrees();
                        Bitmap bitmap = image.toBitmap();
                        image.close();

                        Bitmap rotated =
                                rotateBitmap(
                                        bitmap,
                                        rotation
                                );

                        analyzeImage(rotated);
                    }

                    @Override
                    public void onError(@NonNull ImageCaptureException exception) {
                        Log.e("CameraX", getString(R.string.fehler_beim_aufnehmen), exception);
                        runOnUiThread(() ->{
                            btnCapture.setEnabled(true);
                            btnCapture.setText(R.string.foto_aufnehmen1);
                        });
                    }
                });
    }

    private void analyzeImage(Bitmap bitmap) {

        Log.d("OCR", "Starting recognition...");

        ocrPipeline.recognize(bitmap)

                .addOnSuccessListener(value -> {

                        btnCapture.setEnabled(true);
                        btnCapture.setText(R.string.foto_aufnehmen2);

                        showResult(value);
                    Log.d("OCR", "Recognition finished: " + value);
                })

                .addOnFailureListener(e -> {

                        btnCapture.setEnabled(true);
                        btnCapture.setText(R.string.foto_aufnehmen3);

                        Toast.makeText(
                                this,
                                e.getMessage(),
                                Toast.LENGTH_SHORT
                        ).show();
                });
    }

    private Bitmap rotateBitmap(Bitmap bitmap, int degrees) {
        if (degrees == 0) return bitmap;
        Matrix matrix = new Matrix();
        matrix.postRotate(degrees);
        return Bitmap.createBitmap(bitmap, 0, 0,
                bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    private void showResult(int value) {
        if (cameraProvider != null) {
            cameraProvider.unbindAll();
        }
        Intent intent = new Intent(this, ResultActivity.class);
        intent.putExtra("value", value);
        intent.putExtra("technique_quality", techniqueQuality);
        startActivity(intent);
        finish();
    }

    private void openManualInput() {
        EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        input.setHint(R.string._50_900);

        new AlertDialog.Builder(this)
                .setTitle(R.string.wert_manuell_eingeben)
                .setView(input)
                .setPositiveButton(R.string.ok1, (dialog, which) -> {
                    String text = input.getText().toString();
                    if (!text.isEmpty()) {
                        int value = Integer.parseInt(text);
                        if (value >= 50 && value <= 900) {
                            showResult(value);
                        } else {
                            Toast.makeText(this,
                                    R.string.wert_muss_zwischen_50_und_900_liegen,
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNegativeButton(R.string.abbrechen1, null)
                .show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cameraExecutor.shutdown();
    }
}