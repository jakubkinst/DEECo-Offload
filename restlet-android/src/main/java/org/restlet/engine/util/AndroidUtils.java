package org.restlet.engine.util;

/**
 * Created by jakubkinst on 11/02/15.
 */
public class AndroidUtils {
    /**
     * Method used to check if the code is currently running on Android Platform
     * via System properties {@see http://developer.android.com/reference/java/lang/System.html#getProperty()}
     *
     * @return true if running on Android, false otherwise
     */
    public static boolean isRunningOnAndroid() {
        return System.getProperty("java.vendor").contains("Android");
    }
}
