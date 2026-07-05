package at.fhj.peakflowmate.data.db;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import at.fhj.peakflowmate.data.model.Measurement;

/**
 * Zentrale Room-Datenbank der Anwendung.
 * <p>
 * Die Datenbank verwaltet sämtliche lokal gespeicherten Messdaten
 * und stellt den Zugriff über die entsprechenden Data Access Objects
 * (DAO) bereit.
 */
@Database(entities = {Measurement.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    private static volatile AppDatabase instance;

    /**
     * Liefert das Data Access Object für Messdaten.
     *
     * @return Instanz von {@link MeasurementDao}.
     */
    public abstract MeasurementDao measurementDao();

    /**
     * Gibt die Singleton-Instanz der Datenbank zurück.
     * <p>
     * Falls noch keine Datenbankinstanz existiert, wird sie
     * thread-sicher erstellt.
     *
     * @param context Anwendungskontext.
     * @return Singleton-Instanz der Datenbank.
     */
    public static AppDatabase getInstance(Context context) {
        if (instance == null) {
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
