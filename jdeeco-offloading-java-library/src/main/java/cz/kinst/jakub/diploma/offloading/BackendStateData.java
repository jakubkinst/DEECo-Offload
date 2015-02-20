package cz.kinst.jakub.diploma.offloading;

import com.google.common.eventbus.Subscribe;
import com.google.gson.Gson;

import cz.kinst.jakub.diploma.offloading.backend.OffloadableBackend;
import cz.kinst.jakub.diploma.offloading.events.ShouldPullBackendStateDataEvent;
import cz.kinst.jakub.diploma.offloading.logger.Logger;
import cz.kinst.jakub.diploma.offloading.model.StateBundle;
import cz.kinst.jakub.diploma.offloading.utils.BusProvider;

/**
 * Application-level component responsible for storing and management of backend state data.
 * It is able to pull and push data from/to backends as well as performing moving of state data from one
 * backend to another.
 * <p/>
 * ---------------------------
 * Created by Jakub Kinst 2015
 * E-mail: jakub@kinst.cz
 */
public class BackendStateData {
    /**
     * Backend ID (of the backend this state data corresponds to)
     */
    private final String mBackendId;

    /**
     * Backend Interface (of the backend this state data corresponds to)
     */
    private Class<? extends OffloadableBackend> mBackendInterface;

    /**
     * Actual holder of the state data
     */
    private StateBundle mData = new StateBundle();

    /**
     * Reference to {@link cz.kinst.jakub.diploma.offloading.OffloadingManager} instance
     */
    private final OffloadingManager mOffloadingManager;

    /**
     * Flag for state data moving in progress
     */
    private boolean mMovingInProgress = false;

    /**
     * @param backendId         Backend ID
     * @param backendInterface  Backend Interface
     * @param offloadingManager reference to {@link cz.kinst.jakub.diploma.offloading.OffloadingManager}
     */
    public BackendStateData(String backendId, Class<? extends OffloadableBackend> backendInterface, OffloadingManager offloadingManager) {
        mBackendId = backendId;
        mBackendInterface = backendInterface;
        mOffloadingManager = offloadingManager;
        BusProvider.get().register(this);
    }

    /**
     * Method which gets called periodically from {@link cz.kinst.jakub.diploma.offloading.deeco.components.StateDataMonitorComponent#doPeriodicPull(String, String)} via EventBus
     *
     * @param event
     */
    @Subscribe
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

    /**
     * Push state data to given backend
     *
     * @param backendAddress
     */
    public void pushData(String backendAddress) {
        getBackendProxy(backendAddress).setStateData(mData);
        Logger.d("Pushed data to " + backendAddress);
    }

    /**
     * Pull state data from given backend (synchronized to remain thread-safe)
     *
     * @param backendAddress Backend IP address
     */
    public synchronized void pullData(String backendAddress) {
        mData = getBackendProxy(backendAddress).getStateData();
        Logger.d("Pulled data from " + backendAddress);
        Logger.d("Data: " + new Gson().toJson(mData));
    }

    /**
     * Move data from one backend to another (synchronized to remain thread-safe)
     *
     * @param oldBackendAddress
     * @param newBackendAddress
     */
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

    /**
     * Check if moving state data is in progress right now
     *
     * @return true if moving is in progress
     */
    public boolean isMoving() {
        return mMovingInProgress;
    }

    /**
     * Returns actual state data
     *
     * @return state data
     */
    public StateBundle getData() {
        return mData;
    }

    /**
     * Returns backend ID of backend this state data corresponds to
     *
     * @return Backend ID
     */
    public String getBackendId() {
        return mBackendId;
    }
}
