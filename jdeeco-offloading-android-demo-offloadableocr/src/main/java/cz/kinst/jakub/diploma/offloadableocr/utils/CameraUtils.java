package cz.kinst.jakub.diploma.offloadableocr.utils;

import android.content.Context;
import android.hardware.Camera;
import android.util.Log;
import android.view.Surface;
import android.view.WindowManager;

/**
 * Created by Jakub Kinst 2015
 * E-mail: jakub@kinst.cz
 */
public class CameraUtils {

    public static final String FOLDER_NAME = "OCR";

    public static void setCameraDisplayOrientation(Context context,
                                                   int cameraId, android.hardware.Camera camera) {
        android.hardware.Camera.CameraInfo info = new android.hardware.Camera.CameraInfo();
        android.hardware.Camera.getCameraInfo(cameraId, info);

        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        int rotation = windowManager.getDefaultDisplay()
                .getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = 90;
                break;
            case Surface.ROTATION_180:
                degrees = 180;
                break;
            case Surface.ROTATION_270:
                degrees = 270;
                break;
        }

        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360; // compensate the mirror
        } else { // back-facing
            result = (info.orientation - degrees + 360) % 360;
        }
        if (camera != null) {
            Log.d(Config.TAG, "Orientation: " + result);
            camera.setDisplayOrientation(result);
            Camera.Parameters params = camera.getParameters();
            params.setRotation(result);
            camera.setParameters(params);
        }
    }
}
