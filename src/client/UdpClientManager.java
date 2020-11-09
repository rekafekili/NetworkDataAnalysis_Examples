package client;

import java.io.IOException;
import java.net.*;

public class UdpClientManager {
    private final DatagramSocket mDatagramSocket;
    private InetAddress mServerIp;
    private int mServerPort;

    public UdpClientManager(String ip, int port) throws UnknownHostException, SocketException {
        mServerIp = InetAddress.getByName(ip);
        mServerPort = port;
        mDatagramSocket = new DatagramSocket(port);
    }

    public boolean sendMessage(String message) {
        byte[] buffer = message.getBytes();
        DatagramPacket datagramPacket = new DatagramPacket(buffer, buffer.length, mServerIp, mServerPort);
        try {
            mDatagramSocket.send(datagramPacket);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }
}
