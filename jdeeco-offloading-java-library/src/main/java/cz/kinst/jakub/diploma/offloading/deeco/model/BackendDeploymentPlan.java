package cz.kinst.jakub.diploma.offloading.deeco.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by jakubkinst on 21/01/15.
 */
public class BackendDeploymentPlan implements Serializable {
    private Map<String, String> plan = new HashMap<>(); //key: appComponentId, value: deviceIp

    public void plan(String backendId, String deviceIp) {
        plan.put(backendId, deviceIp);
    }

    public String getPlan(String backendId) {
        return plan.get(backendId);
    }
}
