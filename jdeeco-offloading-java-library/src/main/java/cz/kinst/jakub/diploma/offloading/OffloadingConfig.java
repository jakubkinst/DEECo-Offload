package cz.kinst.jakub.diploma.offloading;

import java.util.logging.Level;

/**
 * Created by jakubkinst on 23/01/15.
 */
public class OffloadingConfig {
    public static final long PING_INTERVAL_MS = 1000;
    public static final long NFP_DATA_COLLECTING_INTERVAL_MS = 3500;
    public static final long STATE_DISTRIBUTING_INTERVAL_MS = 400;
    public static final long UI_MONITOR_UPDATE_INTERVAL_MS = 500;
    public static final long STATE_DATA_MONITOR_UPDATE_INTERVAL_MS = 600;
    public static final long IP_UPDATE_INTERVAL_MS = 800;
    public static int HTTP_PORT_FOR_BACKENDS = 8182;
    public static Level JDEECO_LOGGING_LEVEL = Level.SEVERE;
}
