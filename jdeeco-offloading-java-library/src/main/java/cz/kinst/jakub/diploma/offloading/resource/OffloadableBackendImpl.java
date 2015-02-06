package cz.kinst.jakub.diploma.offloading.resource;

import org.restlet.resource.ServerResource;

import java.util.Map;
import java.util.concurrent.ConcurrentMap;

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
        getContext().setAttributes(stateData.getMap());
        Logger.d("Received state data from " + getClientInfo().getAddress());
    }

    public StateBundle getStateData() {
        ConcurrentMap<String, Object> attributes = getContext().getAttributes();
        StateBundle bundle = new StateBundle(attributes);
        return bundle;
    }

}
