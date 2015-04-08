package cz.kinst.jakub.diploma.offloading.android;

import android.util.Log;

import cz.kinst.jakub.diploma.offloading.logger.LogProvider;

/**
 * Android {@link cz.kinst.jakub.diploma.offloading.logger.LogProvider} implementation
 * <p/>
 * ---------------------------
 * Created by Jakub Kinst 2015
 * E-mail: jakub@kinst.cz
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