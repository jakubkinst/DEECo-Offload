package cz.kinst.jakub.diploma.offloading.deeco;

import cz.cuni.mff.d3s.deeco.annotations.processor.AnnotationProcessor;
import cz.cuni.mff.d3s.deeco.annotations.processor.AnnotationProcessorException;
import cz.cuni.mff.d3s.deeco.knowledge.CloningKnowledgeManagerFactory;
import cz.cuni.mff.d3s.deeco.logging.Log;
import cz.cuni.mff.d3s.deeco.model.runtime.api.RuntimeMetadata;
import cz.cuni.mff.d3s.deeco.model.runtime.custom.RuntimeMetadataFactoryExt;
import cz.cuni.mff.d3s.deeco.runtime.RuntimeFramework;
import cz.kinst.jakub.diploma.offloading.logger.Logger;
import cz.kinst.jakub.diploma.offloading.utils.OffloadingConfig;
import cz.kinst.jakub.diploma.udpbroadcast.UDPBroadcast;
import cz.kinst.jakub.diploma.udpbroadcast.UDPBroadcastRuntimeBuilder;

/**
 * Management class used to work with DEECo runtime (init/start/stop). It creates UDP broadcast-enabled
 * JDEECo runtime
 * <p/>
 * ---------------------------
 * Created by Jakub Kinst 2015
 * E-mail: jakub@kinst.cz
 */
public class DEECoManager {

	/**
	 * JDEECo runtime builder instance
	 */
	private final UDPBroadcastRuntimeBuilder mBuilder;

	/**
	 * JDEECo model instance
	 */
	private final RuntimeMetadata mModel;

	/**
	 * {@link cz.kinst.jakub.diploma.udpbroadcast.UDPBroadcast} platform-specific instance
	 */
	private UDPBroadcast mUdpBroadcast;

	/**
	 * JDEECo runtime instance
	 */
	private RuntimeFramework mDEECoRuntime;

	/**
	 * JDEECo instance running flag
	 */
	private boolean mRunning;

	/**
	 * Annotation processor used to process components and ensembles
	 */
	private AnnotationProcessor mProcessor;


	/**
	 * @param udpBroadcast platform-specific {@link cz.kinst.jakub.diploma.udpbroadcast.UDPBroadcast} implementation
	 */
	public DEECoManager(UDPBroadcast udpBroadcast) {
		Log.i("Setting logger level acording to OffloadingConfig"); // this is called to initialize StandardLogger (workaround)
		java.util.logging.Logger.getLogger("default").setLevel(OffloadingConfig.JDEECO_LOGGING_LEVEL);

		this.mUdpBroadcast = udpBroadcast;

		mBuilder = new UDPBroadcastRuntimeBuilder();
		mModel = RuntimeMetadataFactoryExt.eINSTANCE.createRuntimeMetadata();
		mProcessor = new AnnotationProcessor(RuntimeMetadataFactoryExt.eINSTANCE, mModel, new CloningKnowledgeManagerFactory());
	}


	/**
	 * Static method which checks last pings provided in parameters for their age and determines
	 * if all of them are still alive (not older than a threshold)
	 *
	 * @param componentLastPings last ping timestamps
	 * @return true if none of the timestamp is older than threshold
	 */
	public static boolean areComponentsStillAlive(long... componentLastPings) {
		long now = System.currentTimeMillis();
		for (long lastPing : componentLastPings) {
			long monitorAge = now - lastPing;
			if (monitorAge > OffloadingConfig.PING_INTERVAL_MS * 6)
				return false;
		}
		return true;
	}


	/**
	 * Initialize JDEECo runtime
	 */
	public void initRuntime() {
		try {
			Logger.i("DEECo Runtime initialized at " + mUdpBroadcast.getMyIpAddress());
			mDEECoRuntime = mBuilder.build(mUdpBroadcast.getMyIpAddress(), mModel, mUdpBroadcast);
		} catch (Exception e) {
			e.printStackTrace();
			Logger.e(e.getMessage());
		}
	}


	/**
	 * Register new JDEECo component
	 *
	 * @param component instance of component (must be annotated with {@link cz.cuni.mff.d3s.deeco.annotations.Component} annotation)
	 */
	public void registerComponent(Object component) {
		try {
			mProcessor.process(component);
			Logger.i("DEECo component " + component.getClass().getName() + " registered.");
		} catch (AnnotationProcessorException e) {
			e.printStackTrace();
		}
	}


	/**
	 * Register new ensemble
	 *
	 * @param ensemble Ensemble class (must be annotated with {@link cz.cuni.mff.d3s.deeco.annotations.Ensemble} annotation)
	 */
	public void registerEnsemble(Class ensemble) {
		try {
			mProcessor.process(ensemble);
			Logger.i("DEECo ensemble " + ensemble.getName() + " registered.");
		} catch (AnnotationProcessorException e) {
			e.printStackTrace();
		}
	}


	/**
	 * Start JDEECo runtime
	 */
	public void startRuntime() {
		mUdpBroadcast.startReceiving();
		mDEECoRuntime.start();
		mRunning = true;
		Logger.i("DEECo runtime started.");
	}


	/**
	 * Stop JDEECo runtime
	 */
	public void stopRuntime() {
		mUdpBroadcast.stopReceiving();
		mDEECoRuntime.stop();
		mRunning = false;
		Logger.i("DEECo runtime stopped.");
	}


	/**
	 * Check if JDEECo runtime is running
	 *
	 * @return true if running
	 */
	public boolean isRunning() {
		return mRunning;
	}
}
