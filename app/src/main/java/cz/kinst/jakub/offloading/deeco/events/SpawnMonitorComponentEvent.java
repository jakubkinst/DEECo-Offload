package cz.kinst.jakub.offloading.deeco.events;

import cz.kinst.jakub.offloading.deeco.components.MonitorComponent;

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
