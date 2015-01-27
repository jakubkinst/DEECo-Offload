package cz.kinst.jakub.offloading.logger;

/**
 * Created by jakubkinst on 23/01/15.
 */
public class Logger {
    private static LogProvider sLogger;

    public static void setProvider(LogProvider logger) {
        sLogger = logger;
    }

    public static void d(String message) {
        if (sLogger==null)
            throw new NoLogProviderRegisteredException();
        sLogger.d(message);
    }

    public static void i(String message) {
        if (sLogger==null)
            throw new NoLogProviderRegisteredException();
        sLogger.i(message);
    }

    public static void e(String message) {
        if (sLogger==null)
            throw new NoLogProviderRegisteredException();
        sLogger.e(message);
    }
}
