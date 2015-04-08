package cz.kinst.jakub.diploma.offloading.backend;

import org.restlet.resource.Get;
import org.restlet.resource.Post;

import cz.kinst.jakub.diploma.offloading.model.StateBundle;

/**
 * Each Offloadable backend interface must extend this interface.
 * It contains methods common for all backends to set/retrieve state data.
 * Methods have to be annotated with Restlet resource annotations
 * <p/>
 * ---------------------------
 * Created by Jakub Kinst 2015
 * E-mail: jakub@kinst.cz
 */
public abstract interface OffloadableBackend {
	@Get("?getStateData")
	public StateBundle getStateData();

	@Post("?setStateData")
	public void setStateData(StateBundle stateData);
}
