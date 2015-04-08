package cz.kinst.jakub.diploma.offloading.android.demo;

/**
 * Test payload of demo communication
 * <p/>
 * ---------------------------
 * Created by Jakub Kinst 2015
 * E-mail: jakub@kinst.cz
 */
class Message {
	public String message;
	public long timestamp;


	/**
	 * @param message   Message content
	 * @param timestamp time of creation
	 */
	Message(String message, long timestamp) {
		this.message = message;
		this.timestamp = timestamp;
	}
}
