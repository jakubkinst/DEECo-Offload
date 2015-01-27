package cz.kinst.jakub.diploma.offloading.logger;

/**
 * Created by jakubkinst on 23/01/15.
 */
public class NoLogProviderRegisteredException extends RuntimeException {
    public NoLogProviderRegisteredException() {
        super("No LogProvider implementation provided. Call Logger.setProvider(...) with proper Provider.");
    }
}
