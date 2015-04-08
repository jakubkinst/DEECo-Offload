package cz.kinst.jakub.diploma.offloading.deeco.components;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Set;

import cz.cuni.mff.d3s.deeco.annotations.Component;
import cz.cuni.mff.d3s.deeco.annotations.In;
import cz.cuni.mff.d3s.deeco.annotations.InOut;
import cz.cuni.mff.d3s.deeco.annotations.PeriodicScheduling;
import cz.cuni.mff.d3s.deeco.annotations.Process;
import cz.cuni.mff.d3s.deeco.task.ParamHolder;
import cz.kinst.jakub.diploma.offloading.OffloadingManager;
import cz.kinst.jakub.diploma.offloading.logger.Logger;
import cz.kinst.jakub.diploma.offloading.model.BackendDeploymentPlan;
import cz.kinst.jakub.diploma.offloading.model.BackendMonitorDef;
import cz.kinst.jakub.diploma.offloading.model.NFPData;
import cz.kinst.jakub.diploma.offloading.model.NFPDataHolder;
import cz.kinst.jakub.diploma.offloading.utils.OffloadingConfig;

/**
 * Planner component responsible for gathering {@link NFPData} for backends
 * from monitors and determining current {@link cz.kinst.jakub.diploma.offloading.model.BackendDeploymentPlan}
 * Planner is also responsible for pushing {@link cz.kinst.jakub.diploma.offloading.model.BackendMonitorDef} to {@link cz.kinst.jakub.diploma.offloading.deeco.components.DeviceComponent}
 * <p/>
 * ---------------------------
 * Created by Jakub Kinst 2015
 * E-mail: jakub@kinst.cz
 */
@Component
public class PlannerComponent implements Serializable {

	public String deployedBy;
	public String appId;
	public Set<BackendMonitorDef> monitorDefs;
	public NFPDataHolder nfpDataHolder = new NFPDataHolder();
	public Long lastPing;
	public BackendDeploymentPlan backendDeploymentPlan;


	public PlannerComponent(String appId, Set<BackendMonitorDef> monitorDefs, String deviceIp) {
		this.appId = appId;
		this.monitorDefs = monitorDefs;
		this.deployedBy = deviceIp;
	}


	@Process
	@PeriodicScheduling(period = OffloadingConfig.PING_INTERVAL_MS)
	public static void ping(@InOut("lastPing") ParamHolder<Long> lastPing) {
		lastPing.value = System.currentTimeMillis();
	}


	@Process
	@PeriodicScheduling(period = OffloadingConfig.IP_UPDATE_INTERVAL_MS)
	public static void updateDeviceIp(@InOut("deployedBy") ParamHolder<String> deviceIp) {
		deviceIp.value = OffloadingManager.getInstance().getLocalIpAddress();
	}


	@Process
	@PeriodicScheduling(period = 5000)
	public static void plan(@In("nfpDataHolder") NFPDataHolder nfpDataHolder, @InOut("backendDeploymentPlan") ParamHolder<BackendDeploymentPlan> deploymentPlan) {
		Logger.i("Planner: plan");
		BackendDeploymentPlan newPlan = new BackendDeploymentPlan(OffloadingManager.getInstance().getLocalIpAddress());
		for (String appComponentId : nfpDataHolder.getBackendIds()) {
			HashMap<String, NFPData> alternatives = nfpDataHolder.getActiveByBackendId(appComponentId);
			String selectedDeviceIp = OffloadingManager.getInstance().findOptimalAlternative(appComponentId, alternatives);
			Logger.i("Found best alternative at " + selectedDeviceIp + ": " + alternatives.get(selectedDeviceIp).toString());
			newPlan.plan(appComponentId, selectedDeviceIp);

		}
		deploymentPlan.value = newPlan;
	}
}

