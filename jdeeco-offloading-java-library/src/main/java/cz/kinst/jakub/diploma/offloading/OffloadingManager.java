package cz.kinst.jakub.diploma.offloading;

import org.restlet.Component;
import org.restlet.data.Protocol;
import org.restlet.engine.Engine;
import org.restlet.engine.connector.HttpClientHelper;
import org.restlet.ext.gson.GsonConverter;
import org.restlet.ext.simple.HttpServerHelper;
import org.restlet.resource.ClientResource;
import org.restlet.routing.Router;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import cz.kinst.jakub.diploma.offloading.deeco.DEECoManager;
import cz.kinst.jakub.diploma.offloading.deeco.components.DeviceComponent;
import cz.kinst.jakub.diploma.offloading.deeco.components.MonitorComponent;
import cz.kinst.jakub.diploma.offloading.deeco.components.PlannerComponent;
import cz.kinst.jakub.diploma.offloading.deeco.ensembles.PlannerToDeviceEnsemble;
import cz.kinst.jakub.diploma.offloading.deeco.ensembles.PlannerToMonitorEnsemble;
import cz.kinst.jakub.diploma.offloading.deeco.events.PlanUpdateEvent;
import cz.kinst.jakub.diploma.offloading.deeco.events.SpawnMonitorComponentEvent;
import cz.kinst.jakub.diploma.offloading.deeco.model.DeploymentPlan;
import cz.kinst.jakub.diploma.offloading.deeco.model.MonitorDef;
import cz.kinst.jakub.diploma.offloading.deeco.model.NFPData;
import cz.kinst.jakub.diploma.offloading.logger.Logger;
import cz.kinst.jakub.diploma.offloading.resource.OffloadingResourceImpl;
import cz.kinst.jakub.diploma.udpbroadcast.UDPBroadcast;

/**
 * Created by jakubkinst on 09/01/15.
 */
public class OffloadingManager {
    private static OffloadingManager sInstance;

    private final Component mServerComponent;
    private final Router mRouter;
    private final DEECoManager mDeecoManager;
    private final UDPBroadcast mUdpBroadcast;
    private final String mAppId;
    private List<OffloadingResourceImpl> mResources = new ArrayList<>();
    private DeploymentPlan mDeploymentPlan;
    private OnDeploymentPlanUpdatedListener mDeploymentPlanUpdatedListener;

    public static OffloadingManager getInstance() {
        return sInstance;
    }

    public static OffloadingManager create(UDPBroadcast udpBroadcast, String appId) {
        sInstance = new OffloadingManager(udpBroadcast, appId);
        return sInstance;
    }

    private OffloadingManager(UDPBroadcast udpBroadcast, String appId) {
        BusProvider.get().register(this);
        // init local server for serving resources
        Engine.getInstance().getRegisteredClients().clear();
        Engine.getInstance().getRegisteredClients().add(new HttpClientHelper(null));
        Engine.getInstance().getRegisteredConverters().add(new GsonConverter());
        Engine.getInstance().getRegisteredServers().clear();
        Engine.getInstance().getRegisteredServers().add(new HttpServerHelper(null)); // Simple Server Connector

        mAppId = appId;


        mServerComponent = new Component();
        mServerComponent.getServers().add(Protocol.HTTP, Config.HTTP_PORT_FOR_RESOURCES);
        mRouter = new Router(mServerComponent.getContext().createChildContext());
        mServerComponent.getDefaultHost().attach(mRouter);
        // init DEECo infrastructure for cz.kinst.jakub.cz.kinst.jakub.cz.kinst.jakub.diploma.cz.kinst.jakub.diploma.cz.kinst.jakub.diploma.offloading
        mUdpBroadcast = udpBroadcast;
        mDeecoManager = new DEECoManager(mUdpBroadcast);
    }

    public void attachResource(OffloadingResourceImpl resource) {
        mResources.add(resource);
        mRouter.attach(resource.getPath(), resource.getClass());
    }

    public void start() throws Exception {
        // register DEECo components and ensembles
        HashSet<MonitorDef> monitorDefs = new HashSet<>();
        for (OffloadingResourceImpl mResource : mResources) {
            String resourceId = mResource.getPath();
            MonitorDef monitorDef = new MonitorDef(resourceId);
            monitorDefs.add(monitorDef);
        }
        PlannerComponent plannerComponent = new PlannerComponent(mAppId, monitorDefs);
        DeviceComponent deviceComponent = new DeviceComponent(getLocalIpAddress());
        mDeecoManager.registerComponent(plannerComponent);
        mDeecoManager.registerComponent(deviceComponent);
        mDeecoManager.registerEnsemble(PlannerToDeviceEnsemble.class);
        mDeecoManager.registerEnsemble(PlannerToMonitorEnsemble.class);

        mDeecoManager.initRuntime();
        mDeecoManager.startRuntime();
        mServerComponent.start();
    }

    public void stop() throws Exception {
        mDeecoManager.stopRuntime();
        mServerComponent.stop();
    }

    public void onEvent(SpawnMonitorComponentEvent event) {
        MonitorComponent monitorComponent = event.getMonitorComponent();
        mDeecoManager.registerComponent(monitorComponent);
        //TODO: tell runtime about new component if needed (waiting for confirmation 24/01/2015)
    }

    public void onEventMainThread(PlanUpdateEvent event) {
        mDeploymentPlan = event.getPlan();
        if (mDeploymentPlanUpdatedListener != null)
            mDeploymentPlanUpdatedListener.onDeploymentPlanUpdated(event.getPlan());
    }

    public <T> T getResourceProxy(Class<T> resourceInterface, String host) {
        for (OffloadingResourceImpl res : mResources) {
            if (resourceInterface.isAssignableFrom(res.getClass())) {
                ClientResource cr = new ClientResource(getUrl(host, res.getPath()));
                return cr.wrap(resourceInterface);
            }
        }
        throw new IllegalArgumentException("No Resource of implementing " + resourceInterface.getName() + " was registered.");
    }

    public <T> T getCurrentResourceProxy(Class<T> resourceInterface) {
        for (OffloadingResourceImpl res : mResources) {
            if (resourceInterface.isAssignableFrom(res.getClass())) {
                String plannedHost = mDeploymentPlan.getPlan(res.getPath());
                return getResourceProxy(resourceInterface, plannedHost);
            }
        }
        throw new IllegalArgumentException("No Resource of implementing " + resourceInterface.getName() + " was registered.");
    }

    private static String getUrl(String host, String resourcePath) {
        return "http://" + host + ":" + Config.HTTP_PORT_FOR_RESOURCES + resourcePath;
    }

    public String getLocalIpAddress() {
        return mUdpBroadcast.getMyIpAddress();
    }

    public NFPData checkPerformance(String resourcePath) {
        for (OffloadingResourceImpl resource : mResources) {
            if (resource.getPath().equals(resourcePath)) {
                Logger.i("Performing checkPerformance on " + resourcePath);
                NFPData nfpData = resource.checkPerformance();
                return nfpData;
            }
        }
        throw new IllegalArgumentException("No Resource on path " + resourcePath + " was registered.");
    }

    public String findOptimalAlternative(String resourcePath, Map<String, NFPData> alternatives) {
        for (OffloadingResourceImpl resource : mResources) {
            if (resource.getPath().equals(resourcePath)) {
                Logger.i("Performing findOptimalAlternative on " + resourcePath);
                String optimum = resource.findOptimalAlternative(alternatives);
                return optimum;
            }
        }
        throw new IllegalArgumentException("No Resource on path " + resourcePath + " was registered.");
    }

    public void setDeploymentPlanUpdatedListener(OnDeploymentPlanUpdatedListener mDeploymentPlanUpdatedListener) {
        this.mDeploymentPlanUpdatedListener = mDeploymentPlanUpdatedListener;
    }
}
