package cz.kinst.jakub.diploma.offloading.logger;

/**
 * ---------------------------
 * Created by Jakub Kinst 2015
 * E-mail: jakub@kinst.cz
 */
public class NoLogProviderRegisteredException extends RuntimeException {
    public NoLogProviderRegisteredException() {
        super("No LogProvider implementation provided. Call Logger.setProvider(...) with proper Provider.");
    }
}
