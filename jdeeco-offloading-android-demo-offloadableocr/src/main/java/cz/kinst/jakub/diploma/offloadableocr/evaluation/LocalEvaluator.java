package cz.kinst.jakub.diploma.offloadableocr.evaluation;

import android.app.Activity;

import java.io.File;

import cz.kinst.jakub.diploma.offloadableocr.offloading.OCR;
import cz.kinst.jakub.diploma.offloadableocr.offloading.OCRResult;


/**
 * Created by Jakub Kinst 2015
 * E-mail: jakub@kinst.cz
 */
public class LocalEvaluator extends Evaluator {

	private final OCR mOCR;


	public LocalEvaluator(Activity context, EvaluationListener listener) {
		super(context, listener);
		mOCR = OCR.getInstance();
	}


	@Override
	public String getHost() {
		return "LOCAL";
	}


	@Override
	public OCRResult performOCR(File file) {
		long start = System.nanoTime();
		String recognizedText = mOCR.recognizeText(file, null);
		OCRResult result = new OCRResult(recognizedText, getHost());
		long duration = System.nanoTime() - start;
		result.setDuration(duration);
		return result;
	}
}
