package cz.kinst.jakub.diploma.offloading.deeco.ensembles;


import cz.cuni.mff.d3s.deeco.annotations.Ensemble;
import cz.cuni.mff.d3s.deeco.annotations.In;
import cz.cuni.mff.d3s.deeco.annotations.InOut;
import cz.cuni.mff.d3s.deeco.annotations.KnowledgeExchange;
import cz.cuni.mff.d3s.deeco.annotations.Membership;
import cz.cuni.mff.d3s.deeco.annotations.PeriodicScheduling;
import cz.cuni.mff.d3s.deeco.task.ParamHolder;
import cz.kinst.jakub.diploma.offloading.OffloadingConfig;
import cz.kinst.jakub.diploma.offloading.deeco.DEECoManager;
import cz.kinst.jakub.diploma.offloading.deeco.model.BackendDeploymentPlan;
import cz.kinst.jakub.diploma.offloading.deeco.model.BackendMonitorState;
import cz.kinst.jakub.diploma.offloading.deeco.model.MonitorType;

/**
 * This ensemble takes care of informing BackendMonitors about their state (running / not running)
 * Coordinator: {@link cz.kinst.jakub.diploma.offloading.deeco.components.PlannerComponent}
 * Member: {@link cz.kinst.jakub.diploma.offloading.deeco.components.BackendMonitorComponent}
 */
@Ensemble
@PeriodicScheduling(period = OffloadingConfig.STATE_DISTRIBUTING_INTERVAL_MS)
public class BackendStateDistributingEnsemble {
    @Membership
    public static boolean membership(@In("coord.appId") String plannerAppId, @In("member.deviceIp") String monitorDeviceIp, @In("member.monitorType") int monitorType, @In("member.lastPing") long monitorLastPing, @In("coord.lastPing") long plannerLastPing) {
        // Eliminate disconnected devices' monitors by checking if they are still functional
        return DEECoManager.areComponentsStillAlive(monitorLastPing, plannerLastPing) && monitorType == MonitorType.BACKEND;
    }


    @KnowledgeExchange
    public static void knowledgeExchange(@InOut("member.monitorState") ParamHolder<Integer> monitorState,
                                         @In("member.backendId") String monitorAppComponentId,
                                         @In("member.deviceIp") String monitorDeviceIp,
                                         @In("coord.backendDeploymentPlan") BackendDeploymentPlan plannerBackendDeploymentPlan) {
        String activeIp = plannerBackendDeploymentPlan.getPlan(monitorAppComponentId);
        boolean isThisMonitorActive = monitorDeviceIp.equals(activeIp);
        monitorState.value = isThisMonitorActive ? BackendMonitorState.ACTIVE : BackendMonitorState.NOT_ACTIVE;
    }

}
