package at.fhj.peakflowmate.data.repository;

import android.content.Context;

import androidx.lifecycle.LiveData;

import java.util.List;

import at.fhj.peakflowmate.data.db.AppDatabase;
import at.fhj.peakflowmate.data.db.MeasurementDao;
import at.fhj.peakflowmate.data.model.Measurement;

public class MeasurementRepository {

    private final MeasurementDao dao;

    public MeasurementRepository(Context context) {
        dao = AppDatabase.getInstance(context).measurementDao();
    }

    public void insert(Measurement measurement) {
        new Thread(() -> dao.insert(measurement)).start();
    }

    public void delete(Measurement measurement) {
        new Thread(() -> dao.delete(measurement)).start();
    }

    public LiveData<List<Measurement>> getAll() {
        return dao.getAll();
    }

    public LiveData<List<Measurement>> getSince(int days) {
        long from = System.currentTimeMillis() - (long) days * 24 * 60 * 60 * 1000;
        return dao.getSince(from);
    }

    public LiveData<Integer> getPersonalBest() {
        return dao.getPersonalBest();
    }

    public LiveData<Float> getAverageSince(int days) {
        long from = System.currentTimeMillis() - (long) days * 24 * 60 * 60 * 1000;
        return dao.getAverageSince(from);
    }
}
