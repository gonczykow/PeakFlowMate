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

/**
 * Benutzerdefinierte View zur Darstellung einer Scan-Überlagerung.
 * <p>
 * Die View dunkelt den Hintergrund ab und hebt einen zentralen
 * Scanbereich hervor, der als Orientierung für die Positionierung
 * des Peak-Flow-Messgeräts dient. Zusätzlich werden Hilfslinien
 * innerhalb des Scanbereichs dargestellt.
 */
public class ScanOverlayView extends View {

    private final Paint dimPaint = new Paint();
    private final Paint clearPaint = new Paint();
    private final Paint framePaint = new Paint();
    private final Paint hintPaint = new Paint();

    private RectF frame;

    /**
     * Erstellt eine neue Scan-Überlagerung.
     *
     * @param context Anwendungskontext.
     */
    public ScanOverlayView(Context context) {
        super(context);
        init();
    }

    /**
     * Erstellt eine neue Scan-Überlagerung.
     *
     * @param context Anwendungskontext.
     * @param attrs Attribute aus der XML-Layoutdatei.
     */
    public ScanOverlayView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    /**
     * Erstellt eine neue Scan-Überlagerung.
     *
     * @param context Anwendungskontext.
     * @param attrs Attribute aus der XML-Layoutdatei.
     * @param defStyleAttr Standardstil der View.
     */
    public ScanOverlayView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    /**
     * Initialisiert die Zeichenobjekte und konfiguriert das Erscheinungsbild
     * der Scan-Überlagerung.
     */
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

    /**
     * Zeichnet die Scan-Überlagerung.
     * <p>
     * Der Hintergrund wird abgedunkelt, der Scanbereich freigestellt und
     * mit einem Rahmen versehen. Anschließend werden Orientierungslinien
     * innerhalb des Scanbereichs gezeichnet.
     *
     * @param canvas Zeichenfläche der View.
     */
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

    /**
     * Zeichnet Orientierungslinien innerhalb des Scanbereichs.
     *
     * @param canvas Zeichenfläche der View.
     */
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
    }

    /**
     * Wandelt einen Wert von Density-independent Pixels (dp) in Pixel um.
     *
     * @param value Wert in dp.
     * @return entsprechender Wert in Pixel.
     */
    private float dp(float value) {
        return value * getResources().getDisplayMetrics().density;
    }
}
