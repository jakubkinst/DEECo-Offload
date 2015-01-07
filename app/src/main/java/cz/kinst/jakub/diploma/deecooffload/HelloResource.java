package cz.kinst.jakub.diploma.deecooffload;

import org.restlet.resource.Post;

/**
 * Created by jakubkinst on 07/01/15.
 */
public interface HelloResource {
    @Post
    public Message getHello(String name);
}
