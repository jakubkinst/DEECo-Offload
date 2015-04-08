package cz.kinst.jakub.diploma.offloadableocr;

import android.app.ProgressDialog;
import android.content.Intent;
import android.hardware.Camera;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.widget.FrameLayout;
import android.widget.TextView;

import org.restlet.data.MediaType;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import cz.kinst.jakub.diploma.offloadableocr.offloading.OCRBackend;
import cz.kinst.jakub.diploma.offloadableocr.offloading.OCRBackendImpl;
import cz.kinst.jakub.diploma.offloadableocr.offloading.OCRParams;
import cz.kinst.jakub.diploma.offloadableocr.offloading.OCRResult;
import cz.kinst.jakub.diploma.offloadableocr.utils.Config;
import cz.kinst.jakub.diploma.offloadableocr.utils.FileUtils;
import cz.kinst.jakub.diploma.offloading.Frontend;
import cz.kinst.jakub.diploma.offloading.OffloadingManager;
import cz.kinst.jakub.diploma.offloading.android.AndroidLogProvider;
import cz.kinst.jakub.diploma.offloading.android.AndroidUDPBroadcast;
import cz.kinst.jakub.diploma.offloading.android.MovingProgressDialogListener;
import cz.kinst.jakub.diploma.offloading.backend.MultipartHolder;
import cz.kinst.jakub.diploma.offloading.listeners.OnDeploymentPlanUpdatedListener;
import cz.kinst.jakub.diploma.offloading.logger.Logger;
import cz.kinst.jakub.diploma.offloading.model.BackendDeploymentPlan;

/**
 * Main Activity displaying camera preview and able to take pictures or select a picture from gallery
 * <p/>
 * Created by Jakub Kinst 2015
 * E-mail: jakub@kinst.cz
 */
public class MainActivity extends ActionBarActivity {
	public static final String OCR_URI = "/ocr";
	private static final int REQUEST_SELECT_PHOTO = 100;
	private static Frontend mFrontend;
	@InjectView(R.id.camera_preview_container)
	FrameLayout mCameraPreviewContainer;
	@InjectView(R.id.current_ip)
	TextView mCurrentIp;
	private CameraPreview mCameraPreview;
	private OffloadingManager mOffloadingManager;


	public static Frontend getFrontend() {
		return mFrontend;
	}


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		ButterKnife.inject(this);

		if (!FileUtils.areAssetsCopied() || FirstRunManager.isFirstRun(this)) {
			new CopyAssetsTask(this).execute();
		}

		//init DEECo
		Logger.setProvider(new AndroidLogProvider());

		try {
			mOffloadingManager = OffloadingManager.createInstance(new AndroidUDPBroadcast(this), "ocr");

			OCRBackendImpl ocrBackend = new OCRBackendImpl(OCR_URI, this);
			mOffloadingManager.attachBackend(ocrBackend, OCRBackend.class);
			mFrontend = new Frontend(mOffloadingManager);
			mFrontend.setOnDeploymentPlanUpdatedListener(new OnDeploymentPlanUpdatedListener() {
				@Override
				public void onDeploymentPlanUpdated(BackendDeploymentPlan plan) {
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							String backendAddress = mFrontend.getActiveBackendAddress(OCRBackend.class);
							mCurrentIp.setText(backendAddress);
						}
					});
				}
			});
			mFrontend.setOnBackendMoveListener(new MovingProgressDialogListener(this));

			mOffloadingManager.init(OffloadingManager.MODE_WITH_FRONTEND);
			mOffloadingManager.start();

		} catch (Exception e) {
			e.printStackTrace();
		}

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
		photoPickerIntent.setType("image/jpeg");
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
		// Initialize the resource proxy.
		final OCRBackend backend = mFrontend.getActiveBackendProxy(OCRBackend.class);

		final ProgressDialog progressDialog = ProgressDialog.show(this, getString(R.string.please_wait), getString(R.string.ocr_in_progress), true);

		new AsyncTask<Void, Void, OCRResult>() {
			@Override
			protected OCRResult doInBackground(Void... params) {
				try {
					long start = System.nanoTime();
					ArrayList<File> files = new ArrayList<File>();
					files.add(file);
					OCRResult result = backend.recognize(new MultipartHolder<OCRParams>(files, MediaType.IMAGE_JPEG, new OCRParams()).getRepresentation());
					long duration = System.nanoTime() - start;
					result.setDuration(duration / 1000000);
					return result;
				} catch (Exception e) {
					e.printStackTrace();
					return null;
				}
			}


			@Override
			protected void onPostExecute(OCRResult ocrResult) {
				progressDialog.dismiss();
				ResultActivity.start(MainActivity.this, ocrResult);
			}
		}.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
	}


	@Override
	protected void onDestroy() {
		try {
			mOffloadingManager.stop();
			mFrontend = null;
		} catch (Exception e) {
			e.printStackTrace();
		}
		super.onStop();
	}

}
