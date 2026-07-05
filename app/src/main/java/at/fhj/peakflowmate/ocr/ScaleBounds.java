package at.fhj.peakflowmate.ocr;

/**
 * Beschreibt die vertikalen Begrenzungen einer Peak-Flow-Skala.
 * <p>
 * Das Objekt speichert die obere und untere Y-Koordinate der
 * erkannten Skala innerhalb eines Bildes.
 */
public class ScaleBounds {

    private final int topY;
    private final int bottomY;

    /**
     * Erstellt ein neues Objekt mit den Begrenzungen der Skala.
     *
     * @param topY obere Y-Koordinate der Skala.
     * @param bottomY untere Y-Koordinate der Skala.
     */
    public ScaleBounds(int topY, int bottomY) {
        this.topY = topY;
        this.bottomY = bottomY;
    }

    /**
     * Gibt die obere Begrenzung der Skala zurück.
     *
     * @return obere Y-Koordinate.
     */
    public int getTopY() {
        return topY;
    }

    /**
     * Gibt die untere Begrenzung der Skala zurück.
     *
     * @return untere Y-Koordinate.
     */
    public int getBottomY() {
        return bottomY;
    }
}
