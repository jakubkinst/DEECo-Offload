package cz.kinst.jakub.diploma.offloading;

import cz.kinst.jakub.diploma.offloading.deeco.events.DeploymentPlanUpdateEvent;
import cz.kinst.jakub.diploma.offloading.deeco.model.BackendDeploymentPlan;
import cz.kinst.jakub.diploma.offloading.logger.Logger;

/**
 * Created by jakubkinst on 04/02/15.
 */
public class Frontend {
    private final OffloadingManager mOffloadingManager;
    private OnDeploymentPlanUpdatedListener mOnDeploymentPlanUpdatedListener;
    private BackendDeploymentPlan mBackendDeploymentPlan;
    private OnBackendMoveListener mOnBackendMoveListener;

    public Frontend(OffloadingManager offloadingManager) {
        mOffloadingManager = offloadingManager;
        mBackendDeploymentPlan = new BackendDeploymentPlan(offloadingManager.getLocalIpAddress());
        BusProvider.get().register(this);
    }

    public void setOnDeploymentPlanUpdatedListener(OnDeploymentPlanUpdatedListener listener) {
        this.mOnDeploymentPlanUpdatedListener = listener;
    }

    public void setOnBackendMoveListener(OnBackendMoveListener listener) {
        this.mOnBackendMoveListener = listener;
    }

    public final void onEvent(DeploymentPlanUpdateEvent event) {
        boolean dirty = false;
        BackendDeploymentPlan newPlan = event.getPlan();
        BackendDeploymentPlan oldPlan = mBackendDeploymentPlan;
        mBackendDeploymentPlan = newPlan;
        for (String backendId : newPlan.getBackends()) {
            String oldBackendAddress = oldPlan.getPlan(backendId);
            String newBackendAddress = newPlan.getPlan(backendId);
            if (!oldBackendAddress.equals(newBackendAddress)) {// address has changed
                dirty = true;
                if (mOnBackendMoveListener != null)
                    mOnBackendMoveListener.onBackendMovingStarted(backendId, oldBackendAddress, newBackendAddress);
                Logger.e("MOVING BACKEND START");
                mOffloadingManager.moveBackendStateData(backendId, oldBackendAddress, newBackendAddress);
                Logger.e("MOVING BACKEND END");
                if (mOnBackendMoveListener != null)
                    mOnBackendMoveListener.onBackendMovingDone(backendId, oldBackendAddress, newBackendAddress);

            }
        }
        if (mOnDeploymentPlanUpdatedListener != null) {
            mOnDeploymentPlanUpdatedListener.onDeploymentPlanUpdated(newPlan);
        }
    }

    public BackendDeploymentPlan getDeploymentPlan() {
        return mBackendDeploymentPlan;
    }

    public String getActiveBackendAddress(Class backendInterface) {
        String path = mOffloadingManager.getBackendId(backendInterface);
        String planned = getDeploymentPlan().getPlan(path);
        return planned;

    }

    public <T> T getActiveBackendProxy(Class<T> backendInterface) {
        return mOffloadingManager.getBackendProxy(backendInterface, getActiveBackendAddress(backendInterface));
    }
}
