package cz.kinst.jakub.diploma.offloadableocr;

import android.content.Context;
import android.hardware.Camera;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;

import cz.kinst.jakub.diploma.offloadableocr.utils.CameraUtils;
import cz.kinst.jakub.diploma.offloadableocr.utils.Config;

/**
 * Created by Jakub Kinst 2015
 * E-mail: jakub@kinst.cz
 */
@SuppressWarnings("deprecation")
public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {
    public static final int CAMERA_ID = 0;
    private SurfaceHolder mHolder;
    private Camera mCamera;

    public CameraPreview(Context context) {
        super(context);
        mCamera = Camera.open(CAMERA_ID);


        // Install a SurfaceHolder.Callback so we get notified when the
        // underlying surface is created and destroyed.
        mHolder = getHolder();
        mHolder.addCallback(this);
        // deprecated setting, but required on Android versions prior to 3.0
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    public void surfaceCreated(SurfaceHolder holder) {
        // The Surface has been created, now tell the camera where to draw the mCameraPreviewContainer.
        try {
            mCamera.setPreviewDisplay(holder);
            mCamera.startPreview();
        } catch (IOException e) {
            Log.d("Camera", "Error setting camera mCameraPreviewContainer: " + e.getMessage());
        }
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {

        if (mHolder.getSurface() == null) {
            return;
        }

        try {
            mCamera.stopPreview();
        } catch (Exception e) {
        }
        configureCamera();

        try {
            mCamera.setPreviewDisplay(mHolder);
            mCamera.startPreview();

        } catch (Exception e) {
            Log.d("Camera", "Error starting camera mCameraPreviewContainer: " + e.getMessage());
        }
    }

    private void configureCamera() {

        CameraUtils.setCameraDisplayOrientation(getContext(), CAMERA_ID, mCamera);
        Camera.Parameters params = mCamera.getParameters();
        if (params.getSupportedFocusModes().contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO)) {
            params.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
        }
        if (params.getSupportedFlashModes().contains(Camera.Parameters.FLASH_MODE_AUTO)) {
            params.setFlashMode(Camera.Parameters.FLASH_MODE_AUTO);
        }
        for (Camera.Size size : params.getSupportedPictureSizes()) {
            if (size.width < Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(getContext()).getString("image_size", SettingsActivity.DEFAULT_IMAGE_SIZE))) {
                params.setPictureSize(size.width, size.height);
                Log.i(Config.TAG, "Resolution selected: " + size.width + "x" + size.height + "px");
                break;
            }
        }
        mCamera.setParameters(params);
    }

    public void release() {
        if (mCamera != null) {
            mCamera.stopPreview();
            mHolder.removeCallback(this);
            mCamera.release();        // release the camera for other applications
            mCamera = null;
        }
    }

    public void takePicture(Camera.PictureCallback pictureCallback) {
        if (mCamera != null) {
            mCamera.takePicture(null, null, pictureCallback);
        }
    }
}