package at.fhj.peakflowmate.data.db;


import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import at.fhj.peakflowmate.data.model.Measurement;

/**
 * Data Access Object (DAO) für den Zugriff auf gespeicherte
 * Peak-Flow-Messungen.
 * <p>
 * Das Interface definiert die Datenbankoperationen zum Speichern
 * und Auslesen von Messwerten mithilfe von Room.
 */
@Dao
public interface MeasurementDao {

    /**
     * Speichert eine neue Peak-Flow-Messung in der Datenbank.
     *
     * @param measurement zu speichernde Messung.
     */
    @Insert
    void insert(Measurement measurement);

    /**
     * Liefert alle gespeicherten Messungen in absteigender
     * zeitlicher Reihenfolge.
     *
     * @return LiveData mit allen gespeicherten Messungen.
     */
    @Query("SELECT * FROM measurements ORDER BY timestamp DESC")
    LiveData<List<Measurement>> getAll();

    /**
     * Liefert alle Messungen ab einem bestimmten Zeitpunkt.
     *
     * @param from Zeitstempel (Unix-Zeit in Millisekunden), ab dem
     *             Messungen berücksichtigt werden.
     * @return LiveData mit den gefilterten Messungen.
     */
    @Query("SELECT * FROM measurements WHERE timestamp >= :from ORDER BY timestamp DESC")
    LiveData<List<Measurement>> getSince(long from);

    /**
     * Ermittelt den höchsten gespeicherten Peak-Flow-Wert.
     *
     * @return LiveData mit dem persönlichen Bestwert oder
     *         {@code null}, falls keine Messungen vorhanden sind.
     */
    @Query("SELECT MAX(value) FROM measurements")
    LiveData<Integer> getPersonalBest();
}
