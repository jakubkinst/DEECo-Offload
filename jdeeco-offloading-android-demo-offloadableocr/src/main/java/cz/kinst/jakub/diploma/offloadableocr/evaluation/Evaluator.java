package cz.kinst.jakub.diploma.offloadableocr.evaluation;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;

import java.io.File;

import cz.kinst.jakub.diploma.offloadableocr.offloading.OCRResult;


/**
 * Created by Jakub Kinst 2015
 * E-mail: jakub@kinst.cz
 */
public abstract class Evaluator {
	private final Activity mActivity;
	private final EvaluationListener mListener;


	public Evaluator(Activity context, EvaluationListener listener) {
		mActivity = context;
		mListener = listener;
	}


	public void evaluate(final File file, final int noTestCases) {
		final EvaluationResult totalResult = new EvaluationResult(getHost());
		new Thread(new Runnable() {
			@Override
			public void run() {
				long startTime = System.nanoTime();
				float startBattery = getBatteryLevel();
				for (int i = 0; i < noTestCases; i++) {
					System.gc();
					final OCRResult result = performOCR(file);
					final int testCaseNo = i + 1;
					mActivity.runOnUiThread(new Runnable() {
						@Override
						public void run() {
							mListener.onEvaluationProgress(testCaseNo, noTestCases, result);
						}
					});
					totalResult.getDurations().add(result.getDuration() / 1000000);
				}
				long duration = System.nanoTime() - startTime;
				float batteryConsumed = startBattery - getBatteryLevel();
				totalResult.setTotalTime(duration / 1000000);
				totalResult.setBatteryPercentConsumed(batteryConsumed);

				cleanup();
				mActivity.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						mListener.onEvaluationDone(totalResult);
					}
				});

			}
		}).start();
	}


	public abstract String getHost();


	protected Activity getActivity() {
		return mActivity;
	}


	public void cleanup() {

	}


	public abstract OCRResult performOCR(File file);


	public float getBatteryLevel() {
		Intent batteryIntent = mActivity.registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
		int level = batteryIntent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
		int scale = batteryIntent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);

		// Error checking that probably isn't needed but I added just in case.
		if (level == -1 || scale == -1) {
			return 50.0f;
		}

		return ((float) level / (float) scale) * 100.0f;
	}
}
