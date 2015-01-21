package cz.kinst.jakub.diploma.deecooffload.deeco.events;

import cz.kinst.jakub.diploma.deecooffload.deeco.model.DeploymentPlan;

/**
 * Created by jakubkinst on 21/01/15.
 */
public class PlanUpdateEvent {
    private final DeploymentPlan plan;

    public PlanUpdateEvent(DeploymentPlan plan) {
        this.plan = plan;
    }

    public DeploymentPlan getPlan() {
        return plan;
    }
}
