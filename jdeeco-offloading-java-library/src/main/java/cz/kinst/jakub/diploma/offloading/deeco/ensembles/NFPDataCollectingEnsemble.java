package cz.kinst.jakub.diploma.offloading.deeco.ensembles;


import cz.cuni.mff.d3s.deeco.annotations.Ensemble;
import cz.cuni.mff.d3s.deeco.annotations.In;
import cz.cuni.mff.d3s.deeco.annotations.InOut;
import cz.cuni.mff.d3s.deeco.annotations.KnowledgeExchange;
import cz.cuni.mff.d3s.deeco.annotations.Membership;
import cz.cuni.mff.d3s.deeco.annotations.PeriodicScheduling;
import cz.cuni.mff.d3s.deeco.task.ParamHolder;
import cz.kinst.jakub.diploma.offloading.deeco.DEECoManager;
import cz.kinst.jakub.diploma.offloading.logger.Logger;
import cz.kinst.jakub.diploma.offloading.model.MonitorType;
import cz.kinst.jakub.diploma.offloading.model.NFPData;
import cz.kinst.jakub.diploma.offloading.model.NFPDataHolder;
import cz.kinst.jakub.diploma.offloading.utils.OffloadingConfig;

/**
 * This ensemble takes care of pushing NFPData from Monitors to Planner
 * Coordinator: {@link cz.kinst.jakub.diploma.offloading.deeco.components.PlannerComponent}
 * Member: {@link cz.kinst.jakub.diploma.offloading.deeco.components.BackendMonitorComponent}
 * <p/>
 * ---------------------------
 * Created by Jakub Kinst 2015
 * E-mail: jakub@kinst.cz
 */
@Ensemble
@PeriodicScheduling(period = OffloadingConfig.NFP_DATA_COLLECTING_INTERVAL_MS)
public class NFPDataCollectingEnsemble {
	@Membership
	public static boolean membership(@In("coord.appId") String plannerAppId, @In("member.monitorType") int monitorType, @In("member.lastPing") long monitorLastPing, @In("coord.lastPing") long plannerLastPing) {
		// Eliminate disconnected devices' monitors by checking if they are still functional
		return DEECoManager.areComponentsStillAlive(monitorLastPing, plannerLastPing) && monitorType == MonitorType.BACKEND;
	}


	@KnowledgeExchange
	public static void knowledgeExchange(@In("member.nfpData") NFPData monitorNfpData,
										 @In("member.backendId") String monitorAppComponentId,
										 @In("member.deviceIp") String monitorDeviceIp,
										 @In("coord.deployedBy") String plannerDeployedBy,
										 @InOut("coord.nfpDataHolder") ParamHolder<NFPDataHolder> plannerDataHolder) {
		if (monitorNfpData != null) {
			plannerDataHolder.value.put(monitorAppComponentId, monitorDeviceIp, monitorNfpData);
			Logger.d("--> Monitor at " + monitorDeviceIp + " pushing NFPData to Planner at " + plannerDeployedBy);
		}
	}
}
