package cz.kinst.jakub.diploma.offloadableocr.offloading;


import android.content.Context;
import android.os.Environment;

import org.restlet.representation.Representation;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Map;

import cz.kinst.jakub.diploma.offloadableocr.OCR;
import cz.kinst.jakub.diploma.offloadableocr.OCRResult;
import cz.kinst.jakub.diploma.offloading.OffloadingManager;
import cz.kinst.jakub.diploma.offloading.deeco.model.NFPData;
import cz.kinst.jakub.diploma.offloading.deeco.model.SimpleValueNFPData;
import cz.kinst.jakub.diploma.offloading.logger.Logger;
import cz.kinst.jakub.diploma.offloading.resource.BackendPerformanceProvider;
import cz.kinst.jakub.diploma.offloading.resource.MultipartHolder;
import cz.kinst.jakub.diploma.offloading.resource.OffloadableBackendImpl;

public class OCRBackendImpl extends OffloadableBackendImpl implements OCRBackend {

    public OCRBackendImpl() {
    }

    public OCRBackendImpl(String path, final Context context) {
        super(path, new BackendPerformanceProvider() {
            @Override
            public NFPData checkPerformance() {
                float measuredTime = measureSampleRecognition(context);
                Logger.d("Measured time: " + measuredTime);
                return new SimpleValueNFPData(measuredTime);
            }

            @Override
            public String findOptimalAlternative(Map<String, NFPData> alternatives) {
                String bestAlternative = null;
                float max = Float.MIN_VALUE;
                for (String key : alternatives.keySet()) {
                    if (bestAlternative == null)
                        bestAlternative = key;
                    SimpleValueNFPData nfpData = (SimpleValueNFPData) alternatives.get(key);
                    if (nfpData.getPerformance() > max) {
                        max = nfpData.getPerformance();
                        bestAlternative = key;
                    }
                }
                return bestAlternative;
            }
        });
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
            OCR ocr = new OCR();
            String recognizedText = ocr.recognizeText(outputFile);

            return new OCRResult(recognizedText, OffloadingManager.getInstance().getLocalIpAddress());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    public static float measureSampleRecognition(Context context) {
        long before = System.currentTimeMillis();
        OCR ocr = new OCR();
        File sample = new File(Environment.getExternalStorageDirectory() + "/tesseract/samples/small.jpg");
        ocr.recognizeText(sample);
        return System.currentTimeMillis() - before;
    }

}
