package temparature_humidity_examples;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.*;
import javax.swing.*;

public class Smartphone implements Runnable {
    private static final String DEST_IP_ADDR = "127.0.0.1";
    private static final int DEST_PORT = 7001;
    private static final int MY_PORT = 5555;
    private DatagramSocket socket;     // 통신 소켓 변수
    private JButton btnT, btnH;
    private JLabel labelT, labelH;

    // 클라이언트 프로그램 구동 시 최초로 실행시킬 메소드
    public void onCreate() {
        createSocket(); // 소켓 생성
        setView(); // GUI 화면 생성
        setEvent(); // 메시지 보내는 이벤트 처리 설정.
        dataReceiver(); // 메시지 수신 처리할 쓰레드
    }

    // UI를 구성한다.
    public void setView() {
        JFrame frame = new JFrame("온습도 확인 스마트폰");
        frame.setSize(400, 150);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(null); // 레이아웃 없이 setBounds()를 이용해서 절대 위치에 그리자
        frame.setResizable(false);
        btnT = new JButton("온도 확인");
        btnH = new JButton("습도 확인");
        btnT.setBounds(30, 30, 90, 25);
        btnH.setBounds(30, 65, 90, 25);
        labelT = new JLabel("30℃");
        labelH = new JLabel("50%");
        labelT.setBounds(130, 30, 50, 25);
        labelH.setBounds(130, 65, 50, 25);
        frame.add(btnT);
        frame.add(btnH);
        frame.add(labelT);
        frame.add(labelH);
        frame.setVisible(true);
    }

    private void createSocket() {
        try {
            System.out.println("creating socket with port=" + MY_PORT); // 내가 수신할 port
            socket = new DatagramSocket(MY_PORT);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 이벤트를 등록한다
    private void setEvent() {
        btnT.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //버튼을 눌렀을 때 실행할 코드 작성
                sendMsg("TEMP");
            }
        });
        btnH.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendMsg("HMDT");
            }
        });
    }

    private void sendMsg(String msg) {
        try {
            // 송신할 패킷 객체 생성
            byte[] buf = msg.getBytes();
            DatagramPacket packet = new DatagramPacket(buf, buf.length,
                    InetAddress.getByName(DEST_IP_ADDR), DEST_PORT);
            socket.send(packet);
            System.out.println("sent Msg : " + msg);
        } catch (Exception e) {
            socket.close();
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        while (true) { //누군가 나에게 보낸 패킷(메시지)를 계속 받아서 처리
            //패킷 수신용 DatagramPacket 객체 생성
            byte[] buf = new byte[1024];
            DatagramPacket packet = new DatagramPacket(buf, buf.length);
            try {
                socket.receive(packet);
                String recvData = new String(packet.getData(), 0, packet.getLength()); // or getData().trim()
                // 온습도를 업데이트 해야함.
                parseMessage(recvData);
            } catch (IOException e) {
                socket.close();
                System.out.println("error at socket.receive(); will break and end thread");
                break;
            }
        }
    }

    private void parseMessage(String msg) {
        System.out.println("received Msg: " + msg);
        // temp=30, humidity=40 이런 형태로 메시지가 수신될 것임
        String kv[] = msg.split("=");
        switch (kv[0]) {
            case "temp":
                labelT.setText(kv[1] + "℃");
                break;
            case "humidity":
                labelH.setText(kv[1] + "%");
                break;
            case "auto":
                // 주기적으로 온습도 정보를 받는 경우
                String splitTemp = kv[1].split(",")[0];
                String splitHumidity = kv[1].split(",")[1];
                labelT.setText(splitTemp + "℃");
                labelH.setText(splitHumidity + "%");
        }
    }

    public void dataReceiver() {
        Thread th = new Thread(this);
        th.start();
        // 현재 클래스가 Runnable 인터페이스를 구현했기 때문에
        // this를 전달해서 start()를 실행하면 run()메소드가 쓰레드로 실행됨
    }

    public static void main(String[] args) {
        Smartphone client = new Smartphone();
        client.onCreate(); //시작
    }
}