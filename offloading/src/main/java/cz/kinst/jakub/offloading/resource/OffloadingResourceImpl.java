package cz.kinst.jakub.offloading.resource;

import org.restlet.resource.ServerResource;

import java.util.Map;

import cz.kinst.jakub.offloading.deeco.model.NFPData;

/**
 * Created by jakubkinst on 07/01/15.
 */
public abstract class OffloadingResourceImpl extends ServerResource {

    ResourcePerformanceChecker mResourcePerformanceChecker;
    private String mPath;

    public OffloadingResourceImpl() {
    }

    public OffloadingResourceImpl(String path, ResourcePerformanceChecker resourcePerformanceChecker) {
        mPath = path;
        mResourcePerformanceChecker = resourcePerformanceChecker;
    }

    public String getPath() {
        return mPath;
    }

    public NFPData checkPerformance(String host) {
        return mResourcePerformanceChecker.checkPerformance(host);
    }

    public String findOptimalAlternative(Map<String, NFPData> alternatives) {
        return mResourcePerformanceChecker.findOptimalAlternative(alternatives);
    }

}
