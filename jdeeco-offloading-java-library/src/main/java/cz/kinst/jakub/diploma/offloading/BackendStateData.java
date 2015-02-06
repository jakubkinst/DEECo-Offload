package cz.kinst.jakub.diploma.offloading;

import com.google.gson.Gson;

import cz.kinst.jakub.diploma.offloading.deeco.events.ShouldPullBackendStateDataEvent;
import cz.kinst.jakub.diploma.offloading.logger.Logger;
import cz.kinst.jakub.diploma.offloading.resource.OffloadableBackend;

/**
 * Created by jakubkinst on 04/02/15.
 */
public class BackendStateData {
    private final String mBackendId;
    private Class<? extends OffloadableBackend> mBackendInterface;
    private StateBundle mData;
    private final OffloadingManager mOffloadingManager;
    private boolean mMovingInProgress = false;

    public BackendStateData(String backendId, Class<? extends OffloadableBackend> backendInterface, OffloadingManager offloadingManager) {
        mBackendId = backendId;
        mBackendInterface = backendInterface;
        mOffloadingManager = offloadingManager;
        BusProvider.get().register(this);
    }

    public void onEvent(ShouldPullBackendStateDataEvent event) {
        if (event.getBackendId().equals(mBackendId) && !isMoving()) {
            String pullFrom = event.getBackendAddress();
            Logger.e("PERIODIC pull from " + pullFrom);
            try {
                pullData(pullFrom);
            } catch (Exception e) {
                Logger.i("Could not get state data. The backend is probable dead.");
            }
        }
    }

    private OffloadableBackend getBackendProxy(String backendAddress) {
        return mOffloadingManager.getBackendProxy(mBackendInterface, backendAddress);
    }

    public void pushData(String backendAddress) {
        getBackendProxy(backendAddress).setStateData(mData);
        Logger.d("Pushed data to " + backendAddress);
    }

    public synchronized void pullData(String backendAddress) {
        mData = getBackendProxy(backendAddress).getStateData();
        Logger.d("Pulled data from " + backendAddress);
        Logger.d("Data: " + new Gson().toJson(mData));
    }

    public synchronized void moveData(String oldBackendAddress, String newBackendAddress) {
        mMovingInProgress = true;
        try {
            pullData(oldBackendAddress);
        } catch (Exception e) {
            Logger.i("Could not get old state data while moving. The old backend is probable dead. Pushing cached data.");
        }
        pushData(newBackendAddress);
        mMovingInProgress = false;
    }

    public boolean isMoving() {
        return mMovingInProgress;
    }

    public StateBundle getData() {
        return mData;
    }

    public String getBackendId() {
        return mBackendId;
    }
}
