package cz.kinst.jakub.diploma.offloadableocr;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.widget.TextView;

import butterknife.ButterKnife;
import butterknife.InjectView;
import cz.kinst.jakub.diploma.offloadableocr.offloading.OCRResult;

/**
 * Activity displaying result of an OCR process
 * Created by Jakub Kinst 2015
 * E-mail: jakub@kinst.cz
 */
public class ResultActivity extends ActionBarActivity {

	private static final String EXTRA_RESULT = "result";
	@InjectView(R.id.ocr_result_text)
	TextView mOcrResultText;
	@InjectView(R.id.ocr_result_device_ip)
	TextView mOcrResultDeviceIp;
	@InjectView(R.id.ocr_result_duration)
	TextView mOcrResultDuration;
	private OCRResult mResult;


	public static void start(Context context, OCRResult result) {
		Intent intent = new Intent(context, ResultActivity.class);
		intent.putExtra(EXTRA_RESULT, result);
		context.startActivity(intent);
	}


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_result);
		ButterKnife.inject(this);
		mResult = (OCRResult) getIntent().getSerializableExtra(EXTRA_RESULT);
		mOcrResultText.setText(mResult.getRecognizedText());
		mOcrResultDeviceIp.setText(mResult.getDeviceIp());
		mOcrResultDuration.setText(mResult.getDuration() + " ms");
	}


}
