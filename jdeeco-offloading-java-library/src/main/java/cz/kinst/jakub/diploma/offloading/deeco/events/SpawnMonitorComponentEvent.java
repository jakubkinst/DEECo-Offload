package cz.kinst.jakub.diploma.offloading.deeco.events;

import cz.kinst.jakub.diploma.offloading.deeco.components.MonitorComponent;

/**
 * Created by jakubkinst on 23/01/15.
 */
public class SpawnMonitorComponentEvent {
    private final MonitorComponent monitorComponent;

    public SpawnMonitorComponentEvent(MonitorComponent monitorComponent) {
        this.monitorComponent = monitorComponent;
    }

    public MonitorComponent getMonitorComponent() {
        return monitorComponent;
    }
}
