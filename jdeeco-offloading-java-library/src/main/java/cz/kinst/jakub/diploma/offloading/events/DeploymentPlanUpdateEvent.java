package cz.kinst.jakub.diploma.offloading.events;

import cz.kinst.jakub.diploma.offloading.model.BackendDeploymentPlan;

/**
 * ---------------------------
 * Created by Jakub Kinst 2015
 * E-mail: jakub@kinst.cz
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
