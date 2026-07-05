package at.fhj.peakflowmate.data.db;

import android.widget.ListView;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import at.fhj.peakflowmate.data.model.Measurement;

@Dao
public interface MeasurementDao {

    @Insert
    void insert(Measurement measurement);

    @Query("SELECT * FROM measurements ORDER BY timestamp DESC")
    LiveData<List<Measurement>> getAll();

    @Query("SELECT * FROM measurements WHERE timestamp >= :from ORDER BY timestamp DESC")
    LiveData<List<Measurement>> getSince(long from);

    @Query("SELECT MAX(value) FROM measurements")
    LiveData<Integer> getPersonalBest();
}
