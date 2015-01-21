package cz.kinst.jakub.diploma.deecooffload.deeco.components;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Set;

import cz.cuni.mff.d3s.deeco.annotations.Component;
import cz.cuni.mff.d3s.deeco.annotations.In;
import cz.cuni.mff.d3s.deeco.annotations.PeriodicScheduling;
import cz.cuni.mff.d3s.deeco.annotations.Process;
import cz.kinst.jakub.diploma.deecooffload.deeco.events.PlanUpdateEvent;
import cz.kinst.jakub.diploma.deecooffload.deeco.model.DeploymentPlan;
import cz.kinst.jakub.diploma.deecooffload.deeco.model.MonitorDef;
import cz.kinst.jakub.diploma.deecooffload.deeco.model.NFPData;
import cz.kinst.jakub.diploma.deecooffload.deeco.model.NfpDataHolder;
import cz.kinst.jakub.offloading.BusProvider;
import cz.kinst.jakub.offloading.CommonLog;


@Component
public class Planner implements Serializable {

    public String appId;
    public Set<MonitorDef> monitorDefs;
    public NfpDataHolder nfpDataHolder;

    public Planner(String appId, Set<MonitorDef> monitorDefs) {
        this.appId = appId;
        this.monitorDefs = monitorDefs;
    }

    @Process
    @PeriodicScheduling(period = 1000)
    public static void plan(@In("nfpDataHolder") NfpDataHolder nfpDataHolder) {
        CommonLog.log("Planner: plan");
        //TODO: plan according to nfpDataMap
        DeploymentPlan newPlan = new DeploymentPlan();
        for (String appComponentId : nfpDataHolder.getAppComponentIds()) {
            String selectedDeviceIp = null;
            HashMap<String, NFPData> appComponentAlternatives = nfpDataHolder.getByAppComponentId(appComponentId);
            for (String deviceIp : appComponentAlternatives.keySet()) {
                NFPData nfpData = (NFPData) appComponentAlternatives.get(deviceIp);
                //TODO: select the best NFPData
                selectedDeviceIp = deviceIp;
            }
            newPlan.plan(appComponentId, selectedDeviceIp);

        }
        BusProvider.get().post(new PlanUpdateEvent(newPlan)); //TODO: maybe broadcast only after change
    }
}

