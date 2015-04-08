package cz.kinst.jakub.diploma.offloading.deeco.components;

import java.io.Serializable;

import cz.cuni.mff.d3s.deeco.annotations.Component;
import cz.cuni.mff.d3s.deeco.annotations.In;
import cz.cuni.mff.d3s.deeco.annotations.InOut;
import cz.cuni.mff.d3s.deeco.annotations.PeriodicScheduling;
import cz.cuni.mff.d3s.deeco.annotations.Process;
import cz.cuni.mff.d3s.deeco.task.ParamHolder;
import cz.kinst.jakub.diploma.offloading.OffloadingManager;
import cz.kinst.jakub.diploma.offloading.logger.Logger;
import cz.kinst.jakub.diploma.offloading.model.BackendMonitorState;
import cz.kinst.jakub.diploma.offloading.model.MonitorType;
import cz.kinst.jakub.diploma.offloading.model.NFPData;
import cz.kinst.jakub.diploma.offloading.utils.OffloadingConfig;

/**
 * Backend Monitor Component is responsible for measuring performance of a backend on current device and producing
 * appropriate {@link cz.kinst.jakub.diploma.offloading.model.NFPData}.
 * It must also watch for changes of current IP address in case of switching between networks.
 * <p/>
 * ---------------------------
 * Created by Jakub Kinst 2015
 * E-mail: jakub@kinst.cz
 */
@Component
public class BackendMonitorComponent implements Serializable {
	public int monitorType = MonitorType.BACKEND;
	public int monitorState = BackendMonitorState.NOT_ACTIVE;
	public String deviceIp;
	public String backendId;
	public NFPData nfpData;
	public Long lastPing;


	public BackendMonitorComponent(String backendId, String deviceIp) {
		this.backendId = backendId;
		this.deviceIp = deviceIp;
	}


	@Process
	@PeriodicScheduling(period = OffloadingConfig.PING_INTERVAL_MS)
	public static void ping(@InOut("lastPing") ParamHolder<Long> lastPing) {
		lastPing.value = System.currentTimeMillis();
	}


	@Process
	@PeriodicScheduling(period = OffloadingConfig.IP_UPDATE_INTERVAL_MS)
	public static void updateDeviceIp(@InOut("deviceIp") ParamHolder<String> deviceIp) {
		deviceIp.value = OffloadingManager.getInstance().getLocalIpAddress();
	}


	@Process
	@PeriodicScheduling(period = 10000)
	public static void measure(@In("backendId") String backendId, @In("deviceIp") String deviceIp, @InOut("nfpData") ParamHolder<NFPData> nfpData) {
		//measure and produce NFPData based on "simulation"
		nfpData.value = OffloadingManager.getInstance().measureBackendPerformance(backendId);
		Logger.i("MONITOR measure on " + deviceIp);
	}
}
