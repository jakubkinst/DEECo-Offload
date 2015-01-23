package cz.kinst.jakub.offloading.deeco.components;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Set;

import cz.cuni.mff.d3s.deeco.annotations.Component;
import cz.cuni.mff.d3s.deeco.annotations.In;
import cz.cuni.mff.d3s.deeco.annotations.PeriodicScheduling;
import cz.cuni.mff.d3s.deeco.annotations.Process;
import cz.kinst.jakub.offloading.BusProvider;
import cz.kinst.jakub.offloading.OffloadingManager;
import cz.kinst.jakub.offloading.deeco.events.PlanUpdateEvent;
import cz.kinst.jakub.offloading.deeco.model.DeploymentPlan;
import cz.kinst.jakub.offloading.deeco.model.MonitorDef;
import cz.kinst.jakub.offloading.deeco.model.NFPData;
import cz.kinst.jakub.offloading.deeco.model.NfpDataHolder;
import cz.kinst.jakub.offloading.logger.Logger;


@Component
public class PlannerComponent implements Serializable {

    public String appId;
    public Set<MonitorDef> monitorDefs;
    public NfpDataHolder nfpDataHolder;

    public PlannerComponent(String appId, Set<MonitorDef> monitorDefs) {
        this.appId = appId;
        this.monitorDefs = monitorDefs;
    }

    @Process
    @PeriodicScheduling(period = 1000)
    public static void plan(@In("nfpDataHolder") NfpDataHolder nfpDataHolder) {
        Logger.i("Planner: plan");
        DeploymentPlan newPlan = new DeploymentPlan();
        for (String appComponentId : nfpDataHolder.getAppComponentIds()) {
            HashMap<String, NFPData> alternatives = nfpDataHolder.getByAppComponentId(appComponentId);
            String selectedDeviceIp = OffloadingManager.getInstance().findOptimalAlternative(appComponentId, alternatives);
            Logger.i("Found best alternative at " + selectedDeviceIp);
            newPlan.plan(appComponentId, selectedDeviceIp);

        }
        BusProvider.get().post(new PlanUpdateEvent(newPlan)); //TODO: maybe broadcast only after change
    }
}

