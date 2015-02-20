package cz.kinst.jakub.diploma.offloading.deeco.components;

import java.io.Serializable;

import cz.cuni.mff.d3s.deeco.annotations.Component;
import cz.cuni.mff.d3s.deeco.annotations.In;
import cz.cuni.mff.d3s.deeco.annotations.InOut;
import cz.cuni.mff.d3s.deeco.annotations.PeriodicScheduling;
import cz.cuni.mff.d3s.deeco.annotations.Process;
import cz.cuni.mff.d3s.deeco.task.ParamHolder;
import cz.kinst.jakub.diploma.offloading.events.ShouldPullBackendStateDataEvent;
import cz.kinst.jakub.diploma.offloading.model.MonitorType;
import cz.kinst.jakub.diploma.offloading.utils.BusProvider;
import cz.kinst.jakub.diploma.offloading.utils.OffloadingConfig;

/**
 * State Data Monitor Component responsible for periodic pulling of state data from current backend
 * <p/>
 * ---------------------------
 * Created by Jakub Kinst 2015
 * E-mail: jakub@kinst.cz
 */
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
