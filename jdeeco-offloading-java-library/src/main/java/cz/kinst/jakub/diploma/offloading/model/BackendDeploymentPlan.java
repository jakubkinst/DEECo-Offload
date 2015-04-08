package cz.kinst.jakub.diploma.offloading.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Class representing a plan of backend deployment. It basically stores which IP address should be used for each
 * backend ID. This is a product of the Planner component.
 * <p/>
 * ---------------------------
 * Created by Jakub Kinst 2015
 * E-mail: jakub@kinst.cz
 */
public class BackendDeploymentPlan implements Serializable {
	private final String mLocalIpAddress;
	private Map<String, String> mPlan = new HashMap<>(); //key: appComponentId, value: deviceIp


	public BackendDeploymentPlan(String localIpAddress) {
		mLocalIpAddress = localIpAddress;
	}


	/**
	 * Set the plan to use device with deviceIp for backend with backendId
	 *
	 * @param backendId Backend ID
	 * @param deviceIp  Device IP address
	 */
	public void plan(String backendId, String deviceIp) {
		mPlan.put(backendId, deviceIp);
	}


	/**
	 * Returns device IP address that should be used to call the backend with backendId
	 *
	 * @param backendId Backend ID
	 * @return Device IP address
	 */
	public String getPlan(String backendId) {
		return mPlan.containsKey(backendId) ? mPlan.get(backendId) : mLocalIpAddress;
	}


	/**
	 * Returns set of backend IDs which we have some plan for
	 *
	 * @return
	 */
	public java.util.Set<String> getBackends() {
		return mPlan.keySet();
	}
}
