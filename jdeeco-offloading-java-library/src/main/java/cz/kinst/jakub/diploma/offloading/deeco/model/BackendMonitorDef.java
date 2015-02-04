package cz.kinst.jakub.diploma.offloading.deeco.model;

import java.io.Serializable;

/**
 * Created by jakubkinst on 21/01/15.
 */
public class BackendMonitorDef implements Serializable {
    private final String mBackendId;

    public BackendMonitorDef(String backendId) {
        mBackendId = backendId;
    }

    public String getBackendId() {
        return mBackendId;
    }
}
