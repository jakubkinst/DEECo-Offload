package cz.kinst.jakub.diploma.offloading;

import cz.kinst.jakub.diploma.offloading.deeco.model.BackendDeploymentPlan;

/**
 * Created by jakubkinst on 27/01/15.
 */
public interface OnDeploymentPlanUpdatedListener {
    public void onDeploymentPlanUpdated(BackendDeploymentPlan plan);
}
