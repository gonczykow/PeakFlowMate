package at.fhj.peakflowmate.data.repository;

import android.content.Context;

import androidx.lifecycle.LiveData;

import java.util.List;

import at.fhj.peakflowmate.data.db.AppDatabase;
import at.fhj.peakflowmate.data.db.MeasurementDao;
import at.fhj.peakflowmate.data.model.Measurement;

/**
 * Verwaltet den Zugriff auf gespeicherte Peak-Flow-Messungen.
 * <p>
 * Das Repository dient als Vermittlungsschicht zwischen der
 * Benutzeroberfläche und der Room-Datenbank. Es kapselt den
 * Datenzugriff und stellt Messdaten als {@link LiveData} bereit.
 */
public class MeasurementRepository {

    private final MeasurementDao dao;
    private static final long MILLIS_PER_DAY = 24L * 60 * 60 * 1000;

    /**
     * Erstellt ein neues Repository für Messdaten.
     *
     * @param context Anwendungskontext.
     */
    public MeasurementRepository(Context context) {
        dao = AppDatabase.getInstance(context).measurementDao();
    }

    /**
     * Speichert eine neue Peak-Flow-Messung asynchron in der Datenbank.
     *
     * @param measurement zu speichernde Messung.
     */
    public void insert(Measurement measurement) {
        new Thread(() -> dao.insert(measurement)).start();
    }

    /**
     * Liefert alle gespeicherten Messungen.
     *
     * @return LiveData mit allen Messungen in absteigender
     *         zeitlicher Reihenfolge.
     */
    public LiveData<List<Measurement>> getAll() {
        return dao.getAll();
    }

    /**
     * Liefert alle Messungen der letzten angegebenen Anzahl von Tagen.
     *
     * @param days Anzahl der zu berücksichtigenden Tage.
     * @return LiveData mit den gefilterten Messungen.
     */
    public LiveData<List<Measurement>> getSince(int days) {
        long from = System.currentTimeMillis() - days * MILLIS_PER_DAY;
        return dao.getSince(from);
    }

    /**
     * Liefert den höchsten gespeicherten Peak-Flow-Wert.
     *
     * @return LiveData mit dem persönlichen Bestwert oder
     *         {@code null}, falls keine Messungen vorhanden sind.
     */
    public LiveData<Integer> getPersonalBest() {
        return dao.getPersonalBest();
    }
}
