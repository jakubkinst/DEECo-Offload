package cz.kinst.jakub.diploma.offloading;

import cz.kinst.jakub.diploma.offloading.deeco.model.DeploymentPlan;

/**
 * Created by jakubkinst on 27/01/15.
 */
public interface OnDeploymentPlanUpdatedListener {
    public void onDeploymentPlanUpdated(DeploymentPlan plan);
}
