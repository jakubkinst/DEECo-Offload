package cz.kinst.jakub.diploma.offloadableocr.java.offloading;

import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;

import java.io.File;

import cz.kinst.jakub.diploma.offloading.model.StateBundle;

/**
 * Created by Jakub Kinst 2015
 * E-mail: jakub@kinst.cz
 */
public class OCR {

    public static synchronized String recognizeText(File image, StateBundle settings) {
        try {
            Tesseract tesseract = Tesseract.getInstance();
            if (settings != null) {
                for (String key : settings.getKeys()) {
                    tesseract.setTessVariable(key, settings.getString(key, ""));
                }
            }
            String result = tesseract.doOCR(image);
            System.out.println("Recognized: " + result);
            return result;
        } catch (TesseractException e) {
            e.printStackTrace();
            return null;
        }
    }

}
