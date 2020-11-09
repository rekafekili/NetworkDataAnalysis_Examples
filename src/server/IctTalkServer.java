package server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.HashMap;

public class IctTalkServer {
    private HashMap<String, String> userHashMap = new HashMap<>();
    public IctTalkServer(int port) {
        try {
            DatagramSocket datagramSocket = new DatagramSocket(port);
            while(true) {
                byte[] receiveBuffer = new byte[1024];
                DatagramPacket datagramPacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);
                datagramSocket.receive(datagramPacket);
                String receivedMessage = new String(datagramPacket.getData());

                System.out.println(receivedMessage);

                String command = receivedMessage.split("/")[0];
                String message = receivedMessage.split("/")[1];

                switch(command) {
                    case "register":

                        break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new IctTalkServer(3000);
    }
}
