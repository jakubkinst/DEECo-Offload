package cz.kinst.jakub.diploma.deecooffload.deeco.model;

import java.io.Serializable;
import java.util.HashMap;

/**
 * Created by jakubkinst on 21/01/15.
 */
public class DeploymentPlan implements Serializable {
    private HashMap<String, String> plan; //key: appComponentId, value: deviceIp

    public void plan(String appComponentId, String deviceIp) {
        plan.put(appComponentId, deviceIp);
    }

    public String getPlan(String appComponentId) {
        return plan.get(appComponentId);
    }
}
