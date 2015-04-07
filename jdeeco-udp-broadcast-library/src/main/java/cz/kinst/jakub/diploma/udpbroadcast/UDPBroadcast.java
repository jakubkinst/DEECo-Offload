package cz.kinst.jakub.diploma.udpbroadcast;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 * This class is responsible for the actual UDP communication. It is abstract because the
 * implementation of some methods differ across the platforms (Java SE/Android).
 * It represents the physical networking layer.
 * <p/>
 * It can broadcast the UDP packets via {@link #sendPacket(byte[])} method.
 * It is also used to listen for incoming UDP broadcast packet. To do so, you have to call {@link #startReceiving()} method.
 * <p/>
 * ---------------------------
 * Created by Jakub Kinst 2014
 * E-mail: jakub@kinst.cz
 */
public abstract class UDPBroadcast {

	/**
	 * Thread responsible for receiving UDP broadcasts in background
	 * so it does not block main (UI)thread.
	 */
	private UDPBroadcastReceivingThread mReceivingThread;
	/**
	 * Listener which can be registered from outside this class.
	 * Used to listen for incoming UDP packets.
	 */
	private OnUdpPacketReceivedListener mOnPacketReceivedListener;


	/**
	 * Broadcasts UDP packet into the local network.
	 * This is the main method to use to send data to other devices
	 * <p/>
	 * Port used in this method is set in config class ({@link cz.kinst.jakub.diploma.udpbroadcast.UDPBroadcastConfig#PORT})
	 *
	 * @param packet UDP Packet to send via UDP broadcast
	 */
	public final void sendPacket(byte[] packet) {
		try {
			DatagramSocket socket = new DatagramSocket();
			socket.setBroadcast(true);
			DatagramPacket sendPacket = new DatagramPacket(packet, packet.length, getBroadcastAddress(), UDPBroadcastConfig.PORT);
			socket.send(sendPacket);
			logDebug("Broadcast packet sent to: " + getBroadcastAddress().getHostAddress());
		} catch (IOException e) {
			logError("IOException during packet broadcast: " + e.getMessage());
		}
	}


	/**
	 * Starts receiving UDP broadcast packets in the background thread.
	 * Using {@link UDPBroadcastReceivingThread} to do this.
	 */
	public final void startReceiving() {
		mReceivingThread = new UDPBroadcastReceivingThread(this);
		mReceivingThread.start();
	}


	/**
	 * Stops receiving UDP broadcast packets in the background thread by interrupting the thread itself.
	 */
	public final void stopReceiving() {
		mReceivingThread.interrupt();

		// let the thread be Garbage-Collected
		mReceivingThread = null;
	}


	/**
	 * Provides IP address that should be used to broadcast UDP packets to
	 *
	 * @return IP address used to broadcast
	 */
	protected abstract InetAddress getBroadcastAddress();


	/**
	 * Provides current host's IP address.
	 *
	 * @return IP address of local host
	 */
	public abstract String getMyIpAddress();


	protected void onPacketReceived(DatagramPacket packet) {
		if (getOnPacketReceivedListener() != null)
			getOnPacketReceivedListener().onUdpPacketReceived(packet);
		else
			logError("No listener for incoming UDP packets registered");
	}


	/**
	 * Get registered listener for incoming UDP broadcast packets
	 *
	 * @return listener for incoming UDP broadcast packets
	 */
	public OnUdpPacketReceivedListener getOnPacketReceivedListener() {
		return mOnPacketReceivedListener;
	}


	/**
	 * Register listener for incoming UDP broadcast packets
	 *
	 * @param listener listener for incoming UDP broadcast packets
	 */
	public void setOnPacketReceivedListener(OnUdpPacketReceivedListener listener) {
		this.mOnPacketReceivedListener = listener;
	}


	protected abstract void logDebug(String message);


	protected abstract void logError(String message);


	protected abstract void logInfo(String message);

	public interface OnUdpPacketReceivedListener {
		void onUdpPacketReceived(DatagramPacket packet);
	}
}
