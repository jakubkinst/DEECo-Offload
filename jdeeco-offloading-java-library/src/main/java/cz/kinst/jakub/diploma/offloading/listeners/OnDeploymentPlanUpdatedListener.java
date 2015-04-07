package cz.kinst.jakub.diploma.offloading.listeners;

import cz.kinst.jakub.diploma.offloading.model.BackendDeploymentPlan;

/**
 * ---------------------------
 * Created by Jakub Kinst 2015
 * E-mail: jakub@kinst.cz
 */
public interface OnDeploymentPlanUpdatedListener {
	public void onDeploymentPlanUpdated(BackendDeploymentPlan plan);
}
