package cz.kinst.jakub.diploma.offloading.resource;

import java.util.Map;

import cz.kinst.jakub.diploma.offloading.deeco.model.NFPData;

/**
 * Created by jakubkinst on 23/01/15.
 */
public abstract class BackendPerformanceProvider {
    public abstract NFPData checkPerformance();

    public abstract String findOptimalAlternative(Map<String, NFPData> alternatives);
}
