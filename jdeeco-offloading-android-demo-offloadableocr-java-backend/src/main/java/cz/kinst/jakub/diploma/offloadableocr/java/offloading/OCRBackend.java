package cz.kinst.jakub.diploma.offloadableocr.java.offloading;


import org.restlet.representation.Representation;
import org.restlet.resource.Post;

import cz.kinst.jakub.diploma.offloading.backend.OffloadableBackend;

/**
 * Created by Jakub Kinst 2015
 * E-mail: jakub@kinst.cz
 */
public interface OCRBackend extends OffloadableBackend {

    @Post("?recognize")
    public OCRResult recognize(Representation file);
}
