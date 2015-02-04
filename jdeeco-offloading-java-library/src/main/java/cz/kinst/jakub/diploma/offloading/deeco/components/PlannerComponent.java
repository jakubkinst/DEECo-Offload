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
import cz.kinst.jakub.diploma.offloading.deeco.model.DeploymentPlan;
import cz.kinst.jakub.diploma.offloading.deeco.model.MonitorDef;
import cz.kinst.jakub.diploma.offloading.deeco.model.NFPData;
import cz.kinst.jakub.diploma.offloading.deeco.model.NfpDataHolder;
import cz.kinst.jakub.diploma.offloading.logger.Logger;


@Component
public class PlannerComponent implements Serializable {

    public String deployedBy;
    public String appId;
    public Set<MonitorDef> monitorDefs;
    public NfpDataHolder nfpDataHolder = new NfpDataHolder();
    public Long lastPing;
    public DeploymentPlan deploymentPlan;

    public PlannerComponent(String appId, Set<MonitorDef> monitorDefs, String deviceIp) {
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
    public static void plan(@In("nfpDataHolder") NfpDataHolder nfpDataHolder, @InOut("deploymentPlan") ParamHolder<DeploymentPlan> deploymentPlan) {
        Logger.i("Planner: plan");
        DeploymentPlan newPlan = new DeploymentPlan();
        for (String appComponentId : nfpDataHolder.getAppComponentIds()) {
            HashMap<String, NFPData> alternatives = nfpDataHolder.getActiveByAppComponentId(appComponentId);
            String selectedDeviceIp = OffloadingManager.getInstance().findOptimalAlternative(appComponentId, alternatives);
            Logger.i("Found best alternative at " + selectedDeviceIp);
            newPlan.plan(appComponentId, selectedDeviceIp);

        }
        deploymentPlan.value = newPlan;
    }
}

