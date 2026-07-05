package at.fhj.peakflowmate.ui.measurement;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

public class ScanOverlayView extends View {

    private final Paint dimPaint = new Paint();
    private final Paint clearPaint = new Paint();
    private final Paint framePaint = new Paint();
    private final Paint hintPaint = new Paint();

    private RectF frame;

    public ScanOverlayView(Context context) {
        super(context);
        init();
    }

    public ScanOverlayView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ScanOverlayView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {

        setLayerType(LAYER_TYPE_HARDWARE, null);

        dimPaint.setColor(Color.argb(160, 0, 0, 0));

        clearPaint.setXfermode(
                new PorterDuffXfermode(PorterDuff.Mode.CLEAR));

        framePaint.setStyle(Paint.Style.STROKE);
        framePaint.setStrokeWidth(dp(3));
        framePaint.setColor(Color.WHITE);
        framePaint.setAntiAlias(true);

        hintPaint.setColor(Color.argb(80, 255, 255, 255));
        hintPaint.setStyle(Paint.Style.STROKE);
        hintPaint.setStrokeWidth(dp(2));
        hintPaint.setAntiAlias(true);
    }

    @Override
    protected void onDraw(Canvas canvas) {

        super.onDraw(canvas);

        canvas.drawRect(
                0,
                0,
                getWidth(),
                getHeight(),
                dimPaint
        );

        float frameWidth = dp(250);
        float frameHeight = dp(500);

        float left = (getWidth() - frameWidth) / 2f;
        float top = (getHeight() - frameHeight) / 2f;

        frame = new RectF(
                left,
                top,
                left + frameWidth,
                top + frameHeight
        );

        canvas.drawRoundRect(
                frame,
                dp(20),
                dp(20),
                clearPaint
        );

        canvas.drawRoundRect(
                frame,
                dp(20),
                dp(20),
                framePaint
        );

        drawHint(canvas);
    }

    private void drawHint(Canvas canvas) {

        if (frame == null)
            return;

        float x = frame.left + frame.width() * 0.18f;

        canvas.drawLine(
                x,
                frame.top + dp(35),
                x,
                frame.bottom - dp(35),
                hintPaint
        );

        canvas.drawLine(
                x + dp(12),
                frame.top + dp(35),
                x + dp(12),
                frame.bottom - dp(35),
                hintPaint
        );

        float scaleX = frame.left + frame.width() * 0.45f;

        canvas.drawLine(
                scaleX,
                frame.top + dp(45),
                scaleX,
                frame.bottom - dp(45),
                hintPaint
        );

        Path p = new Path();
        canvas.drawPath(p, hintPaint);
    }

    private float dp(float value) {
        return value * getResources().getDisplayMetrics().density;
    }
}
