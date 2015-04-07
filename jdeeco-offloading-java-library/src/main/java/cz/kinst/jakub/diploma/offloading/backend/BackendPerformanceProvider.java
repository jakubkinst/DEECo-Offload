package cz.kinst.jakub.diploma.offloading.backend;

import java.util.Map;

import cz.kinst.jakub.diploma.offloading.model.NFPData;

/**
 * ---------------------------
 * Created by Jakub Kinst 2015
 * E-mail: jakub@kinst.cz
 */
public abstract class BackendPerformanceProvider {
	public abstract NFPData checkPerformance();

	public abstract String findOptimalAlternative(Map<String, NFPData> alternatives);
}
