package cz.kinst.jakub.diploma.offloadableocr.java.offloading;

import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;

import java.io.File;

import cz.kinst.jakub.diploma.offloading.model.StateBundle;

/**
 * Wrapper around Tesseract in form of tess4j library for Java providing
 * text recognition of an image.
 * <p/>
 * The Tesseract data (trained data for each language) must be available in project directory
 * <p/>
 * Created by Jakub Kinst 2015
 * E-mail: jakub@kinst.cz
 */
public class OCR {
    /**
     * Recognize text from given image with settings that can contain some of the Tesseract variables
     * (see Tesseract docs)
     *
     * @param image    Image to recognize
     * @param settings Tesseract settings
     * @return recognized text
     */
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
