package cz.kinst.jakub.diploma.offloading;

import cz.kinst.jakub.diploma.offloading.deeco.events.DeploymentPlanUpdateEvent;
import cz.kinst.jakub.diploma.offloading.deeco.model.BackendDeploymentPlan;

/**
 * Created by jakubkinst on 04/02/15.
 */
public class Frontend {
    private final OffloadingManager mOffloadingManager;
    private OnDeploymentPlanUpdatedListener mOnDeploymentPlanUpdatedListener;
    private BackendDeploymentPlan mBackendDeploymentPlan = new BackendDeploymentPlan();

    public Frontend(OffloadingManager offloadingManager) {
        mOffloadingManager = offloadingManager;
        BusProvider.get().register(this);
    }

    public void setOnDeploymentPlanUpdatedListener(OnDeploymentPlanUpdatedListener mOnDeploymentPlanUpdatedListener) {
        this.mOnDeploymentPlanUpdatedListener = mOnDeploymentPlanUpdatedListener;
    }

    public final void onEventMainThread(DeploymentPlanUpdateEvent event) {
        if (mOnDeploymentPlanUpdatedListener != null)
            mOnDeploymentPlanUpdatedListener.onDeploymentPlanUpdated(event.getPlan());
        mBackendDeploymentPlan = event.getPlan();
    }

    public BackendDeploymentPlan getDeploymentPlan() {
        return mBackendDeploymentPlan;
    }

    public String getActiveBackendAddress(Class backendInterface) {
        String path = mOffloadingManager.getBackendId(backendInterface);
        String planned = getDeploymentPlan().getPlan(path);
        if (planned != null)
            return planned;
        else
            return mOffloadingManager.getLocalIpAddress();

    }

    public <T> T getActiveBackendProxy(Class<T> backendInterface) {
        return mOffloadingManager.getBackendProxy(backendInterface, getActiveBackendAddress(backendInterface));
    }
}
