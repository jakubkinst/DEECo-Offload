package cz.kinst.jakub.diploma.offloading.logger;

/**
 * ---------------------------
 * Created by Jakub Kinst 2015
 * E-mail: jakub@kinst.cz
 */
public interface LogProvider {
	public void d(String message);

	public void i(String message);

	public void e(String message);
}
