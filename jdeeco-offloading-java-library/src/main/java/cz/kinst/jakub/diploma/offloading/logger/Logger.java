package cz.kinst.jakub.diploma.offloading.logger;

/**
 * ---------------------------
 * Created by Jakub Kinst 2015
 * E-mail: jakub@kinst.cz
 */
public class Logger {
	private static LogProvider sLogger;


	public static void setProvider(LogProvider logger) {
		sLogger = logger;
	}


	public static void d(String message) {
		if (sLogger == null)
			throw new NoLogProviderRegisteredException();
		sLogger.d(message);
	}


	public static void i(String message) {
		if (sLogger == null)
			throw new NoLogProviderRegisteredException();
		sLogger.i(message);
	}


	public static void e(String message) {
		if (sLogger == null)
			throw new NoLogProviderRegisteredException();
		sLogger.e(message);
	}
}
