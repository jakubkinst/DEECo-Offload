package cz.kinst.jakub.diploma.udpbroadcast;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 * Created by jakubkinst on 03/12/14.
 */
public abstract class UDPBroadcast {

    private boolean mIsReceiving = false;
    private UDPReceivingThread mReceivingThread;

    public interface OnUdpPacketReceivedListener {
        void onUdpPacketReceived(DatagramPacket packet);
    }

    private OnUdpPacketReceivedListener mOnPacketReceivedListener;

    public final void sendPacket(byte[] packet) {
        try {
            DatagramSocket socket = new DatagramSocket();
            socket.setBroadcast(true);
            DatagramPacket sendPacket = new DatagramPacket(packet, packet.length, getBroadcastAddress(), UDPBroadcastConfig.PORT);
            socket.send(sendPacket);
            logDebug("Broadcast packet sent to: " + getBroadcastAddress().getHostAddress());
        } catch (IOException e) {
            logError("IOException: " + e.getMessage());
        }
    }

    public final void startReceiving() {
        mReceivingThread = new UDPReceivingThread(this);
        mReceivingThread.start();
    }

    public final void stopReceiving() {
        mReceivingThread.closeSocket();
        mReceivingThread.interrupt();
        mReceivingThread = null;
    }

    protected abstract InetAddress getBroadcastAddress();


    public abstract String getMyIpAddress();

    protected void onPacketReceived(DatagramPacket packet) {

    }

    protected abstract void logDebug(String message);

    protected abstract void logError(String message);

    protected abstract void logInfo(String message);


    public void setOnPacketReceivedListener(OnUdpPacketReceivedListener listener) {
        this.mOnPacketReceivedListener = listener;
    }

    public OnUdpPacketReceivedListener getOnPacketReceivedListener() {
        return mOnPacketReceivedListener;
    }
}
