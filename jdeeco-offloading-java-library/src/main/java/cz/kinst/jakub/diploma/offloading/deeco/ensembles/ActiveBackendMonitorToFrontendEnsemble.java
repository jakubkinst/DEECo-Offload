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
 * This ensemble takes care of informing UIMonitor about active backends
 * Coordinator: {@link cz.kinst.jakub.diploma.offloading.deeco.components.FrontendMonitorComponent}
 * Member: {@link cz.kinst.jakub.diploma.offloading.deeco.components.BackendMonitorComponent}
 */
@Ensemble
@PeriodicScheduling(period = OffloadingConfig.UI_MONITOR_UPDATE_INTERVAL_MS)
public class ActiveBackendMonitorToFrontendEnsemble {
    @Membership
    public static boolean membership(@In("coord.monitorType") int frontendMonitorType,
                                     @In("member.monitorType") int backendMonitorType,
                                     @In("member.monitorState") int backendMonitorState,
                                     @In("member.lastPing") long backendMonitorLastPing,
                                     @In("coord.lastPing") long frontendMonitorLastPing) {
        // connect only active backend monitors with ui monitor
        return DEECoManager.areComponentsStillAlive(backendMonitorLastPing, frontendMonitorLastPing)
                && frontendMonitorType == MonitorType.FRONTEND
                && backendMonitorType == MonitorType.BACKEND
                && backendMonitorState == BackendMonitorState.ACTIVE;
    }

    @KnowledgeExchange
    public static void knowledgeExchange(@InOut("coord.backendDeploymentPlan") ParamHolder<BackendDeploymentPlan> frontendDeploymentPlan,
                                         @In("member.backendId") String backendBackendId,
                                         @In("member.deviceIp") String backendDeviceIp) {
        frontendDeploymentPlan.value.plan(backendBackendId, backendDeviceIp);
    }

}