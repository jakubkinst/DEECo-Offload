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
import cz.kinst.jakub.diploma.offloading.deeco.components.BackendMonitorComponent;
import cz.kinst.jakub.diploma.offloading.deeco.components.DeviceComponent;
import cz.kinst.jakub.diploma.offloading.deeco.components.FrontendMonitorComponent;
import cz.kinst.jakub.diploma.offloading.deeco.components.PlannerComponent;
import cz.kinst.jakub.diploma.offloading.deeco.ensembles.ActiveBackendMonitorToUiEnsemble;
import cz.kinst.jakub.diploma.offloading.deeco.ensembles.BackendStateDistributingEnsemble;
import cz.kinst.jakub.diploma.offloading.deeco.ensembles.NFPDataCollectingEnsemble;
import cz.kinst.jakub.diploma.offloading.deeco.ensembles.PlannerToDeviceEnsemble;
import cz.kinst.jakub.diploma.offloading.deeco.model.BackendMonitorDef;
import cz.kinst.jakub.diploma.offloading.deeco.model.NFPData;
import cz.kinst.jakub.diploma.offloading.logger.Logger;
import cz.kinst.jakub.diploma.offloading.resource.OffloadableBackendImpl;
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
    private List<OffloadableBackendImpl> mBackends = new ArrayList<>();

    public static OffloadingManager getInstance() {
        return sInstance;
    }

    public static OffloadingManager create(UDPBroadcast udpBroadcast, String appId) {
        sInstance = new OffloadingManager(udpBroadcast, appId);
        return sInstance;
    }

    private OffloadingManager(UDPBroadcast udpBroadcast, String appId) {
        // init local server for serving backends
        Engine.getInstance().getRegisteredClients().clear();
        Engine.getInstance().getRegisteredClients().add(new HttpClientHelper(null));
        Engine.getInstance().getRegisteredConverters().add(new GsonConverter());
        Engine.getInstance().getRegisteredServers().clear();
        Engine.getInstance().getRegisteredServers().add(new HttpServerHelper(null)); // Simple Server Connector

        mAppId = appId;


        mServerComponent = new Component();
        mServerComponent.getServers().add(Protocol.HTTP, OffloadingConfig.HTTP_PORT_FOR_BACKENDS);
        mRouter = new Router(mServerComponent.getContext().createChildContext());
        mServerComponent.getDefaultHost().attach(mRouter);
        // init DEECo infrastructure for offloading
        mUdpBroadcast = udpBroadcast;
        mDeecoManager = new DEECoManager(mUdpBroadcast);
    }

    public void attachBackend(OffloadableBackendImpl backend) {
        mBackends.add(backend);
        mRouter.attach(backend.getPath(), backend.getClass());
    }

    public void init() {
        // register DEECo components and ensembles
        HashSet<BackendMonitorDef> monitorDefs = new HashSet<>();
        for (OffloadableBackendImpl backend : mBackends) {
            String backendId = backend.getPath();
            BackendMonitorDef monitorDef = new BackendMonitorDef(backendId);
            monitorDefs.add(monitorDef);
        }
        PlannerComponent plannerComponent = new PlannerComponent(mAppId, monitorDefs, getLocalIpAddress());
        DeviceComponent deviceComponent = new DeviceComponent(getLocalIpAddress());
        FrontendMonitorComponent frontendMonitorComponent = new FrontendMonitorComponent();
        mDeecoManager.registerComponent(plannerComponent);
        mDeecoManager.registerComponent(deviceComponent);
        mDeecoManager.registerComponent(frontendMonitorComponent);
        mDeecoManager.registerEnsemble(PlannerToDeviceEnsemble.class);
        mDeecoManager.registerEnsemble(NFPDataCollectingEnsemble.class);
        mDeecoManager.registerEnsemble(BackendStateDistributingEnsemble.class);
        mDeecoManager.registerEnsemble(ActiveBackendMonitorToUiEnsemble.class);

        mDeecoManager.initRuntime();
    }

    public void start() throws Exception {
        mDeecoManager.startRuntime();
        mServerComponent.start();
    }

    public void stop() throws Exception {
        mDeecoManager.stopRuntime();
        mServerComponent.stop();
    }

    public void spawnNewMonitor(BackendMonitorComponent backendMonitorComponent) {
        mDeecoManager.registerComponent(backendMonitorComponent);
    }

    public <T> T getBackendProxy(Class<T> backendInterface, String host) {
        for (OffloadableBackendImpl res : mBackends) {
            if (backendInterface.isAssignableFrom(res.getClass())) {
                ClientResource cr = new ClientResource(getUrl(host, res.getPath()));
                return cr.wrap(backendInterface);
            }
        }
        throw new IllegalArgumentException("No Backend implementing " + backendInterface.getName() + " was registered.");
    }

    public String getBackendId(Class backendInterface) {
        for (OffloadableBackendImpl res : mBackends) {
            if (backendInterface.isAssignableFrom(res.getClass())) {
                return res.getPath();
            }
        }
        throw new IllegalArgumentException("No Backend implementing " + backendInterface.getName() + " was registered.");
    }

    private static String getUrl(String host, String backendId) {
        return "http://" + host + ":" + OffloadingConfig.HTTP_PORT_FOR_BACKENDS + backendId;
    }

    public String getLocalIpAddress() {
        return mUdpBroadcast.getMyIpAddress();
    }

    public NFPData checkBackendPerformance(String backendId) {
        for (OffloadableBackendImpl backend : mBackends) {
            if (backend.getPath().equals(backendId)) {
                Logger.i("Performing checkBackendPerformance on " + backendId);
                NFPData nfpData = backend.checkPerformance();
                return nfpData;
            }
        }
        throw new IllegalArgumentException("No Backend on path " + backendId + " was registered.");
    }

    public String findOptimalAlternative(String backendPath, Map<String, NFPData> alternatives) {
        for (OffloadableBackendImpl backend : mBackends) {
            if (backend.getPath().equals(backendPath)) {
                Logger.i("Performing findOptimalAlternative on " + backendPath);
                String optimum = backend.findOptimalAlternative(alternatives);
                return optimum;
            }
        }
        throw new IllegalArgumentException("No Backend on path " + backendPath + " was registered.");
    }

}
