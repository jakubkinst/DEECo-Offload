package cz.kinst.jakub.diploma.offloadableocr;

import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by jakubkinst on 11/02/15.
 */
public class FileUtils {

    public static void writeInputStreamToFile(InputStream input, File file) throws IOException {
        final OutputStream output = new FileOutputStream(file);
        final byte[] buffer = new byte[1024];
        int read;

        while ((read = input.read(buffer)) != -1)
            output.write(buffer, 0, read);

        output.close();
        input.close();
    }

    public static File getNewImageFile() {
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.

        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), CameraUtils.FOLDER_NAME);
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d("OCR", "failed to create directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                "OCR_IMG_" + timeStamp + ".jpg");

        return mediaFile;
    }

    public static void writeByteArrayToFile(byte[] data, File pictureFile) throws IOException {
        FileOutputStream fos = new FileOutputStream(pictureFile);
        fos.write(data);
        fos.close();
    }
}
