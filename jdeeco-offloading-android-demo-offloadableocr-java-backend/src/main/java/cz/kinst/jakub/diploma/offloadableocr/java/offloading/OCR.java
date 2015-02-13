package cz.kinst.jakub.diploma.offloadableocr.java.offloading;

import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;

import java.io.File;

/**
 * Created by jakubkinst on 04/02/15.
 */
public class OCR {


    public static OCR getInstance() {
        return new OCR();
    }

    private OCR() {
    }

    public String recognizeText(File image) {
        try {
            Tesseract tesseract = Tesseract.getInstance();
            String result = tesseract.doOCR(image);
            System.out.println("Recognized: " + result);
            return result;
        } catch (TesseractException e) {
            e.printStackTrace();
            return null;
        }
    }

}
