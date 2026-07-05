package at.fhj.peakflowmate.ui.measurement;

import android.Manifest;
import android.app.AlertDialog;
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

/**
 * Aktivität zur Erkennung des Ausatemgeräuschs über das Mikrofon.
 * <p>
 * Diese Aktivität fordert die erforderliche Mikrofonberechtigung an,
 * analysiert das Audiosignal des Benutzers und erkennt einen gültigen
 * Ausatemvorgang. Nach erfolgreicher Erkennung wird die Kameraaktivität
 * zur weiteren Peak-Flow-Messung gestartet. Während der Analyse wird
 * eine animierte Pegelanzeige dargestellt.
 */
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
                                    R.string.mikrofon_wird_benoetigt,
                                    Toast.LENGTH_LONG).show();
                            finish();
                        }
                    });

    /**
     * Initialisiert die Benutzeroberfläche und überprüft die Berechtigung
     * für den Zugriff auf das Mikrofon.
     * <p>
     * Ist die Berechtigung bereits vorhanden, wird die Audioanalyse sofort
     * gestartet. Andernfalls wird der Benutzer zur Erteilung der Berechtigung
     * aufgefordert.
     *
     * @param savedInstanceState enthält den zuvor gespeicherten Zustand der
     *                           Aktivität oder {@code null}, falls keiner
     *                           vorhanden ist.
     */
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

    /**
     * Startet die Analyse des Mikrofonsignals.
     * <p>
     * Während der Analyse wird eine Statusanzeige sowie eine Animation
     * dargestellt. Bei erfolgreicher Erkennung eines Ausatemvorgangs wird
     * die Kameraaktivität geöffnet. Falls kein gültiger Ausatemvorgang
     * erkannt wird, erhält der Benutzer die Möglichkeit, den Vorgang
     * erneut zu starten oder abzubrechen.
     */
    private void startListening() {
        TextView tvStatus = findViewById(R.id.tvStatus);
        tvStatus.setText(R.string.h_ren);

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
                runOnUiThread(() -> {
                    new AlertDialog.Builder(MicrophoneActivity.this)
                            .setTitle(R.string.nicht_erkannt)
                            .setMessage(R.string.bitte_noch_einmal_versuchen)
                            .setPositiveButton(R.string.ok, (dialog, which) -> {
                                audioAnalyzer = new AudioAnalyser(this);
                                audioAnalyzer.start();
                            })
                            .setNegativeButton(R.string.abbrechen, (dialog, which) -> finish())
                            .setCancelable(false)
                            .show();
                });
            }
            });

        audioAnalyser.start();
        }

    /**
     * Startet die Animation der Pegelanzeige.
     * <p>
     * Die Balken werden periodisch in ihrer Höhe verändert, um während
     * der Audioanalyse eine visuelle Rückmeldung zu geben.
     */
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

    /**
     * Gibt verwendete Ressourcen frei.
     * <p>
     * Beendet die Audioanalyse, sofern sie noch aktiv ist, bevor die
     * Aktivität zerstört wird.
     */
        @Override
        protected void onDestroy() {
        super.onDestroy();
        if (audioAnalyser != null) audioAnalyser.stop();
        }
}