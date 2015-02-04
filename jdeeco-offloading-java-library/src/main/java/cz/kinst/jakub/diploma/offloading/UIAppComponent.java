package cz.kinst.jakub.diploma.offloading;

import cz.kinst.jakub.diploma.offloading.deeco.events.DeploymentPlanUpdateEvent;
import cz.kinst.jakub.diploma.offloading.deeco.model.DeploymentPlan;

/**
 * Created by jakubkinst on 04/02/15.
 */
public class UIAppComponent {
    private final OffloadingManager mOffloadingManager;
    private OnDeploymentPlanUpdatedListener mOnDeploymentPlanUpdatedListener;
    private DeploymentPlan mDeploymentPlan = new DeploymentPlan();

    public UIAppComponent(OffloadingManager offloadingManager) {
        mOffloadingManager = offloadingManager;
        BusProvider.get().register(this);
    }

    public void setOnDeploymentPlanUpdatedListener(OnDeploymentPlanUpdatedListener mOnDeploymentPlanUpdatedListener) {
        this.mOnDeploymentPlanUpdatedListener = mOnDeploymentPlanUpdatedListener;
    }

    public final void onEventMainThread(DeploymentPlanUpdateEvent event) {
        if (mOnDeploymentPlanUpdatedListener != null)
            mOnDeploymentPlanUpdatedListener.onDeploymentPlanUpdated(event.getPlan());
        mDeploymentPlan = event.getPlan();
    }

    public DeploymentPlan getDeploymentPlan() {
        return mDeploymentPlan;
    }

    public String getActiveBackendAddress(Class backendInterface) {
        String path = mOffloadingManager.getResourcePath(backendInterface);
        String planned = getDeploymentPlan().getPlan(path);
        if (planned != null)
            return planned;
        else
            return mOffloadingManager.getLocalIpAddress();

    }

    public <T> T getActiveBackendProxy(Class<T> backendInterface) {
        return mOffloadingManager.getResourceProxy(backendInterface, getActiveBackendAddress(backendInterface));
    }
}
