package cz.kinst.jakub.diploma.offloadableocr.evaluation;

import android.app.Activity;

import org.restlet.data.MediaType;

import java.io.File;
import java.util.ArrayList;

import cz.kinst.jakub.diploma.offloadableocr.offloading.OCRBackend;
import cz.kinst.jakub.diploma.offloadableocr.offloading.OCRBackendImpl;
import cz.kinst.jakub.diploma.offloadableocr.offloading.OCRParams;
import cz.kinst.jakub.diploma.offloadableocr.offloading.OCRResult;
import cz.kinst.jakub.diploma.offloading.Frontend;
import cz.kinst.jakub.diploma.offloading.OffloadingManager;
import cz.kinst.jakub.diploma.offloading.android.AndroidLogProvider;
import cz.kinst.jakub.diploma.offloading.android.AndroidUDPBroadcast;
import cz.kinst.jakub.diploma.offloading.backend.MultipartHolder;
import cz.kinst.jakub.diploma.offloading.listeners.OnDeploymentPlanUpdatedListener;
import cz.kinst.jakub.diploma.offloading.logger.Logger;
import cz.kinst.jakub.diploma.offloading.model.BackendDeploymentPlan;


/**
 * Created by Jakub Kinst 2015
 * E-mail: jakub@kinst.cz
 */
public class OffloadEvaluator extends Evaluator {

	public static final String OCR_URI = "/ocr";
	private OffloadingManager mOffloadingManager;
	private static Frontend mFrontend;

	public interface OnHostUpdateListener {
		void onHostUpdated(String host);
	}


	public OffloadEvaluator(Activity context, EvaluationListener listener) {
		super(context, listener);

		//init DEECo
		Logger.setProvider(new AndroidLogProvider());
		try {
			mOffloadingManager = OffloadingManager.createInstance(new AndroidUDPBroadcast(getActivity()), "ocr");

			OCRBackendImpl ocrBackend = new OCRBackendImpl(OCR_URI, getActivity());
			mOffloadingManager.attachBackend(ocrBackend, OCRBackend.class);
			mFrontend = new Frontend(mOffloadingManager);

			mOffloadingManager.init(OffloadingManager.MODE_WITH_FRONTEND);
			mOffloadingManager.start();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	@Override
	public String getHost() {
		return mFrontend.getActiveBackendAddress(OCR_URI);
	}


	public void setOnHostUpdateLstener(final OnHostUpdateListener listener) {
		mFrontend.setOnDeploymentPlanUpdatedListener(new OnDeploymentPlanUpdatedListener() {
			@Override
			public void onDeploymentPlanUpdated(BackendDeploymentPlan plan) {
				listener.onHostUpdated(plan.getPlan(OCR_URI));
			}
		});
	}


	@Override
	public OCRResult performOCR(File file) {
		final OCRBackend backend = mFrontend.getActiveBackendProxy(OCRBackend.class);
		try {
			long start = System.nanoTime();
			ArrayList<File> files = new ArrayList<File>();
			files.add(file);
			OCRResult result = backend.recognize(new MultipartHolder<OCRParams>(files, MediaType.IMAGE_JPEG, new OCRParams()).getRepresentation());
			long duration = System.nanoTime() - start;
			result.setDuration(duration);
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

	}


	@Override
	public void cleanup() {
		super.cleanup();
		try {
			mOffloadingManager.stop();
			mFrontend = null;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
