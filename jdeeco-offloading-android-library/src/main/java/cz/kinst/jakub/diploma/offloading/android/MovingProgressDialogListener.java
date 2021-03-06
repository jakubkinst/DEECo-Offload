package cz.kinst.jakub.diploma.offloading.android;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Handler;

import cz.kinst.jakub.diploma.offloading.listeners.OnBackendMoveListener;

/**
 * Implementation of {@link cz.kinst.jakub.diploma.offloading.listeners.OnBackendMoveListener} showing
 * a standard Android {@link android.app.ProgressDialog} informing user of movement process.
 * <p/>
 * ---------------------------
 * Created by Jakub Kinst 2015
 * E-mail: jakub@kinst.cz
 */
public class MovingProgressDialogListener implements OnBackendMoveListener {
	/**
	 * Timeout after which the dialog is dismissed automatically
	 */
	private static final long PROGRESS_DIALOG_TIMEOUT_MS = 15000;

	/**
	 * Dialog itself
	 */
	private ProgressDialog mProgressDialog;

	/**
	 * Context reference
	 */
	private Context mContext;


	public MovingProgressDialogListener(Context context) {
		this.mContext = context;
	}


	@Override
	public void onBackendMovingStarted(String backendId, final String fromAddress, final String toAddress) {
		Handler mainHandler = new Handler(mContext.getMainLooper());
		mainHandler.post(new Runnable() {
			@Override
			public void run() {
				mProgressDialog = ProgressDialog.show(mContext, "Please wait...", "Moving backend from " + fromAddress + " to " + toAddress, true);
				mProgressDialog.show();
			}
		});
		mainHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				if (mProgressDialog != null && mProgressDialog.isShowing())
					mProgressDialog.dismiss();
			}
		}, PROGRESS_DIALOG_TIMEOUT_MS);
	}


	@Override
	public void onBackendMovingDone(String backendId, String fromAddress, String toAddress) {
		Handler mainHandler = new Handler(mContext.getMainLooper());
		mainHandler.post(new Runnable() {
			@Override
			public void run() {
				if (mProgressDialog != null)
					mProgressDialog.dismiss();
			}
		});
	}
}
