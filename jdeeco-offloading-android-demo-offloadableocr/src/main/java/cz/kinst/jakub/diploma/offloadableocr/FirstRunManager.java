package cz.kinst.jakub.diploma.offloadableocr;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by Jakub Kinst 2015
 * E-mail: jakub@kinst.cz
 */
public class FirstRunManager {
    public static boolean isFirstRun(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        boolean firstRun = prefs.getBoolean("firstRun", true);
        prefs.edit().putBoolean("firstRun", false).commit();
        return firstRun;
    }
}
