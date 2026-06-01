package at.fhj.peakflowmate.data.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName= "measurements")
public class Measurement {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private int value;
    private long timestamp;
    private String techniqueQuality;
    private String note;

    public Measurement (int value, long timestamp, String techniqueQuality, String note) {
        this.value = value;
        this.timestamp = timestamp;
        this.techniqueQuality = techniqueQuality;
        this.note = note;
    }

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public int getValue() {
        return value;
    }
    public void setValue(int value) {
        this.value = value;
    }

    public long getTimestamp() {
        return timestamp;
    }
    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getTechniqueQuality() {
        return techniqueQuality;
    }
    public void setTechniqueQuality(String techniqueQuality) {
        this.techniqueQuality = techniqueQuality;
    }

    public String getNote() {
        return note;
    }
    public void setNote(String note) {
        this.note = note;
    }
}
