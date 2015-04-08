package cz.kinst.jakub.diploma.udpbroadcast;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.UnknownHostException;
import java.util.Enumeration;

/**
 * Implementation of {@link cz.kinst.jakub.diploma.udpbroadcast.UDPBroadcast} for Java SE platform
 * <p/>
 * This contains some platform-specific implementation of important methods such as fetching
 * current IP address, and resolving UDP broadcast IP address.
 * <p/>
 * ---------------------------
 * Created by Jakub Kinst 2014
 * E-mail: jakub@kinst.cz
 */
public class JavaUDPBroadcast extends UDPBroadcast {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final InetAddress getBroadcastAddress() {
		try {
			Enumeration<NetworkInterface> interfaces =
					NetworkInterface.getNetworkInterfaces();
			while (interfaces.hasMoreElements()) {
				NetworkInterface networkInterface = interfaces.nextElement();
				if (networkInterface.isLoopback())
					continue;    // Don't want to broadcast to the loopback interface
				for (InterfaceAddress interfaceAddress :
						networkInterface.getInterfaceAddresses()) {
					InetAddress broadcast = interfaceAddress.getBroadcast();
					if (broadcast == null)
						continue;
					return broadcast;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return null;
	}


	/**
	 * {@inheritDoc}
	 */
	@Override
	public final String getMyIpAddress() {
		try {
			return Inet4Address.getLocalHost().getHostAddress();
		} catch (UnknownHostException e) {
			e.printStackTrace();
			return null;
		}
	}


	/**
	 * {@inheritDoc}
	 */
	@Override
	protected final void logDebug(String message) {
		if (UDPBroadcastConfig.DEBUG_MODE)
			System.out.println("DEBUG: " + UDPBroadcastConfig.LOG_TAG + ": " + message);
	}


	/**
	 * {@inheritDoc}
	 */
	@Override
	protected final void logError(String message) {
		System.err.println("ERROR: " + UDPBroadcastConfig.LOG_TAG + ": " + message);
	}


	/**
	 * {@inheritDoc}
	 */
	@Override
	protected final void logInfo(String message) {
		System.out.println("INFO: " + UDPBroadcastConfig.LOG_TAG + ": " + message);
	}
}
