package client;

import java.io.IOException;
import java.net.*;

public class UdpClientManager {
    private final DatagramSocket mDatagramSocket;
    private InetAddress mServerIp;
    private int mServerPort;
    private ServerListener serverListener;

    public UdpClientManager(String ip, int port) throws UnknownHostException, SocketException {
        mServerIp = InetAddress.getByName(ip);
        mServerPort = port;
        mDatagramSocket = new DatagramSocket(3003);
    }

    public void setServerListener(ServerListener listener) {
        serverListener = listener;
    }

    public boolean sendMessage(String message) {
        byte[] buffer = message.getBytes();
        DatagramPacket datagramPacket = new DatagramPacket(buffer, buffer.length, mServerIp, mServerPort);
        try {
            mDatagramSocket.send(datagramPacket);

            buffer = new byte[1024];
            datagramPacket = new DatagramPacket(buffer, buffer.length);

            mDatagramSocket.receive(datagramPacket);
            String receivedMessage = new String(datagramPacket.getData());
            String type = receivedMessage.split("/")[0];
            String serverMessage = receivedMessage.split("/")[1];


        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }
}
