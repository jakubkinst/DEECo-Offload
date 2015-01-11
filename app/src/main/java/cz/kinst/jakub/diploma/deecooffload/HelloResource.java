package cz.kinst.jakub.diploma.deecooffload;


import org.restlet.representation.Representation;
import org.restlet.resource.Post;

import cz.kinst.jakub.offloading.OffloadingResource;

public interface HelloResource extends OffloadingResource {

    @Post("?hello")
    public Message getHello(String name);

    @Post("?hi")
    public Message getHi(String name);

    @Post("?file")
    public Message testFile(Representation file);
}
