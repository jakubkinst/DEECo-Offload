package cz.kinst.jakub.offloading.deeco.model;

import java.io.Serializable;

/**
 * Created by jakubkinst on 21/01/15.
 */
public class MonitorDef implements Serializable {
    private final String mResourceId;

    public MonitorDef(String resourceId) {
        mResourceId = resourceId;
    }

    public String getResourceId() {
        return mResourceId;
    }
}
