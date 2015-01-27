package cz.kinst.jakub.offloading.logger;

/**
 * Created by jakubkinst on 23/01/15.
 */
public interface LogProvider {
    public void d(String message);

    public void i(String message);

    public void e(String message);
}
