package at.fhj.peakflowmate.data.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * Repräsentiert eine gespeicherte Peak-Flow-Messung.
 * <p>
 * Die Entität wird von Room zur Speicherung der Messdaten
 * in der lokalen Datenbank verwendet. Neben dem Messwert
 * werden auch der Zeitpunkt der Messung, die Qualität der
 * Ausatemtechnik sowie eine optionale Notiz gespeichert.
 */
@Entity(tableName= "measurements")
public class Measurement {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private int value;
    private long timestamp;
    private String techniqueQuality;
    private String note;

    /**
     * Erstellt eine neue Peak-Flow-Messung.
     *
     * @param value gemessener Peak-Flow-Wert in l/min.
     * @param timestamp Zeitpunkt der Messung als Unix-Zeitstempel
     *                  in Millisekunden.
     * @param techniqueQuality Bewertung der Ausatemtechnik.
     * @param note optionale Notiz zur Messung.
     */
    public Measurement (int value, long timestamp, String techniqueQuality, String note) {
        this.value = value;
        this.timestamp = timestamp;
        this.techniqueQuality = techniqueQuality;
        this.note = note;
    }

    /**
     * Gibt die eindeutige ID der Messung zurück.
     *
     * @return Datenbank-ID der Messung.
     */
    public int getId() {
        return id;
    }
    /**
     * Legt die Datenbank-ID der Messung fest.
     *
     * @param id eindeutige Datenbank-ID.
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Gibt den gemessenen Peak-Flow-Wert zurück.
     *
     * @return Peak-Flow-Wert in l/min.
     */
    public int getValue() {
        return value;
    }
    /**
     * Legt den Peak-Flow-Wert fest.
     *
     * @param value Peak-Flow-Wert in l/min.
     */
    public void setValue(int value) {
        this.value = value;
    }

    /**
     * Gibt den Zeitpunkt der Messung zurück.
     *
     * @return Unix-Zeitstempel in Millisekunden.
     */
    public long getTimestamp() {
        return timestamp;
    }
    /**
     * Legt den Zeitpunkt der Messung fest.
     *
     * @param timestamp Unix-Zeitstempel in Millisekunden.
     */
    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    /**
     * Gibt die Bewertung der Ausatemtechnik zurück.
     *
     * @return Qualität der Ausatemtechnik.
     */
    public String getTechniqueQuality() {
        return techniqueQuality;
    }
    /**
     * Legt die Bewertung der Ausatemtechnik fest.
     *
     * @param techniqueQuality Qualität der Ausatemtechnik.
     */
    public void setTechniqueQuality(String techniqueQuality) {
        this.techniqueQuality = techniqueQuality;
    }

    /**
     * Gibt die optionale Notiz zur Messung zurück.
     *
     * @return Notiz zur Messung oder {@code null}.
     */
    public String getNote() {
        return note;
    }
    /**
     * Legt eine optionale Notiz zur Messung fest.
     *
     * @param note Notiz zur Messung.
     */
    public void setNote(String note) {
        this.note = note;
    }
}
