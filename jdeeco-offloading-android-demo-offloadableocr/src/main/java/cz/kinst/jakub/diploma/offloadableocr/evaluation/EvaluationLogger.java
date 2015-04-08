package cz.kinst.jakub.diploma.offloadableocr.evaluation;

import android.os.Environment;

import com.google.common.io.Files;
import com.google.gson.Gson;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * Created by Jakub Kinst 2015
 * E-mail: jakub@kinst.cz
 */
public class EvaluationLogger {
	public static void logResult(EvaluationResult result) throws IOException {
		Date date = new Date();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
		File dir = new File(Environment.getExternalStorageDirectory() + "/deeco-offload-evaluation");
		dir.mkdirs();
		File file = new File(dir + "/result-" + result.getExecutionHost() + "-" + dateFormat.format(date) + ".txt");
		Files.write(new Gson().toJson(result), file, Charset.forName("UTF-8"));
	}
}
