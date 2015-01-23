package cz.kinst.jakub.offloading.deeco.model;

/**
 * Created by jakubkinst on 21/01/15.
 */
public class MonitorDef {
    private final String mResourceId;

    public MonitorDef(String resourceId) {
        mResourceId = resourceId;
    }

    public String getResourceId() {
        return mResourceId;
    }
}
