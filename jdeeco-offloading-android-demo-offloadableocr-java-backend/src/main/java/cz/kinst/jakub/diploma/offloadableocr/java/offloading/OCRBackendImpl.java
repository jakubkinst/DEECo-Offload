package cz.kinst.jakub.diploma.offloadableocr.java.offloading;


import org.restlet.representation.Representation;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Map;

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

    public OCRBackendImpl(String path) {
        super(path, new BackendPerformanceProvider() {
            @Override
            public NFPData checkPerformance() {
                float measuredTime = measureSampleRecognition();
                Logger.d("Measured time: " + measuredTime);
                return new SimpleValueNFPData(measuredTime);
            }

            @Override
            public String findOptimalAlternative(Map<String, NFPData> alternatives) {
                String bestAlternative = null;
                float min = Float.MAX_VALUE;
                for (String key : alternatives.keySet()) {
                    if (bestAlternative == null)
                        bestAlternative = key;
                    SimpleValueNFPData nfpData = (SimpleValueNFPData) alternatives.get(key);
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

    @Override
    public OCRResult recognize(Representation representation) {
        try {
            MultipartHolder<OCRParams> multipartHolder = new MultipartHolder<>(representation, OCRParams.class);
            byte[] file = multipartHolder.getReceivedFiles().get(0).get();

            File outputFile = new File("ocr-images/received_" + System.currentTimeMillis() + ".jpg");
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(outputFile));
            bos.write(file);
            bos.flush();
            bos.close();
            String recognizedText = OCR.recognizeText(outputFile, getStateData());

            return new OCRResult(recognizedText, OffloadingManager.getInstance().getLocalIpAddress());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    public static synchronized float measureSampleRecognition() {
        long before = System.currentTimeMillis();
        File sample = new File("samples/test.png");
        OCR.recognizeText(sample, null);
        return System.currentTimeMillis() - before;
//        return 0;
    }

}
