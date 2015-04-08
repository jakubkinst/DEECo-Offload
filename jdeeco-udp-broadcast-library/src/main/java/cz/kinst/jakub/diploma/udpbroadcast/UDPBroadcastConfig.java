package cz.kinst.jakub.diploma.udpbroadcast;

/**
 * UDP Broadcast configuration class.
 * Fields are static and can be overwritten.
 * <p/>
 * ---------------------------
 * Created by Jakub Kinst 2014
 * E-mail: jakub@kinst.cz
 */
public class UDPBroadcastConfig {
	/**
	 * Log tag used to distinct log messages in console
	 */
	public static String LOG_TAG = "UDP COMM";

	/**
	 * UDP port number to be used for UDP broadcast communication
	 */
	public static int PORT = 2133;

	/**
	 * The size of a packet sent via UDP broadcast.
	 * This should be the same as the size used inside JDEECo knowledge cloning implementation.
	 */
	public static int PACKET_SIZE = 1000;

	/**
	 * Debug mode switch affecting logging level
	 */
	public static boolean DEBUG_MODE = false;
}
