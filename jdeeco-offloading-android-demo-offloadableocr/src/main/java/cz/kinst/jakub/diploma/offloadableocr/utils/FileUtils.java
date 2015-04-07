package cz.kinst.jakub.diploma.offloadableocr.utils;

import android.content.res.AssetManager;
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
 * Created by Jakub Kinst 2015
 * E-mail: jakub@kinst.cz
 */
public class FileUtils {

	/**
	 * Write input stream to a file
	 *
	 * @param input Input stream
	 * @param file  File to write IS to
	 * @throws IOException
	 */
	public static void writeInputStreamToFile(InputStream input, File file) throws IOException {
		final OutputStream output = new FileOutputStream(file);
		final byte[] buffer = new byte[1024];
		int read;

		while ((read = input.read(buffer)) != -1)
			output.write(buffer, 0, read);

		output.close();
		input.close();
	}


	/**
	 * Generate new imege file to store captured image into
	 *
	 * @return File
	 */
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


	/**
	 * Write byte array to file
	 *
	 * @param data input byte array
	 * @param file file to writy BA into
	 * @throws IOException
	 */
	public static void writeByteArrayToFile(byte[] data, File file) throws IOException {
		FileOutputStream fos = new FileOutputStream(file);
		fos.write(data);
		fos.close();
	}


	/**
	 * Copy all assets to external storage directory
	 *
	 * @param assetManager
	 */
	public static void copyAllAssets(AssetManager assetManager) {
		copyFolderFromAssets(assetManager, "tessdata");
		copyFolderFromAssets(assetManager, "samples");
	}


	/**
	 * Check if all assets are copied already
	 *
	 * @return true if copied
	 */
	public static boolean areAssetsCopied() {
		File folder = new File(Environment.getExternalStorageDirectory() + "/" + Config.APP_FOLDER);
		return folder.exists() && folder.isDirectory();
	}


	private static void copyFolderFromAssets(AssetManager assetManager, String name) {
		// "Name" is the name of your folder!
		String[] files = null;

		String state = Environment.getExternalStorageState();

		if (Environment.MEDIA_MOUNTED.equals(state)) {
			// We can read and write the media
			// Checking file on assets subfolder
			try {
				files = assetManager.list(name);
			} catch (IOException e) {
				Log.e(Config.TAG, "Failed to get asset file list.", e);
			}
			// Analyzing all file on assets subfolder
			for (String filename : files) {
				InputStream in = null;
				OutputStream out = null;
				// First: checking if there is already a target folder
				File folder = new File(Environment.getExternalStorageDirectory() + "/" + Config.APP_FOLDER + "/" + name);
				boolean success = true;
				if (!folder.exists()) {
					success = folder.mkdirs();
				}
				if (success) {
					// Moving all the files on external SD
					try {
						in = assetManager.open(name + "/" + filename);
						out = new FileOutputStream(Environment.getExternalStorageDirectory() + "/" + Config.APP_FOLDER + "/" + name + "/" + filename);
						Log.i(Config.TAG, Environment.getExternalStorageDirectory() + "/" + Config.APP_FOLDER + "/" + name + "/" + filename);
						copyFile(in, out);
						in.close();
						in = null;
						out.flush();
						out.close();
						out = null;
					} catch (IOException e) {
						Log.e(Config.TAG, "Failed to copy asset file: " + filename, e);
					}
				} else {
					// Do something else on failure
				}
			}
		} else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
			// We can only read the media
		} else {
			// Something else is wrong. It may be one of many other states, but all we need
			// is to know is we can neither read nor write
		}
	}


	// Method used by copyAssets() on purpose to copy a file.
	private static void copyFile(InputStream in, OutputStream out) throws IOException {
		byte[] buffer = new byte[1024];
		int read;
		while ((read = in.read(buffer)) != -1) {
			out.write(buffer, 0, read);
		}
	}
}
