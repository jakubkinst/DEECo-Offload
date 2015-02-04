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
import cz.kinst.jakub.diploma.offloading.OffloadingConfig;
import cz.kinst.jakub.diploma.offloading.OffloadingManager;
import cz.kinst.jakub.diploma.offloading.deeco.model.BackendDeploymentPlan;
import cz.kinst.jakub.diploma.offloading.deeco.model.BackendMonitorDef;
import cz.kinst.jakub.diploma.offloading.deeco.model.NFPData;
import cz.kinst.jakub.diploma.offloading.deeco.model.NFPDataHolderX;
import cz.kinst.jakub.diploma.offloading.logger.Logger;


@Component
public class PlannerComponent implements Serializable {

    public String deployedBy;
    public String appId;
    public Set<BackendMonitorDef> monitorDefs;
    public NFPDataHolderX nfpDataHolder = new NFPDataHolderX();
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
    @PeriodicScheduling(period = 5000)
    public static void plan(@In("nfpDataHolder") NFPDataHolderX nfpDataHolder, @InOut("backendDeploymentPlan") ParamHolder<BackendDeploymentPlan> deploymentPlan) {
        Logger.i("Planner: plan");
        BackendDeploymentPlan newPlan = new BackendDeploymentPlan();
        for (String appComponentId : nfpDataHolder.getBackendIds()) {
            HashMap<String, NFPData> alternatives = nfpDataHolder.getActiveByBackendId(appComponentId);
            String selectedDeviceIp = OffloadingManager.getInstance().findOptimalAlternative(appComponentId, alternatives);
            Logger.i("Found best alternative at " + selectedDeviceIp);
            newPlan.plan(appComponentId, selectedDeviceIp);

        }
        deploymentPlan.value = newPlan;
    }
}

