package at.fhj.peakflowmate.ocr;

public class ScalePoint {

    private final int y;
    private final int value;

    public ScalePoint(int y, int value) {
        this.y = y;
        this.value = value;
    }

    public int getY() {
        return y;
    }

    public int getValue() {
        return value;
    }

    @Override
    public String toString() {
        return value + " @ " + y;
    }
}
