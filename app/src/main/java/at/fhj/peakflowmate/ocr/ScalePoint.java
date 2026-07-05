package at.fhj.peakflowmate.ocr;

/**
 * Repräsentiert einen Kalibrierungspunkt der Peak-Flow-Skala.
 * <p>
 * Ein Skalenpunkt besteht aus der vertikalen Position innerhalb
 * des Bildes sowie dem zugehörigen Peak-Flow-Wert.
 */
public class ScalePoint {

    private final int y;
    private final int value;

    /**
     * Erstellt einen neuen Kalibrierungspunkt.
     *
     * @param y vertikale Position des Skalenpunkts im Bild.
     * @param value zugehöriger Peak-Flow-Wert in l/min.
     */
    public ScalePoint(int y, int value) {
        this.y = y;
        this.value = value;
    }

    /**
     * Gibt die vertikale Position des Skalenpunkts zurück.
     *
     * @return Y-Koordinate des Skalenpunkts.
     */
    public int getY() {
        return y;
    }

    /**
     * Gibt den Peak-Flow-Wert des Skalenpunkts zurück.
     *
     * @return Peak-Flow-Wert in l/min.
     */
    public int getValue() {
        return value;
    }

    /**
     * Gibt eine Zeichenkettendarstellung des Skalenpunkts zurück.
     *
     * @return Zeichenkette mit Peak-Flow-Wert und Y-Koordinate.
     */
    @Override
    public String toString() {
        return value + " @ " + y;
    }
}
