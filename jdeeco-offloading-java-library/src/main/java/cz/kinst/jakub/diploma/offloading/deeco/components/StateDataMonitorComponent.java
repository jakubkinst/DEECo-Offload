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
import cz.kinst.jakub.diploma.offloading.deeco.events.ShouldPullBackendStateDataEvent;
import cz.kinst.jakub.diploma.offloading.deeco.model.MonitorType;

@Component
public class StateDataMonitorComponent implements Serializable {
    public int monitorType = MonitorType.STATE_DATA;
    public String backendId;
    public String currentBackendAddress;
    public Long lastPing;

    public StateDataMonitorComponent(String backendId) {
        this.backendId = backendId;
    }

    @Process
    @PeriodicScheduling(period = OffloadingConfig.PING_INTERVAL_MS)
    public static void ping(@InOut("lastPing") ParamHolder<Long> lastPing) {
        lastPing.value = System.currentTimeMillis();
    }

    @Process
    @PeriodicScheduling(period = 12000)
    public static void doPeriodicPull(@In("backendId") String backendId, @In("currentBackendAddress") String currentBackendAddress) {
        if (currentBackendAddress != null)
            BusProvider.get().post(new ShouldPullBackendStateDataEvent(backendId, currentBackendAddress));
    }
}
