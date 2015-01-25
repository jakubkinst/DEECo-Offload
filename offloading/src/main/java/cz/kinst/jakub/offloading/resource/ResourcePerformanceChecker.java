package cz.kinst.jakub.offloading.resource;

import java.util.Map;

import cz.kinst.jakub.offloading.deeco.model.NFPData;

/**
 * Created by jakubkinst on 23/01/15.
 */
//TODO: rename class and methods properly
public abstract class ResourcePerformanceChecker {
    public abstract NFPData checkPerformance();

    public abstract String findOptimalAlternative(Map<String, NFPData> alternatives);
}
