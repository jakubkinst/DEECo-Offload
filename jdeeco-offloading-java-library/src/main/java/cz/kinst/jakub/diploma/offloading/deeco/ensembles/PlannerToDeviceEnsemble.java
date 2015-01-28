package cz.kinst.jakub.diploma.offloading.deeco.ensembles;


import java.util.Map;
import java.util.Set;

import cz.cuni.mff.d3s.deeco.annotations.Ensemble;
import cz.cuni.mff.d3s.deeco.annotations.In;
import cz.cuni.mff.d3s.deeco.annotations.InOut;
import cz.cuni.mff.d3s.deeco.annotations.KnowledgeExchange;
import cz.cuni.mff.d3s.deeco.annotations.Membership;
import cz.cuni.mff.d3s.deeco.annotations.PeriodicScheduling;
import cz.cuni.mff.d3s.deeco.task.ParamHolder;
import cz.kinst.jakub.diploma.offloading.deeco.DEECoManager;
import cz.kinst.jakub.diploma.offloading.deeco.model.MonitorDef;

/**
 * This ensemble is responsible for pushing MonitorDefs from Planner to Device
 * Coordinator: Planner
 * Member: Device
 */
@Ensemble
@PeriodicScheduling(period = 13000) // check every 13 seconds TODO: tune this value
public class PlannerToDeviceEnsemble {
    @Membership
    public static boolean membership(@In("coord.appId") String plannerAppId, @In("coord.lastPing") long plannerLastPing, @In("member.lastPing") long deviceLastPing, @In("member.ip") String deviceIp) {
        return DEECoManager.isComponentStillAlive(deviceLastPing) && DEECoManager.isComponentStillAlive(plannerLastPing);
    }

    @KnowledgeExchange
    public static void knowledgeExchange(@InOut("member.monitorDefs") ParamHolder<Map<String, Set<MonitorDef>>> deviceMonitorDefs,
                                         @In("coord.monitorDefs") Set<MonitorDef> plannerMonitorDefs,
                                         @In("coord.appId") String plannerAppId) {
        deviceMonitorDefs.value.put(plannerAppId, plannerMonitorDefs);
    }
}
