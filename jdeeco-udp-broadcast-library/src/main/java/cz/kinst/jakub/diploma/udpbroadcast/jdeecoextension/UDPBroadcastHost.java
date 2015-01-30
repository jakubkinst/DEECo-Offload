package cz.kinst.jakub.diploma.udpbroadcast.jdeecoextension;

import java.net.DatagramPacket;

import cz.cuni.mff.d3s.deeco.network.AbstractHost;
import cz.cuni.mff.d3s.deeco.network.KnowledgeDataReceiver;
import cz.cuni.mff.d3s.deeco.network.KnowledgeDataSender;
import cz.cuni.mff.d3s.deeco.network.NetworkInterface;
import cz.cuni.mff.d3s.deeco.network.PacketReceiver;
import cz.cuni.mff.d3s.deeco.network.PacketSender;
import cz.kinst.jakub.diploma.udpbroadcast.UDPBroadcast;
import cz.kinst.jakub.diploma.udpbroadcast.UDPBroadcastConfig;

/**
 * This is an implementation of {@link cz.cuni.mff.d3s.deeco.network.AbstractHost} class from JDEECo
 * library. It is the actual extension adding support for UDP broadcast Knowledge Cloning.
 * <p/>
 * This class represents the layer between JDEECo and physical UDP network communication
 * <p/>
 * ---------------------------
 * Created by Jakub Kinst 2014
 * E-mail: jakub@kinst.cz
 */
public class UDPBroadcastHost extends AbstractHost implements NetworkInterface {

    private final PacketReceiver packetReceiver;
    private final PacketSender packetSender;
    private final UDPBroadcast udpBroadcast;

    /**
     * Creates an instance based on local host's IP address and platform-specific
     * UDPBroadcast implementation.
     *
     * @param ipAddress    Local host's IP address
     * @param udpBroadcast Implementation of {@link cz.kinst.jakub.diploma.udpbroadcast.UDPBroadcast} as the physical networking layer
     */
    public UDPBroadcastHost(String ipAddress, UDPBroadcast udpBroadcast) {
        super(ipAddress, new DefaultCurrentTimeProvider());
        this.udpBroadcast = udpBroadcast;
        this.packetReceiver = new PacketReceiver(id, UDPBroadcastConfig.PACKET_SIZE);
        this.packetSender = new PacketSender(this, UDPBroadcastConfig.PACKET_SIZE, false, false);
        this.packetReceiver.setCurrentTimeProvider(this);
        this.udpBroadcast.setOnPacketReceivedListener(new UDPBroadcast.OnUdpPacketReceivedListener() {
            @Override
            public void onUdpPacketReceived(DatagramPacket packet) {
                packetReceived(packet.getData(), 1);
            }
        });
    }

    /**
     * Registers a Knowledge Data Receiver to the packet receiver
     *
     * @param knowledgeDataReceiver
     */
    public void setKnowledgeDataReceiver(KnowledgeDataReceiver knowledgeDataReceiver) {
        packetReceiver.setKnowledgeDataReceiver(knowledgeDataReceiver);
    }

    /**
     * Provides Knowledge Data Sender
     */
    public KnowledgeDataSender getKnowledgeDataSender() {
        return packetSender;
    }

    /**
     * This method bridges the physical network layer to the JDEECo knowledge cloning layer.
     * It has to be called when new packet is received from the network
     * <p/>
     * {@inheritDoc}
     */
    @Override
    public void packetReceived(byte[] packet, double rssi) {
        packetReceiver.packetReceived(packet, rssi);
    }


    /**
     * This method bridges the JDEECo knowledge cloning layer to the physical network layer.
     * JDEECo knoledge cloning layer should call this method to send UDP packets to the network
     * <p/>
     * {@inheritDoc}
     */
    @Override
    public void sendPacket(byte[] packet, String recipient) {
        // SEND UDP packet via UDP interface
        udpBroadcast.sendPacket(packet);
    }

    public void finalize() {
        packetReceiver.clearCachedMessages();
    }
}
