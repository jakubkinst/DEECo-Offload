package cz.kinst.jakub.offloading;

import org.restlet.resource.ServerResource;

/**
 * Created by jakubkinst on 07/01/15.
 */
public abstract class OffloadingResourceImpl extends ServerResource {


    private String mPath;

    public OffloadingResourceImpl() {
    }

    public OffloadingResourceImpl(String path) {
        mPath = path;
    }

    public String getPath() {
        return mPath;
    }
}
