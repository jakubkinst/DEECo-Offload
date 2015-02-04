package cz.kinst.jakub.diploma.offloading.deeco.events;

import cz.kinst.jakub.diploma.offloading.deeco.model.BackendDeploymentPlan;

/**
 * Created by jakubkinst on 21/01/15.
 */
public class DeploymentPlanUpdateEvent {
    private final BackendDeploymentPlan plan;

    public DeploymentPlanUpdateEvent(BackendDeploymentPlan plan) {
        this.plan = plan;
    }

    public BackendDeploymentPlan getPlan() {
        return plan;
    }
}
