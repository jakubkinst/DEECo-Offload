package cz.kinst.jakub.diploma.offloading.resource;

import org.restlet.resource.ServerResource;

import java.util.Map;

import cz.kinst.jakub.diploma.offloading.deeco.model.NFPData;

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

}
