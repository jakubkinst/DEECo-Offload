package cz.kinst.jakub.diploma.offloadableocr.offloading;


import org.restlet.representation.Representation;
import org.restlet.resource.Post;

import cz.kinst.jakub.diploma.offloading.backend.OffloadableBackend;

public interface OCRBackend extends OffloadableBackend {

    @Post("?recognize")
    public OCRResult recognize(Representation file);
}
