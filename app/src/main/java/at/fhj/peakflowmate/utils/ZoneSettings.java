package at.fhj.peakflowmate.utils;

import android.content.Context;

/**
 * Hilfsklasse zur Verwaltung der Grenzwerte für die Peak-Flow-Zonen.
 * <p>
 * Die Klasse speichert und lädt die Minimalwerte der grünen und gelben
 * Zone mithilfe von SharedPreferences. Diese Grenzwerte werden zur
 * Bewertung der Messergebnisse verwendet.
 */
public class ZoneSettings {

    private static final String PREFS = "zone_settings";
    private static final String KEY_GREEN = "green_min";
    private static final String KEY_YELLOW = "yellow_min";

    /**
     * Gibt den Minimalwert der grünen Zone zurück.
     *
     * @param context Anwendungskontext.
     * @return der gespeicherte Minimalwert der grünen Zone oder
     *         {@code 400}, falls kein Wert gespeichert wurde.
     */
    public static int getGreenMin(Context context) {
        return context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
                .getInt(KEY_GREEN, 400);
    }

    /**
     * Gibt den Minimalwert der gelben Zone zurück.
     *
     * @param context Anwendungskontext.
     * @return der gespeicherte Minimalwert der gelben Zone oder
     *         {@code 300}, falls kein Wert gespeichert wurde.
     */
    public static int getYellowMin(Context context) {
        return context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
                .getInt(KEY_YELLOW, 300);
    }

    /**
     * Speichert die Grenzwerte der grünen und gelben Zone.
     *
     * @param context Anwendungskontext.
     * @param greenMin Minimalwert der grünen Zone.
     * @param yellowMin Minimalwert der gelben Zone.
     */
    public static void save(Context context, int greenMin, int yellowMin) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
                .edit()
                .putInt(KEY_GREEN, greenMin)
                .putInt(KEY_YELLOW, yellowMin)
                .apply();
    }
}