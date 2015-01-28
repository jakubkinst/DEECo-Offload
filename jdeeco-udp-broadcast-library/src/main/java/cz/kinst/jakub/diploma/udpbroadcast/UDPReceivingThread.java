package cz.kinst.jakub.diploma.udpbroadcast;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Arrays;

/**
 * Created by jakubkinst on 28/01/15.
 */
public class UDPReceivingThread extends Thread {
    private final UDPBroadcast mUdpBroadcast;
    private DatagramSocket mSocket;

    public UDPReceivingThread(UDPBroadcast udpBroadcast) {
        mUdpBroadcast = udpBroadcast;
    }


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
                if (mUdpBroadcast.getOnPacketReceivedListener() != null)
                    mUdpBroadcast.getOnPacketReceivedListener().onUdpPacketReceived(packet);
                else
                    mUdpBroadcast.logError("No listener for incoming UDP packets registered");
            }
        } catch (IOException ex) {
            mUdpBroadcast.logError("Oops: " + ex.getMessage());
        }
    }

    public void closeSocket() {
        mSocket.disconnect();
        mSocket.close();
    }
}
