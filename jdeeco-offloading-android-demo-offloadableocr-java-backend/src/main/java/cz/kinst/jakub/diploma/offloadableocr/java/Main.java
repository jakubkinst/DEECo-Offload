package cz.kinst.jakub.diploma.offloadableocr.java;

import cz.kinst.jakub.diploma.offloadableocr.java.offloading.OCRBackend;
import cz.kinst.jakub.diploma.offloadableocr.java.offloading.OCRBackendImpl;
import cz.kinst.jakub.diploma.offloading.OffloadingManager;
import cz.kinst.jakub.diploma.offloading.logger.JavaLogProvider;
import cz.kinst.jakub.diploma.offloading.logger.Logger;
import cz.kinst.jakub.diploma.udpbroadcast.JavaUDPBroadcast;

public class Main {
    private static final String OCR_URI = "/ocr";
    private static OffloadingManager mOffloadingManager;

    public static void main(String[] args) {
        //init DEECo
        Logger.setProvider(new JavaLogProvider());

        try {
            mOffloadingManager = OffloadingManager.create(new JavaUDPBroadcast(), "ocr");

            OCRBackendImpl ocrBackend = new OCRBackendImpl(OCR_URI);
            mOffloadingManager.attachBackend(ocrBackend, OCRBackend.class);

            mOffloadingManager.init(OffloadingManager.TYPE_ONLY_BACKEND);
            mOffloadingManager.start();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
