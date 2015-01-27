package cz.kinst.jakub.diploma.offloading.logger;

/**
 * Created by jakubkinst on 21/01/15.
 */
public class JavaLogProvider implements LogProvider {
    public void d(String message) {
        System.out.println("Offload DEBUG: " + message);
    }

    public void i(String message) {
        System.out.println("Offload INFO: " + message);
    }

    public void e(String message) {
        System.err.println("Offload ERROR: " + message);
    }
}