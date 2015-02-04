package cz.kinst.jakub.diploma.offloading.deeco.components;

import java.io.Serializable;

import cz.cuni.mff.d3s.deeco.annotations.Component;
import cz.cuni.mff.d3s.deeco.annotations.In;
import cz.cuni.mff.d3s.deeco.annotations.InOut;
import cz.cuni.mff.d3s.deeco.annotations.PeriodicScheduling;
import cz.cuni.mff.d3s.deeco.annotations.Process;
import cz.cuni.mff.d3s.deeco.task.ParamHolder;
import cz.kinst.jakub.diploma.offloading.OffloadingConfig;
import cz.kinst.jakub.diploma.offloading.OffloadingManager;
import cz.kinst.jakub.diploma.offloading.deeco.model.BackendMonitorState;
import cz.kinst.jakub.diploma.offloading.deeco.model.MonitorType;
import cz.kinst.jakub.diploma.offloading.deeco.model.NFPData;
import cz.kinst.jakub.diploma.offloading.logger.Logger;

@Component
public class BackendMonitorComponent implements Serializable {
    public int monitorType = MonitorType.BACKEND;
    public int monitorState = BackendMonitorState.NOT_ACTIVE;
    public String appComponentId;
    public String deviceIp;
    public String resourceId;
    public NFPData nfpData;
    public Long lastPing;

    public BackendMonitorComponent(String resourceId, String deviceIp) {
        this.resourceId = resourceId;
        this.deviceIp = deviceIp;
    }

    @Process
    @PeriodicScheduling(period = OffloadingConfig.PING_INTERVAL_MS)
    public static void ping(@InOut("lastPing") ParamHolder<Long> lastPing) {
        lastPing.value = System.currentTimeMillis();
    }

    @Process
    @PeriodicScheduling(period = 10000)
    public static void measure(@In("resourceId") String resourceId, @In("deviceIp") String deviceIp, @InOut("nfpData") ParamHolder<NFPData> nfpData) {
        //measure and produce NFPData based on "simulation"
        nfpData.value = OffloadingManager.getInstance().checkPerformance(resourceId);
        Logger.i("MONITOR measure on " + deviceIp);
    }
}
