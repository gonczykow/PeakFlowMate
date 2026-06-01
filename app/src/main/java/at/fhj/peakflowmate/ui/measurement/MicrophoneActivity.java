package at.fhj.peakflowmate.ui.measurement;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import at.fhj.peakflowmate.R;
import at.fhj.peakflowmate.audio.AudioAnalyser;

public class MicrophoneActivity extends AppCompatActivity {

    private AudioAnalyser audioAnalyser;
    private View[] bars;

    private final ActivityResultLauncher<String> requestMic =
            registerForActivityResult(
                    new ActivityResultContracts.RequestPermission(),
                    granted -> {
                        if(granted) {
                            startListening();
                        } else {
                            Toast.makeText(this,
                                    "Mikrofon wird benoetigt",
                                    Toast.LENGTH_LONG).show();
                            finish();
                        }
                    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_microphone);

        bars = new View[] {
                findViewById(R.id.bar1), findViewById(R.id.bar2),
                findViewById(R.id.bar3), findViewById(R.id.bar4),
                findViewById(R.id.bar5), findViewById(R.id.bar6),
                findViewById(R.id.bar7)
        };

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                == PackageManager.PERMISSION_GRANTED) {
            startListening();
        } else {
            requestMic.launch(Manifest.permission.RECORD_AUDIO);
        }
    }

    private void startListening() {
        TextView tvStatus = findViewById(R.id.tvStatus);
        tvStatus.setText("Hoeren...");

        animateBars();

        audioAnalyser = new AudioAnalyser(new AudioAnalyser.OnExhaleDetected() {
            @Override
            public void onSuccess(String quality) {
                runOnUiThread(() -> {
                    Intent intent = new Intent(MicrophoneActivity.this, CameraActivity.class);
                    intent.putExtra("technique_quality", quality);
                    startActivity(intent);
                    finish();
                });
            }

            @Override
            public void onFailure() {
                runOnUiThread(() ->
                    tvStatus.setText("Bitte noch einmal versuchen"));
                };
            });

        audioAnalyser.start();
        }

        private void animateBars() {
            int[] heights = {8, 16, 24, 32, 24, 16, 8};
            for (int i = 0; i < bars.length; i++) {
                final int index = i;
                final int[] dpHeights = {heights[i], heights[(i + 3) % 7]};
                bars[i].postDelayed(new Runnable() {
                    int step = 0;
                    @Override
                    public void run() {
                        if(isDestroyed()) return;
                        int dp = dpHeights[step % 2];
                        bars[index].getLayoutParams().height =
                                (int) (dp * getResources().getDisplayMetrics().density);
                        bars[index].requestLayout();
                        step++;
                        bars[index].postDelayed(this, 300 + index * 40L);
                    }
                }, index * 80L);
            }
        }

        @Override
        protected void onDestroy() {
        super.onDestroy();
        if (audioAnalyser != null) audioAnalyser.stop();
        }
}