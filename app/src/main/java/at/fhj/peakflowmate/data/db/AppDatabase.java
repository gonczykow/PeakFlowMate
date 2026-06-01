package at.fhj.peakflowmate.data.db;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import at.fhj.peakflowmate.data.model.Measurement;

@Database(entities = {Measurement.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    public static volatile AppDatabase instance;

    public abstract MeasurementDao measurementDao();

    public static AppDatabase getInstance(Context context) {
        if(instance == null) {
            synchronized (AppDatabase.class) {
                if(instance == null) {
                    instance = Room.databaseBuilder(
                            context.getApplicationContext(),
                            AppDatabase.class,
                            "peakflowmate.db"
                    ).build();
                }
            }
        }
        return instance;
    }
}
