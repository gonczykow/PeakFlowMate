package at.fhj.peakflowmate.ocr;

import android.util.Log;
import java.util.Comparator;
import java.util.List;

public class ValueInterpolator {

    public Integer interpolate(SliderPoint slider, List<ScalePoint> points) {
        if (points == null || points.isEmpty() || slider == null) {
            return null;
        }

        points.sort(Comparator.comparingInt(ScalePoint::getY));
        int sliderY = slider.getY();

        ScalePoint firstPoint = points.get(0);
        ScalePoint lastPoint = points.get(points.size() - 1);

        if (sliderY <= firstPoint.getY()) {
            return firstPoint.getValue();
        }
        if (sliderY >= lastPoint.getY()) {
            return lastPoint.getValue();
        }

        for (int i = 0; i < points.size() - 1; i++) {
            ScalePoint p1 = points.get(i);
            ScalePoint p2 = points.get(i + 1);

            if (sliderY >= p1.getY() && sliderY <= p2.getY()) {
                int dy = p2.getY() - p1.getY();
                if (dy == 0) continue;

                float ratio = (float) (sliderY - p1.getY()) / dy;
                float exactValue = p1.getValue() + ratio * (p2.getValue() - p1.getValue());

                int finalValue = Math.round(exactValue / 10f) * 10;

                Log.d("OCR_Math", "SliderY=" + sliderY + " matched between Y=" + p1.getY() + " and Y=" + p2.getY()
                        + " | Calculated Value=" + finalValue);

                return finalValue;
            }
        }
        return firstPoint.getValue();
    }
}

