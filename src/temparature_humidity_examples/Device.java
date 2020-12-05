package temparature_humidity_examples;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.*;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.*;
import javax.swing.event.*;

public class Device extends JFrame implements Runnable {
    private static final String DEST_IP_ADDR = "127.0.0.1";
    private static final int DEST_PORT = 5555;
    private static final int MY_PORT = 7001;
    private DatagramSocket socket;     // 통신 소켓 변수
    private JTextField temp, humidity;

    // 클라이언트 프로그램 구동 시 최초로 실행시킬 메소드
    public void onCreate() {
        createSocket(); // 소켓 생성
        setView(); // GUI 화면 생성
        dataReceiver(); // 메시지 수신 처리할 쓰레드
    }

    // UI를 구성한다.
    public void setView() {
        setTitle("온습도 측정 IoT 장치");
        setLocationRelativeTo(null); // 화면 중앙에 윈도우 배치
        setResizable(false);
        setSize(300, 100);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel(new GridLayout(3, 3));
        panel.add(new JLabel("현재 온도(℃):"));
        temp = new JTextField("30");
        panel.add(temp);
        JSlider tempSlider = new JSlider(0, 100, 30);
        panel.add(tempSlider);

        panel.add(new JLabel("현재 습도(%):"));
        humidity = new JTextField("50");
        panel.add(humidity);
        JSlider humiditySlider = new JSlider(0, 100, 30);
        panel.add(humiditySlider);


        // 자동 전송 주기를 입력하는 텍스트필드와 설정 버튼 추가
        panel.add(new JLabel("전송 주기(ms):"));
        JTextField periodField = new JTextField("2000");
        panel.add(periodField);
        JButton periodSetButton = new JButton("설정");
        panel.add(periodSetButton);
        periodSetButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                long periodValue = Long.parseLong(periodField.getText());

                Timer sendTimer = new Timer();
                TimerTask sendTimerTask = new TimerTask() {
                    @Override
                    public void run() {

                    }
                };

                sendTimer.schedule(sendTimerTask, periodValue, periodValue);
            }
        });

        tempSlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                temp.setText("" + tempSlider.getValue());
            }
        });

        humiditySlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                humidity.setText("" + humiditySlider.getValue());
            }
        });

        // 슬라이더 초기값 설정
        tempSlider.setValue(30);
        humiditySlider.setValue(50);

        add(panel);
        setVisible(true);
    }

    private void createSocket() {
        try {
            System.out.println("creating socket with port=" + MY_PORT); // 내가 수신할 port
            socket = new DatagramSocket(MY_PORT);
        } catch (Exception e) {
            e.printStackTrace();
        }
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
            // 패킷 수신용 DatagramPacket 객체 생성
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
        // TEMP, HMDT 이런 형태로 메시지가 수신될 것임
        switch (msg) {
            case "TEMP":
                sendMsg("temp=" + temp.getText());
                break;
            case "HMDT":
                sendMsg("humidity=" + humidity.getText());
                break;
        }
    }

    public void dataReceiver() {
        Thread th = new Thread(this);
        th.start();
        // 현재 클래스가 Runnable 인터페이스를 구현했기 때문에
        // this를 전달해서 start()를 실행하면 run()메소드가 쓰레드로 실행됨
    }

    public static void main(String[] args) {
        Device iot1 = new Device();
        iot1.onCreate(); //시작
    }
}
