package cz.kinst.jakub.offloading.deeco.components;

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
import cz.kinst.jakub.offloading.BusProvider;
import cz.kinst.jakub.offloading.deeco.events.SpawnMonitorComponentEvent;
import cz.kinst.jakub.offloading.deeco.model.MonitorDef;
import cz.kinst.jakub.offloading.logger.Logger;

@Component
public class DeviceComponent implements Serializable{
    public String ip;
    public Map<String, Set<MonitorDef>> monitorDefs = new HashMap<>(); // key is the app id
    public Set<String> spawnedMonitors = new HashSet<>();

    public DeviceComponent(String ip) {
        this.ip = ip;
    }

    @Process
    @PeriodicScheduling(period = 6000)
    public static void manageMonitors(@In("monitorDefs") Map<String, Set<MonitorDef>> monitorDefs, @InOut("spawnedMonitors") ParamHolder<Set<String>> spawnedMonitors, @In("ip") String ip) {
        //spawn monitors for each one of the monitorDefs
        for (String appId : monitorDefs.keySet()) {
            Set<MonitorDef> appMonitorDefs = monitorDefs.get(appId);
            for (MonitorDef monitorDef : appMonitorDefs) {
                if (!spawnedMonitors.value.contains(monitorDef.getResourceId())) {
                    Logger.i("New MonitorDef found. Going to spawn new Monitor component");
                    MonitorComponent monitorComponent = new MonitorComponent(monitorDef.getResourceId(), ip);
                    BusProvider.get().post(new SpawnMonitorComponentEvent(monitorComponent));
                    spawnedMonitors.value.add(monitorDef.getResourceId());
                }
                //TODO: handle situation when monitorDefs are deleted
            }

        }

    }
}
