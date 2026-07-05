package at.fhj.peakflowmate.utils;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import at.fhj.peakflowmate.R;

public class ReminderWorker extends Worker {

    Context context = getApplicationContext();

    public ReminderWorker(@NonNull Context context,
                          @NonNull WorkerParameters params) {
        super(context, params);
    }

    @NonNull
    @Override
    public Result doWork() {
        showNotification();
        return Result.success();
    }

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

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(
                        getApplicationContext(), "reminder")
                        .setSmallIcon(R.drawable.ic_launcher_foreground)
                        .setContentTitle(context.getString(R.string.peak_flow_mate))
                        .setContentText(context.getString(R.string.zeit_f_r_ihre_t_gliche_peak_flow_messung))
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                        .setAutoCancel(true);

        manager.notify(1, builder.build());
    }
}
