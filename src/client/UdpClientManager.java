package client;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class UdpClientManager {
    public UdpClientManager(String ip, int port) {
        try {
            InetAddress inetAddress = InetAddress.getByName(ip);
            DatagramSocket ds = new DatagramSocket(port);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
