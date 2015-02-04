package cz.kinst.jakub.diploma.offloading.deeco.components;

import java.io.Serializable;

import cz.cuni.mff.d3s.deeco.annotations.Component;
import cz.cuni.mff.d3s.deeco.annotations.In;
import cz.cuni.mff.d3s.deeco.annotations.InOut;
import cz.cuni.mff.d3s.deeco.annotations.PeriodicScheduling;
import cz.cuni.mff.d3s.deeco.annotations.Process;
import cz.cuni.mff.d3s.deeco.task.ParamHolder;
import cz.kinst.jakub.diploma.offloading.BusProvider;
import cz.kinst.jakub.diploma.offloading.OffloadingConfig;
import cz.kinst.jakub.diploma.offloading.deeco.events.DeploymentPlanUpdateEvent;
import cz.kinst.jakub.diploma.offloading.deeco.model.BackendDeploymentPlan;
import cz.kinst.jakub.diploma.offloading.deeco.model.MonitorType;

@Component
public class FrontendMonitorComponent implements Serializable {
    public int monitorType = MonitorType.FRONTEND;
    public BackendDeploymentPlan backendDeploymentPlan = new BackendDeploymentPlan();
    public Long lastPing;

    @Process
    @PeriodicScheduling(period = OffloadingConfig.PING_INTERVAL_MS)
    public static void ping(@InOut("lastPing") ParamHolder<Long> lastPing) {
        lastPing.value = System.currentTimeMillis();
    }

    @Process
    @PeriodicScheduling(period = 1000)
    public static void updateUi(@In("backendDeploymentPlan") BackendDeploymentPlan deploymentPlan) {
        BusProvider.get().post(new DeploymentPlanUpdateEvent(deploymentPlan));
    }
}
