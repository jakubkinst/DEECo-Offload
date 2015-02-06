package cz.kinst.jakub.diploma.offloading.deeco.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by jakubkinst on 21/01/15.
 */
public class BackendDeploymentPlan implements Serializable {
    private final String mLocalIpAddress;
    private Map<String, String> mPlan = new HashMap<>(); //key: appComponentId, value: deviceIp

    public BackendDeploymentPlan(String localIpAddress) {
        mLocalIpAddress = localIpAddress;
    }

    public void plan(String backendId, String deviceIp) {
        mPlan.put(backendId, deviceIp);
    }

    public String getPlan(String backendId) {
        return mPlan.containsKey(backendId) ? mPlan.get(backendId) : mLocalIpAddress;
    }

    public java.util.Set<String> getBackends() {
        return mPlan.keySet();
    }
}
