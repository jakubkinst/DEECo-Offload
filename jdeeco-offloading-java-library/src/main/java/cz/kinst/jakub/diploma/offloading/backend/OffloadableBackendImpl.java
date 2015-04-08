package cz.kinst.jakub.diploma.offloading.backend;

import org.restlet.resource.ServerResource;

import java.util.Map;

import cz.kinst.jakub.diploma.offloading.logger.Logger;
import cz.kinst.jakub.diploma.offloading.model.NFPData;
import cz.kinst.jakub.diploma.offloading.model.StateBundle;

/**
 * Offloadable backend implementation superclass.
 * Each offloadable backend implementation must extend this class.
 * It provides implementation of common methods.
 * <p/>
 * ---------------------------
 * Created by Jakub Kinst 2015
 * E-mail: jakub@kinst.cz
 */
public abstract class OffloadableBackendImpl extends ServerResource {

	/**
	 * Performance provider responsible for measuring performance and producing {@link cz.kinst.jakub.diploma.offloading.model.NFPData}
	 * and finding the best of the NFPData alternatives
	 */
	BackendPerformanceProvider mBackendPerformanceProvider;

	/**
	 * Backend ID - path that is used in URL to access the backend API via HTTP
	 */
	private String mBackendId;


	public OffloadableBackendImpl() {
	}


	/**
	 * @param id                         Backend ID
	 * @param backendPerformanceProvider Performance provider responsible for measuring performance and producing {@link cz.kinst.jakub.diploma.offloading.model.NFPData}
	 *                                   and finding the best of the NFPData alternatives
	 */
	public OffloadableBackendImpl(String id, BackendPerformanceProvider backendPerformanceProvider) {
		mBackendId = id;
		mBackendPerformanceProvider = backendPerformanceProvider;
	}


	/**
	 * Backend ID getter
	 *
	 * @return backend ID
	 */
	public String getBackendId() {
		return mBackendId;
	}


	/**
	 * Check performance of the backend
	 *
	 * @return NFPData
	 */
	public NFPData checkPerformance() {
		return mBackendPerformanceProvider.checkPerformance();
	}


	/**
	 * Find the best alternative out of set of other alternatives
	 *
	 * @param alternatives map containing alternatives for different devices
	 * @return the IP address of a device with the best performance
	 */
	public String findOptimalAlternative(Map<String, NFPData> alternatives) {
		return mBackendPerformanceProvider.findOptimalAlternative(alternatives);
	}


	/**
	 * Exposed method to get state data for the currently connected client
	 *
	 * @return
	 */
	public StateBundle getStateData() {
		StateBundle bundle = (StateBundle) getContext().getAttributes().get(getClientInfo().getAddress());
		if (bundle == null) {
			bundle = new StateBundle();
			setStateData(bundle);
		}
		return bundle;
	}


	/**
	 * Exposed method to save state data for the currently connected client
	 *
	 * @param stateData
	 */
	public void setStateData(StateBundle stateData) {
		getContext().getAttributes().put(getClientInfo().getAddress(), stateData);
		Logger.d("Received state data from " + getClientInfo().getAddress());
	}

}
