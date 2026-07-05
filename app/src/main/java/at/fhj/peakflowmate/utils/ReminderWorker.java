package at.fhj.peakflowmate.utils;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.app.PendingIntent;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import at.fhj.peakflowmate.MainActivity;
import at.fhj.peakflowmate.R;

/**
 * Worker zur Anzeige einer Erinnerung an die tägliche Peak-Flow-Messung.
 * <p>
 * Diese Klasse wird von WorkManager ausgeführt und erstellt eine
 * Systembenachrichtigung, die den Benutzer an die Durchführung einer
 * Peak-Flow-Messung erinnert. Beim Antippen der Benachrichtigung wird
 * die Hauptaktivität der Anwendung geöffnet.
 */
public class ReminderWorker extends Worker {

    Context context = getApplicationContext();

    /**
     * Erstellt einen neuen Worker für Erinnerungsbenachrichtigungen.
     *
     * @param context Anwendungskontext.
     * @param params Parameter für die Ausführung des Workers.
     */
    public ReminderWorker(@NonNull Context context,
                          @NonNull WorkerParameters params) {
        super(context, params);
    }

    /**
     * Führt den Worker aus und zeigt eine Erinnerungsbenachrichtigung an.
     *
     * @return {@link Result#success()}, wenn die Benachrichtigung erfolgreich
     *         erstellt wurde.
     */
    @NonNull
    @Override
    public Result doWork() {
        showNotification();
        return Result.success();
    }
    /**
     * Erstellt und zeigt eine Systembenachrichtigung an.
     * <p>
     * Falls erforderlich, wird zunächst ein Benachrichtigungskanal erstellt.
     * Anschließend wird eine Benachrichtigung erzeugt, die beim Antippen
     * die Hauptaktivität der Anwendung öffnet.
     */
    private void showNotification() {
        NotificationManager manager = (NotificationManager)
                getApplicationContext()
                        .getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    "reminder",
                    context.getString(R.string.messerinnerung),
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            manager.createNotificationChannel(channel);
        }

        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        int pendingFlags = Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                ? PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
                : PendingIntent.FLAG_UPDATE_CURRENT;

        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                0,
                intent,
                pendingFlags
        );

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(
                        getApplicationContext(), "reminder")
                        .setSmallIcon(R.drawable.ic_launcher_foreground)
                        .setContentTitle(context.getString(R.string.peak_flow_mate))
                        .setContentText(context.getString(R.string.zeit_f_r_ihre_t_gliche_peak_flow_messung))
                        .setContentIntent(pendingIntent)
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                        .setAutoCancel(true);

        manager.notify(1, builder.build());
    }
}
