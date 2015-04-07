package cz.kinst.jakub.diploma.offloading.logger;

/**
 * ---------------------------
 * Created by Jakub Kinst 2015
 * E-mail: jakub@kinst.cz
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