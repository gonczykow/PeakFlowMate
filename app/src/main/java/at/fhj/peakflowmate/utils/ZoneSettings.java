package at.fhj.peakflowmate.utils;

import android.content.Context;

public class ZoneSettings {

    private static final String PREFS = "zone_settings";
    private static final String KEY_GREEN = "green_min";
    private static final String KEY_YELLOW = "yellow_min";

    public static int getGreenMin(Context context) {
        return context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
                .getInt(KEY_GREEN, 400);
    }

    public static int getYellowMin(Context context) {
        return context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
                .getInt(KEY_YELLOW, 300);
    }

    public static void save(Context context, int greenMin, int yellowMin) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
                .edit()
                .putInt(KEY_GREEN, greenMin)
                .putInt(KEY_YELLOW, yellowMin)
                .apply();
    }
}