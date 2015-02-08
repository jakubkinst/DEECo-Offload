package cz.kinst.jakub.diploma.offloading.resource;

import org.restlet.resource.ServerResource;

import java.util.Map;

import cz.kinst.jakub.diploma.offloading.StateBundle;
import cz.kinst.jakub.diploma.offloading.deeco.model.NFPData;
import cz.kinst.jakub.diploma.offloading.logger.Logger;

/**
 * Created by jakubkinst on 07/01/15.
 */
public abstract class OffloadableBackendImpl extends ServerResource {

    BackendPerformanceProvider mBackendPerformanceProvider;
    private String mPath;

    public OffloadableBackendImpl() {
    }

    public OffloadableBackendImpl(String path, BackendPerformanceProvider backendPerformanceProvider) {
        mPath = path;
        mBackendPerformanceProvider = backendPerformanceProvider;
    }

    public String getPath() {
        return mPath;
    }

    public NFPData checkPerformance() {
        return mBackendPerformanceProvider.checkPerformance();
    }

    public String findOptimalAlternative(Map<String, NFPData> alternatives) {
        return mBackendPerformanceProvider.findOptimalAlternative(alternatives);
    }


    public void setStateData(StateBundle stateData) {
        getContext().getAttributes().put(getClientInfo().getAddress(), stateData);
        Logger.d("Received state data from " + getClientInfo().getAddress());
    }

    public StateBundle getStateData() {
        StateBundle bundle = (StateBundle) getContext().getAttributes().get(getClientInfo().getAddress());
        if (bundle == null) {
            bundle = new StateBundle();
            setStateData(bundle);
        }
        return bundle;
    }

}
