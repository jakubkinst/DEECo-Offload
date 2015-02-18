package cz.kinst.jakub.diploma.offloadableocr;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import cz.kinst.jakub.diploma.offloadableocr.utils.FileUtils;

/**
 * Created by jakubkinst on 16/02/15.
 */
public class CopyAssetsTask extends AsyncTask {
    private final Context mContext;
    private ProgressDialog mCopyingDialog;

    public CopyAssetsTask(Context context) {
        mContext = context;
    }

    @Override
    protected void onPreExecute() {
        mCopyingDialog = ProgressDialog.show(mContext, mContext.getString(R.string.please_wait), mContext.getString(R.string.initing_ocr_data), true);
    }

    @Override
    protected Void doInBackground(Object[] params) {
        FileUtils.copyAllAssets(mContext.getAssets());
        return null;
    }

    @Override
    protected void onPostExecute(Object o) {
        mCopyingDialog.dismiss();
    }
}