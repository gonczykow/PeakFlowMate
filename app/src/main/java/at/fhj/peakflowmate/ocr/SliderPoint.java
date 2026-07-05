package at.fhj.peakflowmate.ocr;

/**
 * Repräsentiert die Position des erkannten Schiebereglers
 * im Bild des Peak-Flow-Meters.
 * <p>
 * Die Koordinaten werden nach der Schiebereglererkennung
 * für die Berechnung des Peak-Flow-Werts verwendet.
 */
public class SliderPoint {

    private final int x;
    private final int y;

    /**
     * Erstellt einen neuen Punkt des Schiebereglers.
     *
     * @param x horizontale Position des Schiebereglers.
     * @param y vertikale Position des Schiebereglers.
     */

    public SliderPoint(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Gibt die horizontale Position des Schiebereglers zurück.
     *
     * @return X-Koordinate des Schiebereglers.
     */
    public int getX() {
        return x;
    }

    /**
     * Gibt die vertikale Position des Schiebereglers zurück.
     *
     * @return Y-Koordinate des Schiebereglers.
     */
    public int getY() {
        return y;
    }

    /**
     * Gibt eine Zeichenkettendarstellung der Schiebereglerposition zurück.
     *
     * @return Zeichenkette mit den X- und Y-Koordinaten.
     */
    @Override
    public String toString() {
        return "SliderPoint{x=" + x + ", y=" + y + '}';
    }
}
