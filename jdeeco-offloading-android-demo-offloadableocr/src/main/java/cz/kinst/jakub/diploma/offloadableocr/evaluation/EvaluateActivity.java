package cz.kinst.jakub.diploma.offloadableocr.evaluation;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import cz.kinst.jakub.diploma.offloadableocr.R;
import cz.kinst.jakub.diploma.offloadableocr.offloading.OCRResult;


/**
 * Evaluation activity used to measure durations and battery usage of OCR process when offloaded and when not
 * <p/>
 * Created by Jakub Kinst 2015
 * E-mail: jakub@kinst.cz
 */
public class EvaluateActivity extends ActionBarActivity implements EvaluationListener {
	@InjectView(R.id.start_evaluation_local)
	Button mStartEvaluationLocal;
	@InjectView(R.id.start_deeco)
	Button mStartDeeco;
	@InjectView(R.id.start_evaluation_offload)
	Button mStartEvaluationOffload;
	@InjectView(R.id.progress)
	TextView mProgress;
	@InjectView(R.id.number_cases)
	EditText mNumberCases;
	private OffloadEvaluator mOffloadEvaluator;
	private LocalEvaluator mLocalEvaluator;


	public static File getFileFromAssets(Context context, String name) {
		AssetManager am = context.getAssets();
		try {
			InputStream inputStream = am.open(name);
			File f = new File(Environment.getExternalStorageDirectory() + "/" + name);
			OutputStream outputStream = new FileOutputStream(f);
			byte buffer[] = new byte[1024];
			int length = 0;

			while ((length = inputStream.read(buffer)) > 0) {
				outputStream.write(buffer, 0, length);
			}

			outputStream.close();
			inputStream.close();

			return f;
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;

	}


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_evaluate);
		ButterKnife.inject(this);
	}


	@OnClick(R.id.start_evaluation_local)
	void startLocalEvaluation() {
		mStartEvaluationLocal.setEnabled(false);
		File file = getFileFromAssets(this, "eval_sample.jpg");
		mLocalEvaluator = new LocalEvaluator(this, this);
		mLocalEvaluator.evaluate(file, Integer.parseInt(mNumberCases.getText().toString()));
	}


	@OnClick(R.id.start_deeco)
	void startDEECo() {
		mOffloadEvaluator = new OffloadEvaluator(this, this);
		mStartDeeco.setEnabled(false);
		mOffloadEvaluator.setOnHostUpdateLstener(new OffloadEvaluator.OnHostUpdateListener() {
			@Override
			public void onHostUpdated(final String host) {
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						mStartDeeco.setText(host);
					}
				});
			}
		});
	}


	@OnClick(R.id.start_evaluation_offload)
	void startOffloadEvaluation() {
		mStartEvaluationOffload.setEnabled(false);
		File file = getFileFromAssets(this, "eval_sample.jpg");
		mOffloadEvaluator.evaluate(file, Integer.parseInt(mNumberCases.getText().toString()));
	}


	@Override
	public void onEvaluationProgress(int caseNo, int totalCases, OCRResult result) {
		mProgress.setText(Math.round(100 * caseNo / (float) totalCases) + "% (" + caseNo + "/" + totalCases + ")");
	}


	@Override
	public void onEvaluationDone(EvaluationResult totalResult) {
		try {
			EvaluationLogger.logResult(totalResult);
			Toast.makeText(this, "Result saved. Total time:" + totalResult.getTotalTime() + "ms", Toast.LENGTH_LONG).show();
		} catch (IOException e) {
			Toast.makeText(this, "ERROR Writing result to a file", Toast.LENGTH_LONG).show();
			e.printStackTrace();
		}
	}


	@Override
	protected void onDestroy() {
		try {
			mLocalEvaluator.cleanup();
			mOffloadEvaluator.cleanup();
		} catch (Exception e) {

		}
		super.onDestroy();
	}
}
