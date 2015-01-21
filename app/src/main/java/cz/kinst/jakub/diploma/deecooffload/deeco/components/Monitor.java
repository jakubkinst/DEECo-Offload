package cz.kinst.jakub.diploma.deecooffload.deeco.components;

import cz.cuni.mff.d3s.deeco.annotations.Component;
import cz.cuni.mff.d3s.deeco.annotations.In;
import cz.cuni.mff.d3s.deeco.annotations.InOut;
import cz.cuni.mff.d3s.deeco.annotations.PeriodicScheduling;
import cz.cuni.mff.d3s.deeco.annotations.Process;
import cz.cuni.mff.d3s.deeco.task.ParamHolder;
import cz.kinst.jakub.diploma.deecooffload.deeco.model.NFPData;
import cz.kinst.jakub.offloading.OffloadingResource;

@Component
public class Monitor {
    public String appComponentId;
    public String deviceIp;
    public OffloadingResource offloadableAppComponent;
    public NFPData nfpData;

    public Monitor(OffloadingResource offloadableAppComponent, String deviceIp) {
        this.offloadableAppComponent = offloadableAppComponent;
        this.deviceIp = deviceIp;
    }

    @Process
    @PeriodicScheduling(period = 1000)
    public static void measure(@In("deviceIp") String deviceIp, @InOut("nfpData") ParamHolder<NFPData> nfpData) {
        //TODO: measure and produce NFPData based on "simulation"
        // run "simulation" on deviceIp and create NFPData
    }
}
