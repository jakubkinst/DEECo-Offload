package cz.kinst.jakub.diploma.deecooffload.deeco.components;

import java.util.Map;
import java.util.Set;

import cz.cuni.mff.d3s.deeco.annotations.Component;
import cz.cuni.mff.d3s.deeco.annotations.In;
import cz.cuni.mff.d3s.deeco.annotations.PeriodicScheduling;
import cz.cuni.mff.d3s.deeco.annotations.Process;
import cz.kinst.jakub.diploma.deecooffload.deeco.model.MonitorDef;

@Component
public class Device {
    public String ip;
    public Map<String, Set<MonitorDef>> monitorDefs; // key is the app id

    public Device(String ip) {
        this.ip = ip;
    }

    @Process
    @PeriodicScheduling(period = 500)
    public static void manageMonitors(@In("monitorDefs") Map<String, Set<MonitorDef>> monitorDefs) {
        //TODO: spawn monitors for each one of the monitorDefs
    }
}
