package cz.kinst.jakub.diploma.offloading;

import com.google.common.eventbus.Subscribe;

import cz.kinst.jakub.diploma.offloading.events.DeploymentPlanUpdateEvent;
import cz.kinst.jakub.diploma.offloading.listeners.OnBackendMoveListener;
import cz.kinst.jakub.diploma.offloading.listeners.OnDeploymentPlanUpdatedListener;
import cz.kinst.jakub.diploma.offloading.logger.Logger;
import cz.kinst.jakub.diploma.offloading.model.BackendDeploymentPlan;
import cz.kinst.jakub.diploma.offloading.utils.BusProvider;

/**
 * Application-component responsible for communicating with Application UI on one side
 * and DEECo components on the other.
 * <p/>
 * ---------------------------
 * Created by Jakub Kinst 2015
 * E-mail: jakub@kinst.cz
 */
public class Frontend {
	/**
	 * Reference to {@link cz.kinst.jakub.diploma.offloading.OffloadingManager} instance
	 */
	private final OffloadingManager mOffloadingManager;

	/**
	 * Listener for deployment plan updates
	 */
	private OnDeploymentPlanUpdatedListener mOnDeploymentPlanUpdatedListener;

	/**
	 * Current backend deployment plan (which backends are currently active)
	 */
	private BackendDeploymentPlan mBackendDeploymentPlan;

	/**
	 * Listener for backend moving process
	 */
	private OnBackendMoveListener mOnBackendMoveListener;


	/**
	 * @param offloadingManager reference to {@link cz.kinst.jakub.diploma.offloading.OffloadingManager} instance
	 */
	public Frontend(OffloadingManager offloadingManager) {
		mOffloadingManager = offloadingManager;
		mBackendDeploymentPlan = new BackendDeploymentPlan(offloadingManager.getLocalIpAddress());
		BusProvider.get().register(this);
	}


	/**
	 * Set listener for deployment plan update (used by UI to show update current backend address for example)
	 *
	 * @param listener
	 */
	public void setOnDeploymentPlanUpdatedListener(OnDeploymentPlanUpdatedListener listener) {
		this.mOnDeploymentPlanUpdatedListener = listener;
	}


	/**
	 * Set listener for moving process - informs about movin start and end (used to show moving progress dialog to the user)
	 *
	 * @param listener
	 */
	public void setOnBackendMoveListener(OnBackendMoveListener listener) {
		this.mOnBackendMoveListener = listener;
	}


	/**
	 * This method will be called from DEECo runtime by {@link cz.kinst.jakub.diploma.offloading.deeco.components.FrontendMonitorComponent#updateUi(cz.kinst.jakub.diploma.offloading.model.BackendDeploymentPlan)} via EventBus
	 * to let this class know of a new deployment plan
	 *
	 * @param event
	 */
	@Subscribe
	public final void onEvent(DeploymentPlanUpdateEvent event) {
		boolean dirty = false;
		BackendDeploymentPlan newPlan = event.getPlan();
		BackendDeploymentPlan oldPlan = mBackendDeploymentPlan;
		mBackendDeploymentPlan = newPlan;
		for (String backendId : newPlan.getBackends()) {
			String oldBackendAddress = oldPlan.getPlan(backendId);
			String newBackendAddress = newPlan.getPlan(backendId);
			if (!oldBackendAddress.equals(newBackendAddress)) {// address has changed
				if (mOnBackendMoveListener != null)
					mOnBackendMoveListener.onBackendMovingStarted(backendId, oldBackendAddress, newBackendAddress);
				Logger.i("MOVING BACKEND START");
				mOffloadingManager.moveBackendStateData(backendId, oldBackendAddress, newBackendAddress);
				Logger.i("MOVING BACKEND END");
				if (mOnBackendMoveListener != null)
					mOnBackendMoveListener.onBackendMovingDone(backendId, oldBackendAddress, newBackendAddress);

			}
		}

		if (mOnDeploymentPlanUpdatedListener != null) {
			mOnDeploymentPlanUpdatedListener.onDeploymentPlanUpdated(newPlan);
		}
	}


	/**
	 * Provides current deployment plan
	 *
	 * @return deployment plan
	 */
	public BackendDeploymentPlan getDeploymentPlan() {
		return mBackendDeploymentPlan;
	}


	/**
	 * Returns current backend address for specific backend interface according to the latest deployment plan
	 *
	 * @param backendInterface
	 * @return backend address
	 */
	public String getActiveBackendAddress(Class backendInterface) {
		String backendId = mOffloadingManager.getBackendId(backendInterface);
		return getActiveBackendAddress(backendId);
	}


	/**
	 * Returns current backend address for specific backend ID according to the latest deployment plan
	 *
	 * @param backendId
	 * @return backend address
	 */
	public String getActiveBackendAddress(String backendId) {
		String planned = getDeploymentPlan().getPlan(backendId);
		return planned;
	}


	/**
	 * Provides current backend proxy for specific backend interface according to the latest deployment plan
	 *
	 * @param backendInterface
	 * @param <T>
	 * @return
	 */
	public <T> T getActiveBackendProxy(Class<T> backendInterface) {
		return mOffloadingManager.getBackendProxy(backendInterface, getActiveBackendAddress(backendInterface));
	}
}
