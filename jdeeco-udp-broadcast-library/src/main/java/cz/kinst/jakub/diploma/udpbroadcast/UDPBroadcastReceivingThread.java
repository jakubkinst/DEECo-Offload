package cz.kinst.jakub.diploma.udpbroadcast;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Arrays;

/**
 * Thread which should be spawned to be receiving UDP broadcast packets from the network in the background
 * so it's not blocking main (UI) thread. It has a reference to {@link cz.kinst.jakub.diploma.udpbroadcast.UDPBroadcast} instance to inform it about new packets
 * <p/>
 * ---------------------------
 * Created by Jakub Kinst 2015
 * E-mail: jakub@kinst.cz
 */
public class UDPBroadcastReceivingThread extends Thread {
	private final UDPBroadcast mUdpBroadcast;
	private DatagramSocket mSocket;


	/**
	 * Creates a new instance of this thread
	 *
	 * @param udpBroadcast reference to {@link cz.kinst.jakub.diploma.udpbroadcast.UDPBroadcast} implementation
	 */
	public UDPBroadcastReceivingThread(UDPBroadcast udpBroadcast) {
		mUdpBroadcast = udpBroadcast;
	}


	/**
	 * {@inheritDoc}
	 */
	@Override
	public void run() {
		try {
			//Keep a socket open to listen to all the UDP trafic that is destined for this port
			mSocket = new DatagramSocket(UDPBroadcastConfig.PORT, InetAddress.getByName("0.0.0.0"));
			mSocket.setBroadcast(true);

			while (!isInterrupted()) {
				mUdpBroadcast.logDebug("Ready to receive broadcast packets!");

				//Receive a packet
				byte[] recvBuf = new byte[UDPBroadcastConfig.PACKET_SIZE];
				DatagramPacket packet = new DatagramPacket(recvBuf, recvBuf.length);
				if (!mSocket.isClosed())
					mSocket.receive(packet);
				// get actual length of data and trim the byte array accordingly
				int length = packet.getLength();
				byte[] data = Arrays.copyOfRange(packet.getData(), 0, length);
				packet.setData(data);

				//Packet received

				String sender = packet.getAddress().getHostAddress();

				// if received message is from myself, skip
				if (sender.equals(mUdpBroadcast.getMyIpAddress())) continue;

				mUdpBroadcast.logDebug("Packet received from: " + sender + "; Size: " + data.length);
				//String content = new String(data).trim();
				//logDebug("Content: " + content);
				mUdpBroadcast.onPacketReceived(packet);
			}
		} catch (IOException ex) {
			mUdpBroadcast.logError("Oops: " + ex.getMessage());
		}
	}


	/**
	 * {@inheritDoc}
	 */
	@Override
	public void interrupt() {
		// make sure to close the socket to avoid Exception inside the thread implementation
		closeSocket();
		super.interrupt();
	}


	/**
	 * Closes UDP socket. Called before interrupting the thread!
	 */
	private void closeSocket() {
		mSocket.disconnect();
		mSocket.close();
	}
}
