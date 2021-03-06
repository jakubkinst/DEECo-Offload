package cz.kinst.jakub.diploma.offloadableocr.offloading;


import android.content.Context;
import android.os.Environment;

import org.restlet.representation.Representation;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Map;

import cz.kinst.jakub.diploma.offloadableocr.utils.Config;
import cz.kinst.jakub.diploma.offloading.OffloadingManager;
import cz.kinst.jakub.diploma.offloading.backend.BackendPerformanceProvider;
import cz.kinst.jakub.diploma.offloading.backend.MultipartHolder;
import cz.kinst.jakub.diploma.offloading.backend.OffloadableBackendImpl;
import cz.kinst.jakub.diploma.offloading.logger.Logger;
import cz.kinst.jakub.diploma.offloading.model.NFPData;
import cz.kinst.jakub.diploma.offloading.model.SingleValueNFPData;

/**
 * Created by Jakub Kinst 2015
 * E-mail: jakub@kinst.cz
 */
public class OCRBackendImpl extends OffloadableBackendImpl implements OCRBackend {

	public OCRBackendImpl() {
	}


	public OCRBackendImpl(String path, final Context context) {
		super(path, new BackendPerformanceProvider() {
			@Override
			public NFPData checkPerformance() {
				float measuredTime = measureSampleRecognition();
				Logger.d("Measured time: " + measuredTime);
				return new SingleValueNFPData(measuredTime);
			}


			@Override
			public String findOptimalAlternative(Map<String, NFPData> alternatives) {
				String bestAlternative = null;
				float min = Float.MAX_VALUE;
				for (String key : alternatives.keySet()) {
					if (bestAlternative == null)
						bestAlternative = key;
					SingleValueNFPData nfpData = (SingleValueNFPData) alternatives.get(key);
					Logger.d("Alternative at " + key + ": " + nfpData.getPerformance());
					if (nfpData.getPerformance() < min) {
						min = nfpData.getPerformance();
						bestAlternative = key;
					}
				}
				return bestAlternative;
			}
		});
	}


	public static float measureSampleRecognition() {
		long before = System.nanoTime();
		File sample = new File(Environment.getExternalStorageDirectory() + "/" + Config.APP_FOLDER + "/samples/test.png");
		OCR.getInstance().recognizeText(sample, null);
		return System.nanoTime() - before;
	}


	@Override
	public OCRResult recognize(Representation representation) {
		try {
			MultipartHolder<OCRParams> multipartHolder = new MultipartHolder<>(representation, OCRParams.class);
			byte[] file = multipartHolder.getReceivedFiles().get(0).get();

			File outputFile = new File(Environment.getExternalStorageDirectory() + File.separator + "received.jpg");
			BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(outputFile));
			bos.write(file);
			bos.flush();
			bos.close();
			String recognizedText = OCR.getInstance().recognizeText(outputFile, getStateData());

			return new OCRResult(recognizedText, OffloadingManager.getInstance().getLocalIpAddress());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}
