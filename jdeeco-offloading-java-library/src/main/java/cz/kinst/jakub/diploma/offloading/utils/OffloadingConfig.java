package cz.kinst.jakub.diploma.offloading.utils;

import java.util.logging.Level;

/**
 * Offloading configuration - mostly time period values for DEECo components and ensembles
 * <p/>
 * ---------------------------
 * Created by Jakub Kinst 2015
 * E-mail: jakub@kinst.cz
 */
public class OffloadingConfig {
	/**
	 * How often should each DEECo component check if it's still alive (ms)
	 */
	public static final long PING_INTERVAL_MS = 1000;

	/**
	 * How often should monitors push it's NFPData to Planner (ms)
	 */
	public static final long NFP_DATA_COLLECTING_INTERVAL_MS = 3500;

	/**
	 * How often should the state of backend monitors be distributed from Planner (ms)
	 */
	public static final long STATE_DISTRIBUTING_INTERVAL_MS = 400;

	/**
	 * How often should the FrontendMonitor be updated about current backend (ms)
	 */
	public static final long FRONTEND_MONITOR_UPDATE_INTERVAL_MS = 500;

	/**
	 * How often should the StateDataMonitor be updated about current backend (ms)
	 */
	public static final long STATE_DATA_MONITOR_UPDATE_INTERVAL_MS = 600;

	/**
	 * How often should monitors check for it's IP address if it has changed (ms)
	 */
	public static final long IP_UPDATE_INTERVAL_MS = 800;

	/**
	 * HTTP port used to serve backends
	 */
	public static int HTTP_PORT_FOR_BACKENDS = 8182;

	/**
	 * Logging level
	 */
	public static Level JDEECO_LOGGING_LEVEL = Level.SEVERE;
}
