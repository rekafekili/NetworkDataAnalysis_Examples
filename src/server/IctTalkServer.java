package server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
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
                InetAddress clientIp = datagramPacket.getAddress();
                int clientPort = datagramPacket.getPort();
                String receivedMessage = new String(datagramPacket.getData());

                System.out.println(receivedMessage);

                String command = receivedMessage.split("/")[0];
                String message = receivedMessage.split("/")[1];
                String resultMessage = "";

                switch(command) {
                    case "register":
                        String userId = message.split(",")[0];
                        String userPw = message.split(",")[1];
                        if(!userHashMap.containsKey(userId)) {
                            userHashMap.put(userId, userPw);
                            resultMessage = "OK/Registered";
                        } else {
                            resultMessage = "ERROR/ID Already Exist";
                        }
                        break;
                    case "signin":

                        break;
                }

                byte[] bytes = resultMessage.getBytes();
                datagramPacket = new DatagramPacket(bytes, bytes.length, clientIp, clientPort);
                datagramSocket.send(datagramPacket);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new IctTalkServer(3000);
    }
}
