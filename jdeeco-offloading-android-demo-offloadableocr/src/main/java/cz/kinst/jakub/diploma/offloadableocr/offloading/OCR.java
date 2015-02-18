package cz.kinst.jakub.diploma.offloadableocr.offloading;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.os.Environment;
import android.util.Log;

import com.googlecode.tesseract.android.TessBaseAPI;

import java.io.File;
import java.io.IOException;

import cz.kinst.jakub.diploma.offloadableocr.utils.Config;

/**
 * Created by jakubkinst on 04/02/15.
 */
public class OCR {
    private static final String LOG_TAG = "OCR";
    private static final String DATA_PATH = Environment.getExternalStorageDirectory() + "/" + Config.APP_FOLDER;
    private static final String LANG = "eng";
    private final TessBaseAPI mBaseApi;

    public static OCR getInstance() {
        return new OCR();
    }

    private OCR() {
        mBaseApi = new TessBaseAPI();
        mBaseApi.setDebug(true);
        mBaseApi.init(DATA_PATH, LANG);
    }

    public String recognizeText(File image) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 2;
        Bitmap bitmap = BitmapFactory.decodeFile(image.getAbsolutePath(), options);

        try {
            ExifInterface exif = new ExifInterface(image.getAbsolutePath());
            int exifOrientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

            Log.v(LOG_TAG, "Orient: " + exifOrientation);

            int rotate = 0;
            switch (exifOrientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    rotate = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    rotate = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    rotate = 270;
                    break;
            }

            Log.v(LOG_TAG, "Rotation: " + rotate);

            if (rotate != 0) {

                // Getting width & height of the given image.
                int w = bitmap.getWidth();
                int h = bitmap.getHeight();

                // Setting pre rotate
                Matrix mtx = new Matrix();
                mtx.preRotate(rotate);

                // Rotating Bitmap
                bitmap = Bitmap.createBitmap(bitmap, 0, 0, w, h, mtx, false);
                // tesseract req. ARGB_8888
                bitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
            }

        } catch (IOException e) {
            Log.e(LOG_TAG, "Rotate or coversion failed: " + e.toString());
        }


        Log.v(LOG_TAG, "Before baseApi");


        mBaseApi.setImage(bitmap);
        String recognizedText = mBaseApi.getUTF8Text();

        Log.v(LOG_TAG, "OCR Result: " + recognizedText);

        return recognizedText;
    }

}