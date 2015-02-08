package cz.kinst.jakub.diploma.offloading.deeco.components;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import cz.cuni.mff.d3s.deeco.annotations.Component;
import cz.cuni.mff.d3s.deeco.annotations.In;
import cz.cuni.mff.d3s.deeco.annotations.InOut;
import cz.cuni.mff.d3s.deeco.annotations.PeriodicScheduling;
import cz.cuni.mff.d3s.deeco.annotations.Process;
import cz.cuni.mff.d3s.deeco.task.ParamHolder;
import cz.kinst.jakub.diploma.offloading.OffloadingConfig;
import cz.kinst.jakub.diploma.offloading.OffloadingManager;
import cz.kinst.jakub.diploma.offloading.deeco.model.BackendMonitorDef;
import cz.kinst.jakub.diploma.offloading.logger.Logger;

@Component
public class DeviceComponent implements Serializable {
    public String ip;
    public Map<String, Set<BackendMonitorDef>> monitorDefs = new HashMap<>(); // key is the app id
    public Set<String> spawnedMonitors = new HashSet<>();
    public Long lastPing;

    public DeviceComponent(String ip) {
        this.ip = ip;
    }

    @Process
    @PeriodicScheduling(period = OffloadingConfig.PING_INTERVAL_MS)
    public static void ping(@InOut("lastPing") ParamHolder<Long> lastPing) {
        lastPing.value = System.currentTimeMillis();
    }

    @Process
    @PeriodicScheduling(period = OffloadingConfig.IP_UPDATE_INTERVAL_MS)
    public static void updateDeviceIp(@InOut("ip") ParamHolder<String> deviceIp) {
        deviceIp.value = OffloadingManager.getInstance().getLocalIpAddress();
    }

    @Process
    @PeriodicScheduling(period = 6000)
    public static void manageMonitors(@In("monitorDefs") Map<String, Set<BackendMonitorDef>> monitorDefs, @InOut("spawnedMonitors") ParamHolder<Set<String>> spawnedMonitors, @In("ip") String ip) {
        //spawn monitors for each one of the monitorDefs
        for (String appId : monitorDefs.keySet()) {
            Set<BackendMonitorDef> appMonitorDefs = monitorDefs.get(appId);
            for (BackendMonitorDef monitorDef : appMonitorDefs) {
                if (!spawnedMonitors.value.contains(monitorDef.getBackendId())) {
                    Logger.i("New MonitorDef found. Going to spawn new Monitor component");
                    BackendMonitorComponent backendMonitorComponent = new BackendMonitorComponent(monitorDef.getBackendId(), ip);
                    OffloadingManager.getInstance().spawnNewMonitor(backendMonitorComponent);
                    spawnedMonitors.value.add(monitorDef.getBackendId());
                }
                //TODO: handle situation when monitorDefs are deleted
            }

        }

    }
}
