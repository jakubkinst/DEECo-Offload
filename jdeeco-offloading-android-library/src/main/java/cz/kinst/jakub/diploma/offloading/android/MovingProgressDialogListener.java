package cz.kinst.jakub.diploma.offloading.android;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Handler;

import cz.kinst.jakub.diploma.offloading.OnBackendMoveListener;

/**
 * Created by jakubkinst on 06/02/15.
 */
public class MovingProgressDialogListener implements OnBackendMoveListener {
    private ProgressDialog mProgressDialog;
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
