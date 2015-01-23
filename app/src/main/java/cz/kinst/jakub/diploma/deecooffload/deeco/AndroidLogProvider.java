package cz.kinst.jakub.diploma.deecooffload.deeco;

import android.util.Log;

import cz.kinst.jakub.offloading.logger.LogProvider;

/**
 * Created by jakubkinst on 21/01/15.
 */
public class AndroidLogProvider implements LogProvider {
    public void d(String message) {
        Log.d("Offload DEBUG", message);
    }

    public void i(String message) {
        Log.i("Offload INFO", message);
    }

    public void e(String message) {
        Log.e("Offload ERROR", message);
    }
}