package cz.kinst.jakub.diploma.offloadableocr;

import android.app.ProgressDialog;
import android.content.Intent;
import android.hardware.Camera;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.widget.FrameLayout;

import java.io.File;
import java.io.IOException;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;


public class MainActivity extends ActionBarActivity {
    private static final int REQUEST_SELECT_PHOTO = 100;
    @InjectView(R.id.camera_preview_container)
    FrameLayout mCameraPreviewContainer;

    private CameraPreview mCameraPreview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        PreferenceManager.setDefaultValues(this, R.xml.settings, false);
        ButterKnife.inject(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mCameraPreview = new CameraPreview(this);
        mCameraPreviewContainer.addView(mCameraPreview);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mCameraPreviewContainer.removeAllViews();
        mCameraPreview.release();
    }

    @OnClick(R.id.settings)
    void openSettings() {
        SettingsActivity.start(this);
    }

    @OnClick(R.id.pick_from_gallery)
    void chooseImage() {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, REQUEST_SELECT_PHOTO);
    }

    @OnClick(R.id.take_picture)
    void takePicture() {
        mCameraPreview.takePicture(new Camera.PictureCallback() {
            @Override
            public void onPictureTaken(byte[] data, Camera camera) {
                try {
                    File file = FileUtils.getNewImageFile();
                    FileUtils.writeByteArrayToFile(data, file);
                    onImageSelected(file);
                } catch (IOException e) {
                    Log.e(Config.TAG, "Error: " + e.getMessage());
                    return;
                }

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        switch (requestCode) {
            case REQUEST_SELECT_PHOTO:
                if (resultCode == RESULT_OK) {
                    Uri selectedImage = imageReturnedIntent.getData();
                    try {
                        File file = FileUtils.getNewImageFile();
                        FileUtils.writeInputStreamToFile(getContentResolver().openInputStream(selectedImage), file);
                        onImageSelected(file);
                    } catch (IOException e) {
                        Log.e(Config.TAG, "Error: " + e.getMessage());
                    }
                }
        }
    }

    private void onImageSelected(final File file) {
        final ProgressDialog progressDialog = ProgressDialog.show(this, getString(R.string.please_wait), getString(R.string.ocr_in_progress), true);
        new AsyncTask<Void, Void, OCRResult>() {
            @Override
            protected OCRResult doInBackground(Void... params) {
                long start = System.currentTimeMillis();
                OCR ocr = new OCR();
                String text = ocr.recognizeText(file);
                long duration = System.currentTimeMillis() - start;
                return new OCRResult(text, "0.0.0.0", duration);
            }

            @Override
            protected void onPostExecute(OCRResult ocrResult) {
                progressDialog.dismiss();
                ResultActivity.start(MainActivity.this, ocrResult);
            }
        }.execute();
    }

}
