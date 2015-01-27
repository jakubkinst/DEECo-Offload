package cz.kinst.jakub.offloading.deeco.ensembles;


import cz.cuni.mff.d3s.deeco.annotations.Ensemble;
import cz.cuni.mff.d3s.deeco.annotations.In;
import cz.cuni.mff.d3s.deeco.annotations.InOut;
import cz.cuni.mff.d3s.deeco.annotations.KnowledgeExchange;
import cz.cuni.mff.d3s.deeco.annotations.Membership;
import cz.cuni.mff.d3s.deeco.annotations.PeriodicScheduling;
import cz.cuni.mff.d3s.deeco.task.ParamHolder;
import cz.kinst.jakub.offloading.deeco.model.NFPData;
import cz.kinst.jakub.offloading.deeco.model.NfpDataHolder;

/**
 * This ensemble takes care of pushing NFPData from Monitors to Planner
 * Coordinator: Planner
 * Member: Monitor
 */
@Ensemble
@PeriodicScheduling(period = 6000) // check every second TODO: tune this value
public class PlannerToMonitorEnsemble {
    @Membership
    public static boolean membership(@In("coord.appId") String plannerAppId, @In("member.deviceIp") String monitorDeviceIp) {
        return true; // all Monitors with all Planners
    }

    @KnowledgeExchange
    public static void knowledgeExchange(@In("member.nfpData") NFPData monitorNfpData,
                                         @In("member.resourceId") String monitorAppComponentId,
                                         @In("member.deviceIp") String monitorDeviceIp,
                                         @InOut("coord.nfpDataHolder") ParamHolder<NfpDataHolder> plannerDataHolder) {
        plannerDataHolder.value.put(monitorAppComponentId, monitorDeviceIp, monitorNfpData);
    }
}
