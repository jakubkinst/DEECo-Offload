package cz.kinst.jakub.diploma.offloading.deeco.ensembles;


import cz.cuni.mff.d3s.deeco.annotations.Ensemble;
import cz.cuni.mff.d3s.deeco.annotations.In;
import cz.cuni.mff.d3s.deeco.annotations.InOut;
import cz.cuni.mff.d3s.deeco.annotations.KnowledgeExchange;
import cz.cuni.mff.d3s.deeco.annotations.Membership;
import cz.cuni.mff.d3s.deeco.annotations.PeriodicScheduling;
import cz.cuni.mff.d3s.deeco.task.ParamHolder;
import cz.kinst.jakub.diploma.offloading.deeco.DEECoManager;
import cz.kinst.jakub.diploma.offloading.deeco.model.NFPData;
import cz.kinst.jakub.diploma.offloading.deeco.model.NfpDataHolder;
import cz.kinst.jakub.diploma.offloading.logger.Logger;

/**
 * This ensemble takes care of pushing NFPData from Monitors to Planner
 * Coordinator: Planner
 * Member: Monitor
 */
@Ensemble
@PeriodicScheduling(period = 6000) // check every second TODO: tune this value
public class PlannerToMonitorEnsemble {
    @Membership
    public static boolean membership(@In("coord.appId") String plannerAppId, @In("member.deviceIp") String monitorDeviceIp, @In("member.lastPing") long monitorLastPing, @In("coord.lastPing") long plannerLastPing) {
        // Eliminate disconnected devices' monitors by checking if they are still functional
        return DEECoManager.isComponentStillAlive(monitorLastPing) && DEECoManager.isComponentStillAlive(plannerLastPing);
    }


    @KnowledgeExchange
    public static void knowledgeExchange(@In("member.nfpData") NFPData monitorNfpData,
                                         @In("member.resourceId") String monitorAppComponentId,
                                         @In("member.deviceIp") String monitorDeviceIp,
                                         @In("coord.deployedBy") String plannerDeployedBy,
                                         @InOut("coord.nfpDataHolder") ParamHolder<NfpDataHolder> plannerDataHolder) {
        plannerDataHolder.value.put(monitorAppComponentId, monitorDeviceIp, monitorNfpData);
        Logger.d("--> Monitor at " + monitorDeviceIp + " pushing NFPData to Planner at " + plannerDeployedBy);
    }
}
