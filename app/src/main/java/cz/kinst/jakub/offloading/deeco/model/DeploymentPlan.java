package cz.kinst.jakub.offloading.deeco.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by jakubkinst on 21/01/15.
 */
public class DeploymentPlan implements Serializable {
    private Map<String, String> plan = new HashMap<>(); //key: appComponentId, value: deviceIp

    public void plan(String appComponentId, String deviceIp) {
        plan.put(appComponentId, deviceIp);
    }

    public String getPlan(String appComponentId) {
        return plan.get(appComponentId);
    }
}
