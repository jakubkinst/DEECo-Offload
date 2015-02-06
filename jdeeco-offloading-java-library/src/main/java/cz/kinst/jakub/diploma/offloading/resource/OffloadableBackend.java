package cz.kinst.jakub.diploma.offloading.resource;

import org.restlet.resource.Get;
import org.restlet.resource.Post;

import cz.kinst.jakub.diploma.offloading.StateBundle;

/**
 * Created by jakubkinst on 09/01/15.
 */
public abstract interface OffloadableBackend {
    @Post("?setStateData")
    public void setStateData(StateBundle stateData);

    @Get("?getStateData")
    public StateBundle getStateData();
}
