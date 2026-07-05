package at.fhj.peakflowmate.ui.settings;

import android.Manifest;
import android.app.TimePickerDialog;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import java.util.Calendar;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import at.fhj.peakflowmate.R;
import at.fhj.peakflowmate.utils.ReminderWorker;
import at.fhj.peakflowmate.utils.ZoneSettings;

/**
 * Aktivität zur Konfiguration der Anwendungseinstellungen.
 * <p>
 * Diese Aktivität ermöglicht die Anpassung der Grenzwerte für die
 * Peak-Flow-Zonen sowie die Konfiguration einer täglichen Erinnerung
 * an die Durchführung einer Peak-Flow-Messung.
 */
public class SettingsActivity extends AppCompatActivity {

    private int reminderHour = 8;
    private int reminderMinute = 0;
    private static final String PREFS = "zone_settings";
    private static final String KEY_REMINDER_HOUR = "reminder_hour";
    private static final String KEY_REMINDER_MINUTE = "reminder_minute";
    private static final String WORK_NAME = "peak_flow_reminder";

    /**
     * Initialisiert die Einstellungsansicht.
     * <p>
     * Lädt die gespeicherten Grenzwerte und die Erinnerungszeit,
     * richtet die Benutzeroberfläche ein und speichert geänderte
     * Einstellungen nach Bestätigung durch den Benutzer.
     *
     * @param savedInstanceState enthält den zuvor gespeicherten Zustand der
     *                           Aktivität oder {@code null}, falls keiner
     *                           vorhanden ist.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        EditText etGreen = findViewById(R.id.etGreen);
        EditText etYellow = findViewById(R.id.etYellow);
        Button btnSave = findViewById(R.id.btnSaveSettings);
        TextView tvSelectedTime = findViewById(R.id.tvSelectedTime);
        Button btnTimePicker = findViewById(R.id.btnTimePicker);

        SharedPreferences prefs = getSharedPreferences(PREFS, MODE_PRIVATE);
        reminderHour = prefs.getInt(KEY_REMINDER_HOUR, 8);
        reminderMinute = prefs.getInt(KEY_REMINDER_MINUTE, 0);
        tvSelectedTime.setText(
                String.format(Locale.getDefault(), "%02d:%02d", reminderHour, reminderMinute));

        etGreen.setText(String.valueOf(ZoneSettings.getGreenMin(this)));
        etYellow.setText(String.valueOf(ZoneSettings.getYellowMin(this)));

        btnTimePicker.setOnClickListener(v -> {
            new TimePickerDialog(this, (view, hour, minute) -> {
                reminderHour = hour;
                reminderMinute = minute;
                tvSelectedTime.setText(
                        String.format(Locale.getDefault(), "%02d:%02d", hour, minute));
            }, reminderHour, reminderMinute, true).show();
        });

        btnSave.setOnClickListener(v -> {
            String greenStr = etGreen.getText().toString();
            String yellowStr = etYellow.getText().toString();

            if (greenStr.isEmpty() || yellowStr.isEmpty()) {
                Toast.makeText(this, R.string.bitte_alle_felder_ausf_llen,
                        Toast.LENGTH_SHORT).show();
                return;
            }

            int green = Integer.parseInt(greenStr);
            int yellow = Integer.parseInt(yellowStr);

            if (yellow >= green) {
                Toast.makeText(this,
                        R.string.gr_ne_zone_muss_h_her_als_gelbe_sein,
                        Toast.LENGTH_SHORT).show();
                return;
            }

            if (yellow > 900 || green > 900) {
                Toast.makeText(this,
                        R.string.wert_darf_nicht_h_her_als_900_sein,
                        Toast.LENGTH_SHORT).show();
                return;
            }

            if (yellow < 50 || green < 50) {
                Toast.makeText(this,
                        R.string.wert_darf_nicht_kleiner_als_50_sein,
                        Toast.LENGTH_SHORT).show();
                return;
            }

            ZoneSettings.save(this, green, yellow);
            prefs.edit()
                    .putInt(KEY_REMINDER_HOUR, reminderHour)
                    .putInt(KEY_REMINDER_MINUTE, reminderMinute)
                    .apply();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                if (ContextCompat.checkSelfPermission(this,
                        Manifest.permission.POST_NOTIFICATIONS)
                        != PackageManager.PERMISSION_GRANTED) {
                    requestNotification.launch(
                            Manifest.permission.POST_NOTIFICATIONS);
                } else {
                    scheduleReminder(reminderHour, reminderMinute);
                }
            } else {
                scheduleReminder(reminderHour, reminderMinute);
            }
            Toast.makeText(this, R.string.gespeichert, Toast.LENGTH_SHORT).show();
            finish();
        });
    }

    /**
     * Launcher zur Anforderung der Berechtigung für Benachrichtigungen.
     * <p>
     * Nach erfolgreicher Berechtigungsvergabe wird die tägliche Erinnerung
     * geplant.
     */
    private final ActivityResultLauncher<String> requestNotification =
            registerForActivityResult(
                    new ActivityResultContracts.RequestPermission(),
                    granted -> {
                        if (granted) {
                            scheduleReminder(reminderHour, reminderMinute);
                        } else {
                            Toast.makeText(this,
                                    R.string.benachrichtigungen_wurden_nicht_erlaubt,
                                    Toast.LENGTH_SHORT).show();
                        }
                    });

    /**
     * Plant eine täglich wiederkehrende Erinnerung zur Peak-Flow-Messung.
     * <p>
     * Die Erinnerung wird mithilfe des WorkManagers eingerichtet und
     * beginnt zum angegebenen Zeitpunkt.
     *
     * @param hour Stunde der Erinnerung.
     * @param minute Minute der Erinnerung.
     */
    private void scheduleReminder(int hour, int minute) {
        Calendar now = Calendar.getInstance();
        Calendar target = Calendar.getInstance();
        target.set(Calendar.HOUR_OF_DAY, hour);
        target.set(Calendar.MINUTE, minute);
        target.set(Calendar.SECOND, 0);

        if (target.before(now)) {
            target.add(Calendar.DAY_OF_MONTH, 1);
        }

        long delay = target.getTimeInMillis() - now.getTimeInMillis();

        PeriodicWorkRequest request =
                new PeriodicWorkRequest.Builder(
                        ReminderWorker.class,
                        24, TimeUnit.HOURS)
                        .setInitialDelay(delay, TimeUnit.MILLISECONDS)
                        .build();

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
                WORK_NAME,
                ExistingPeriodicWorkPolicy.UPDATE,
                request
        );
    }
}
