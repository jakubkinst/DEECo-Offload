package cz.kinst.jakub.offloading.deeco.components;

import cz.cuni.mff.d3s.deeco.annotations.Component;
import cz.cuni.mff.d3s.deeco.annotations.In;
import cz.cuni.mff.d3s.deeco.annotations.InOut;
import cz.cuni.mff.d3s.deeco.annotations.PeriodicScheduling;
import cz.cuni.mff.d3s.deeco.annotations.Process;
import cz.cuni.mff.d3s.deeco.task.ParamHolder;
import cz.kinst.jakub.offloading.OffloadingManager;
import cz.kinst.jakub.offloading.deeco.model.NFPData;

@Component
public class MonitorComponent {
    public String appComponentId;
    public String deviceIp;
    public String resourceId;
    public NFPData nfpData;

    public MonitorComponent(String resourceId, String deviceIp) {
        this.resourceId = resourceId;
        this.deviceIp = deviceIp;
    }

    @Process
    @PeriodicScheduling(period = 1000)
    public static void measure(@In("deviceIp") String deviceIp, @In("resourceId") String resourceId, @InOut("nfpData") ParamHolder<NFPData> nfpData) {
        //measure and produce NFPData based on "simulation"
        nfpData.value = OffloadingManager.getInstance().checkPerformance(resourceId, deviceIp);
    }
}
