package cz.kinst.jakub.diploma.offloading;

import org.restlet.Client;
import org.restlet.Component;
import org.restlet.Context;
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

import cz.kinst.jakub.diploma.offloading.backend.OffloadableBackend;
import cz.kinst.jakub.diploma.offloading.backend.OffloadableBackendImpl;
import cz.kinst.jakub.diploma.offloading.deeco.DEECoManager;
import cz.kinst.jakub.diploma.offloading.deeco.components.BackendMonitorComponent;
import cz.kinst.jakub.diploma.offloading.deeco.components.DeviceComponent;
import cz.kinst.jakub.diploma.offloading.deeco.components.FrontendMonitorComponent;
import cz.kinst.jakub.diploma.offloading.deeco.components.PlannerComponent;
import cz.kinst.jakub.diploma.offloading.deeco.components.StateDataMonitorComponent;
import cz.kinst.jakub.diploma.offloading.deeco.ensembles.ActiveBackendMonitorToFrontendEnsemble;
import cz.kinst.jakub.diploma.offloading.deeco.ensembles.ActiveBackendMonitorToStateDataEnsemble;
import cz.kinst.jakub.diploma.offloading.deeco.ensembles.BackendStateDistributingEnsemble;
import cz.kinst.jakub.diploma.offloading.deeco.ensembles.NFPDataCollectingEnsemble;
import cz.kinst.jakub.diploma.offloading.deeco.ensembles.PlannerToDeviceEnsemble;
import cz.kinst.jakub.diploma.offloading.logger.Logger;
import cz.kinst.jakub.diploma.offloading.model.BackendMonitorDef;
import cz.kinst.jakub.diploma.offloading.model.NFPData;
import cz.kinst.jakub.diploma.offloading.utils.OffloadingConfig;
import cz.kinst.jakub.diploma.udpbroadcast.UDPBroadcast;

/**
 * Management class for offloading capabilities. This is central authority for deploying offloadable backend
 * components, storing their state data and connecting Application level with DEECo level.
 * Uses Restlet library to serve offloadable backends
 * <p/>
 * <p/>
 * ---------------------------
 * Created by Jakub Kinst 2015
 * E-mail: jakub@kinst.cz
 */
public class OffloadingManager {
	/**
	 * Runtime mode with deployed Frontend and BackendStateData components.
	 * Generally used for applications with user interface - client.
	 */
	public static final int MODE_WITH_FRONTEND = 0;

	/**
	 * Runtime mode without frontend capabilities.
	 * Generaly used when deploying backend only.
	 */
	public static final int MODE_ONLY_BACKEND = 1;

	/**
	 * Static instance of this class
	 */
	private static OffloadingManager sInstance;

	/**
	 * Restlet server component
	 */
	private final Component mServerComponent;

	/**
	 * Restlet routing manager
	 */
	private final Router mRouter;

	/**
	 * DEECo manager for communicating with DEECo layer - spawning DEECo components,
	 * defining new ensembles or controlling runtime (start/stop)
	 */
	private final DEECoManager mDeecoManager;

	/**
	 * Instance of {@link cz.kinst.jakub.diploma.udpbroadcast.UDPBroadcast} class.
	 * Instance is platform-specific (Android/Java)
	 */
	private final UDPBroadcast mUdpBroadcast;

	/**
	 * Application ID
	 */
	private final String mAppId;

	/**
	 * List of deployed oflloadable backends
	 */
	private List<OffloadableBackendImpl> mBackends = new ArrayList<>();

	/**
	 * List of backend states. Each belongs to one backend from {@link #mBackends}
	 */
	private List<BackendStateData> mBackendStateDataCollection = new ArrayList<>();


	private OffloadingManager(UDPBroadcast udpBroadcast, String appId) {
		// initialize local server for serving backends via HTTP
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


	/**
	 * App-wide provider for {@link cz.kinst.jakub.diploma.offloading.OffloadingManager} singleton instance.
	 * Before using this, the singleton instance must be created using {@link #createInstance(cz.kinst.jakub.diploma.udpbroadcast.UDPBroadcast, String)}
	 *
	 * @return Singleton instance
	 */
	public static OffloadingManager getInstance() {
		return sInstance;
	}


	/**
	 * App wide method used to create singleton instance of {@link cz.kinst.jakub.diploma.offloading.OffloadingManager}
	 *
	 * @param udpBroadcast {@link cz.kinst.jakub.diploma.udpbroadcast.UDPBroadcast} implementation (platform-specific)
	 * @param appId        Unique application ID
	 * @return
	 */
	public static OffloadingManager createInstance(UDPBroadcast udpBroadcast, String appId) {
		sInstance = new OffloadingManager(udpBroadcast, appId);
		return sInstance;
	}


	private static String getUrl(String host, String backendId) {
		return "http://" + host + ":" + OffloadingConfig.HTTP_PORT_FOR_BACKENDS + backendId;
	}


	/**
	 * Attaches a new Backend instance to the HTTP server and registers it for later DEECo initialization
	 *
	 * @param backend          Backend implementation
	 * @param backendInterface Backend interface
	 */
	public void attachBackend(OffloadableBackendImpl backend, Class<? extends OffloadableBackend> backendInterface) {
		mBackends.add(backend);
		mRouter.attach(backend.getBackendId(), backend.getClass());
		BackendStateData backendStateData = new BackendStateData(backend.getBackendId(), backendInterface, this);
		mBackendStateDataCollection.add(backendStateData);
	}


	/**
	 * Initializes DEECo according to runtimeMode and registered backends.
	 * Spawns DEECo components and registers needed ensembles
	 *
	 * @param runtimeMode Runtime Mode (either {@link #MODE_ONLY_BACKEND} or {@link #MODE_WITH_FRONTEND})
	 */
	public void init(int runtimeMode) {
		// register DEECo components and ensembles
		HashSet<BackendMonitorDef> monitorDefs = new HashSet<>();
		for (OffloadableBackendImpl backend : mBackends) {
			String backendId = backend.getBackendId();
			BackendMonitorDef monitorDef = new BackendMonitorDef(backendId);
			monitorDefs.add(monitorDef);
		}
		if (runtimeMode == MODE_WITH_FRONTEND) {
			PlannerComponent plannerComponent = new PlannerComponent(mAppId, monitorDefs, getLocalIpAddress());
			FrontendMonitorComponent frontendMonitorComponent = new FrontendMonitorComponent();
			mDeecoManager.registerComponent(plannerComponent);
			mDeecoManager.registerComponent(frontendMonitorComponent);
			for (BackendStateData backendStateData : mBackendStateDataCollection) {
				mDeecoManager.registerComponent(new StateDataMonitorComponent(backendStateData.getBackendId()));
			}
		}
		DeviceComponent deviceComponent = new DeviceComponent(getLocalIpAddress());
		mDeecoManager.registerComponent(deviceComponent);

		mDeecoManager.registerEnsemble(PlannerToDeviceEnsemble.class);
		mDeecoManager.registerEnsemble(NFPDataCollectingEnsemble.class);
		mDeecoManager.registerEnsemble(BackendStateDistributingEnsemble.class);
		mDeecoManager.registerEnsemble(ActiveBackendMonitorToFrontendEnsemble.class);
		mDeecoManager.registerEnsemble(ActiveBackendMonitorToStateDataEnsemble.class);

		mDeecoManager.initRuntime();
	}


	/**
	 * Starts DEECo runtime and Backend serving
	 *
	 * @throws Exception
	 */
	public void start() throws Exception {
		mDeecoManager.startRuntime();
		mServerComponent.start();
	}


	/**
	 * Stops DEECo runtime and Backend serving
	 *
	 * @throws Exception
	 */
	public void stop() throws Exception {
		mDeecoManager.stopRuntime();
		mServerComponent.stop();
	}


	/**
	 * Used to spawn new BackendMonitor at DEECo level. This is usually called from DEECo runtime
	 * ({@link cz.kinst.jakub.diploma.offloading.deeco.components.DeviceComponent} spawning new Backend monitors)
	 *
	 * @param backendMonitorComponent Backend Monitor component instance
	 */
	public void spawnNewMonitor(BackendMonitorComponent backendMonitorComponent) {
		mDeecoManager.registerComponent(backendMonitorComponent);
	}


	/**
	 * Returns Restlet proxy for remote/local backend which can be used as normal Java class to retrieve/send any
	 * data from/to any backend.
	 *
	 * @param backendInterface Interface of requested backend (backend of this type must have been attached before via {@link #attachBackend(cz.kinst.jakub.diploma.offloading.backend.OffloadableBackendImpl, Class)})
	 * @param host             IP address of the remote/local host
	 * @param <T>
	 * @return Backend proxy according to specified interface
	 */
	public <T> T getBackendProxy(Class<T> backendInterface, String host) {
		for (OffloadableBackendImpl res : mBackends) {
			if (backendInterface.isAssignableFrom(res.getClass())) {
				ClientResource cr = new ClientResource(getUrl(host, res.getBackendId()));
				Context context = new Context();
				context.getParameters().add("socketConnectTimeoutMs", "3000");
				Client client = new Client(context, Protocol.HTTP);
				cr.setNext(client);
				return cr.wrap(backendInterface);
			}
		}
		throw new IllegalArgumentException("No Backend implementing " + backendInterface.getName() + " was registered.");
	}


	/**
	 * Returns backend ID according to backend interface
	 *
	 * @param backendInterface
	 * @return Backend ID
	 */
	public String getBackendId(Class backendInterface) {
		for (OffloadableBackendImpl res : mBackends) {
			if (backendInterface.isAssignableFrom(res.getClass())) {
				return res.getBackendId();
			}
		}
		throw new IllegalArgumentException("No Backend implementing " + backendInterface.getName() + " was registered.");
	}


	/**
	 * Provides local host's IP address. Using platform-specific {@link cz.kinst.jakub.diploma.udpbroadcast.UDPBroadcast} implementation.
	 *
	 * @return
	 */
	public String getLocalIpAddress() {
		return mUdpBroadcast.getMyIpAddress();
	}


	/**
	 * Measure Backend performance of specified backend at local node
	 *
	 * @param backendId
	 * @return NFPData - result of performance measurement
	 */
	public NFPData measureBackendPerformance(String backendId) {
		for (OffloadableBackendImpl backend : mBackends) {
			if (backend.getBackendId().equals(backendId)) {
				Logger.i("Performing measureBackendPerformance on " + backendId);
				NFPData nfpData = backend.checkPerformance();
				return nfpData;
			}
		}
		throw new IllegalArgumentException("No Backend on path " + backendId + " was registered.");
	}


	/**
	 * Picks best alternative out of offered pairs of host and {@link cz.kinst.jakub.diploma.offloading.model.NFPData} using implementation of
	 * {@link cz.kinst.jakub.diploma.offloading.backend.OffloadableBackendImpl#findOptimalAlternative(java.util.Map)} method
	 *
	 * @param backendId    Backend ID
	 * @param alternatives Alternatives
	 * @return
	 */
	public String findOptimalAlternative(String backendId, Map<String, NFPData> alternatives) {
		for (OffloadableBackendImpl backend : mBackends) {
			if (backend.getBackendId().equals(backendId)) {
				Logger.i("Performing findOptimalAlternative on " + backendId);
				String optimum = backend.findOptimalAlternative(alternatives);
				return optimum;
			}
		}
		throw new IllegalArgumentException("No Backend on path " + backendId + " was registered.");
	}


	/**
	 * Provides backend state data instance for accessing state data
	 *
	 * @param backendId Backend ID
	 * @return State data
	 */
	public BackendStateData getBackendStateData(String backendId) {
		for (BackendStateData backendStateData : mBackendStateDataCollection) {
			if (backendStateData.getBackendId().equals(backendId))
				return backendStateData;
		}
		throw new IllegalArgumentException("No BackendStateData of id " + backendId + " was registered.");
	}


	/**
	 * Performs state data moving from one backend to another to preserve state consistency
	 *
	 * @param backendId         Backend ID
	 * @param oldBackendAddress Old backend address
	 * @param newBackendAddress New backend address
	 */
	public void moveBackendStateData(String backendId, String oldBackendAddress, String newBackendAddress) {
		getBackendStateData(backendId).moveData(oldBackendAddress, newBackendAddress);
	}
}
