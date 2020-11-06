package server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class UDPEchoServer {
    public UDPEchoServer(int port) {
        try {
            DatagramSocket ds = new DatagramSocket(port);
            while(true) {
                byte buffer[] = new byte[512];
                DatagramPacket dp = new DatagramPacket(buffer, buffer.length);
                System.out.println("Ready");
                ds.receive(dp);
                String str = new String(dp.getData());
                System.out.println("Received Data : " + str);

                InetAddress ia = dp.getAddress();
                port = dp.getPort();
                System.out.println("Client IP : " + ia + " , Client Port : " + port);
                dp = new DatagramPacket(dp.getData(), dp.getData().length, ia, port);
                ds.send(dp);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new UDPEchoServer(3000);
    }
}
