package cz.kinst.jakub.diploma.offloading.deeco.components;

import java.io.Serializable;

import cz.cuni.mff.d3s.deeco.annotations.Component;
import cz.cuni.mff.d3s.deeco.annotations.In;
import cz.cuni.mff.d3s.deeco.annotations.InOut;
import cz.cuni.mff.d3s.deeco.annotations.PeriodicScheduling;
import cz.cuni.mff.d3s.deeco.annotations.Process;
import cz.cuni.mff.d3s.deeco.task.ParamHolder;
import cz.kinst.jakub.diploma.offloading.OffloadingManager;
import cz.kinst.jakub.diploma.offloading.events.DeploymentPlanUpdateEvent;
import cz.kinst.jakub.diploma.offloading.model.BackendDeploymentPlan;
import cz.kinst.jakub.diploma.offloading.model.MonitorType;
import cz.kinst.jakub.diploma.offloading.utils.BusProvider;
import cz.kinst.jakub.diploma.offloading.utils.OffloadingConfig;

/**
 * Frontend monitor component is gathers deployment plan from active backend monitors
 * and periodically pushes it to the UI of the application
 * <p/>
 * ---------------------------
 * Created by Jakub Kinst 2015
 * E-mail: jakub@kinst.cz
 */
@Component
public class FrontendMonitorComponent implements Serializable {
    public int monitorType = MonitorType.FRONTEND;
    public BackendDeploymentPlan backendDeploymentPlan = new BackendDeploymentPlan(OffloadingManager.getInstance().getLocalIpAddress());
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
