package cz.kinst.jakub.diploma.offloadableocr.java;

import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;

import java.io.File;

public class Main
{
    public static void main(String[] args) {
        File imageFile = new File("samples/eurotext.jpg");
        Tesseract instance = Tesseract.getInstance(); // JNA Interface Mapping
        // Tesseract1 instance = new Tesseract1(); // JNA Direct Mapping

        try {
            String result = instance.doOCR(imageFile);
            System.out.println(result);
        } catch (TesseractException e) {
            System.err.println(e.getMessage());
        }
    }
}
