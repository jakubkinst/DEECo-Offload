package cz.kinst.jakub.diploma.offloading.deeco.events;

import cz.kinst.jakub.diploma.offloading.deeco.model.DeploymentPlan;

/**
 * Created by jakubkinst on 21/01/15.
 */
public class DeploymentPlanUpdateEvent {
    private final DeploymentPlan plan;

    public DeploymentPlanUpdateEvent(DeploymentPlan plan) {
        this.plan = plan;
    }

    public DeploymentPlan getPlan() {
        return plan;
    }
}
