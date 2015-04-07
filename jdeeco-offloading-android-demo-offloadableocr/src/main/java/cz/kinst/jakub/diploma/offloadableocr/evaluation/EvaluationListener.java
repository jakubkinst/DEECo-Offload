package cz.kinst.jakub.diploma.offloadableocr.evaluation;

import cz.kinst.jakub.diploma.offloadableocr.offloading.OCRResult;


/**
 * Created by Jakub Kinst 2015
 * E-mail: jakub@kinst.cz
 */
public interface EvaluationListener {
	void onEvaluationProgress(int caseNo, int totalCases, OCRResult result);


	void onEvaluationDone(EvaluationResult totalResult);
}
