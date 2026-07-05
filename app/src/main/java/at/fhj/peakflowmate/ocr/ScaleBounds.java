package at.fhj.peakflowmate.ocr;

public class ScaleBounds {

    public final int topY;
    public final int bottomY;

    public ScaleBounds(int topY, int bottomY) {
        this.topY = topY;
        this.bottomY = bottomY;
    }

    public int getTopY() {
        return topY;
    }

    public int getBottomY() {
        return bottomY;
    }
}
