package at.fhj.peakflowmate.ocr;

public class SliderPoint {

    private final int x;
    private final int y;

    public SliderPoint(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    @Override
    public String toString() {
        return "SliderPoint{x=" + x + ", y=" + y + '}';
    }
}
