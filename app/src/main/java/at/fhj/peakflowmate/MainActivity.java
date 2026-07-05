package at.fhj.peakflowmate;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import at.fhj.peakflowmate.data.model.Measurement;
import at.fhj.peakflowmate.data.repository.MeasurementRepository;
import at.fhj.peakflowmate.ui.diary.DiaryActivity;
import at.fhj.peakflowmate.ui.measurement.MicrophoneActivity;
import at.fhj.peakflowmate.ui.onboarding.OnboardingActivity;
import at.fhj.peakflowmate.ui.settings.SettingsActivity;

/**
 * Hauptaktivität der Anwendung.
 * <p>
 * Diese Aktivität dient als Startbildschirm von PeakFlowMate. Sie überprüft,
 * ob das Onboarding bereits abgeschlossen wurde, und leitet den Benutzer
 * gegebenenfalls zur Onboarding-Aktivität weiter. Außerdem werden die letzte
 * gespeicherte Peak-Flow-Messung angezeigt sowie Navigationsmöglichkeiten
 * zur Messung, zum Tagebuch und zu den Einstellungen bereitgestellt.
 */
public class MainActivity extends AppCompatActivity {

    private MeasurementRepository repository;

    /**
     * Initialisiert die Hauptaktivität.
     *
     * Führt die folgenden Schritte aus:
     *
     *     Überprüft, ob das Onboarding abgeschlossen wurde.
     *     Initialisiert die Benutzeroberfläche und Edge-to-Edge-Darstellung.
     *     Erstellt das Repository für Messdaten.
     *     Zeigt die zuletzt gespeicherte Peak-Flow-Messung an.
     *     Registriert Listener für die Navigation zur Messung,
     *     zum Tagebuch und zu den Einstellungen.
     *
     *
     * @param savedInstanceState enthält den zuvor gespeicherten Zustand der
     *                           Aktivität oder {@code null}, falls keiner
     *                           vorhanden ist.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        boolean onboardingDone = getSharedPreferences("app_prefs", MODE_PRIVATE)
                .getBoolean("onboarding_done", false);
        if (!onboardingDone) {
            startActivity(new Intent(this, OnboardingActivity.class));
            finish();
            return;
        }
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        repository = new MeasurementRepository(this);

        Button btnMeasure = findViewById(R.id.btnMeasure);
        Button btnDiary = findViewById(R.id.btnDiary);
        TextView tvLast = findViewById(R.id.tvLastMeasurement);
        findViewById(R.id.btnSettings).setOnClickListener(v ->
                startActivity(new Intent(this, SettingsActivity.class)));

        repository.getAll().observe(this, measurements -> {
            if (measurements != null && measurements.size() > 0) {
                Measurement last = measurements.get(0);
                String date = new SimpleDateFormat("dd.MM.yyyy",
                        Locale.getDefault()).format(new Date(last.getTimestamp()));
                tvLast.setText(getString(R.string.letzte_messung1) + last.getValue() + getString(R.string.l_min2) + date);
            } else {
                tvLast.setText(R.string.noch_keine_messungen);
            }
        });

        btnMeasure.setOnClickListener(v ->
                startActivity(new Intent(this, MicrophoneActivity.class)));

        btnDiary.setOnClickListener(v ->
                startActivity(new Intent(this, DiaryActivity.class)));
    }
}