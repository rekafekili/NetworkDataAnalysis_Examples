package json_fx_udp;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import javax.swing.*;

public class UDPClientGUI {
    private static final String INVITE = "INVITE sip:hello@korea.com SIP/2.0\r\n";
    private static final String TITLE = "UDP 채팅";
    JFrame frame;
    // 메시지 출력 영역
    JTextArea msgout;
    JScrollPane msgScroll;
    // 메시지 입력 영역
    JTextField msgInput;
    JButton sendBtn;
    JPanel msgPanel;
    // Destination IP, port설정용 버튼
    JButton setBtn;
    JTextField destIPInput;
    JTextField destPortInput;
    JTextField srcPortInput;
    // 통신 소켓 변수
    private DatagramSocket socket;
    private String destIP = "127.0.0.1";
    private int destPort = 5060;
    private int srcPort = 5060;

    // 클라이언트 프로그램 구동 시 최초로 실행시킬 메소드
    public void onCreate() {
        createSocket(); // 소켓 생성
        setView(); // GUI 화면 생성
        setEvent(); // 메시지 보내는 이벤트 처리 설정.
        dataReceiver(); // 메시지 수신 처리할 쓰레드
    }
    // UI를 구성한다.
    public void setView() {
        frame = new JFrame(TITLE);
        frame.setSize(400, 300);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        msgout = new JTextArea("UDP 채팅에 오신걸 환영합니다\n", 10, 40);
        msgout.setLineWrap(true);
        msgout.setEditable(false);
        msgScroll = new JScrollPane(msgout,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        msgInput = new JTextField();
        sendBtn = new JButton("전송");

        msgPanel = new JPanel(new BorderLayout());
        msgPanel.add(msgInput, BorderLayout.CENTER);
        msgPanel.add(sendBtn, BorderLayout.EAST);

        JPanel destPanel = new JPanel();
        JLabel destIPLabel = new JLabel("dest IP:");
        JLabel destPortLabel = new JLabel("dest port:");
        destIPInput = new JTextField(destIP);
        destPortInput = new JTextField(""+destPort);

        JLabel srcPortLabel = new JLabel("src port:");
        srcPortInput = new JTextField(""+srcPort);
        setBtn = new JButton("설정");
        destPanel.add(destIPLabel);
        destPanel.add(destIPInput);
        destPanel.add(destPortLabel);
        destPanel.add(destPortInput);
        destPanel.add(srcPortLabel);
        destPanel.add(srcPortInput);
        destPanel.add(setBtn);

        frame.add(destPanel, BorderLayout.NORTH);
        frame.add(msgScroll, BorderLayout.CENTER);
        frame.add(msgPanel, BorderLayout.SOUTH);
        frame.pack();
        frame.setVisible(true);
        msgInput.requestFocus();
    }

    private void createSocket() {
        try {
            System.out.println("creating socket with port="+srcPort);
            socket = new DatagramSocket(srcPort);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 이벤트를 등록한다
    private void setEvent() {

        setBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                srcPort = Integer.valueOf(srcPortInput.getText());
                System.out.println("입력된 port=" + srcPort);
                if( socket==null ) {
                    createSocket();// 소켓 생성;
                    dataReceiver();; // 쓰레드 시작
                } else {
                    System.out.println("현재소켓의 port=" + socket.getLocalPort());
                }
                if( srcPort!=socket.getLocalPort() ) {
                    socket.close();
                    createSocket();// 수신 포트 변경으로인해 소켓 다시 생성;
                    dataReceiver();; // 쓰레드 다시 시작
                }
            }
        });

        class MyActionListener implements ActionListener{
            @Override
            public void actionPerformed(ActionEvent e) {
                sendMsg();
            }
        }
        msgInput.addActionListener(new MyActionListener());
        sendBtn.addActionListener(new MyActionListener());
    }

    private void sendMsg() {
        String msg = msgInput.getText();
        if( msg.equals("invite") ) msg=INVITE;
        msgout.append("Me:"+msg+"\n"); // 보낸 메시지 표시
        msgInput.setText("");
        msgout.setCaretPosition(msgout.getDocument().getLength());
        msgInput.requestFocus();

        try {
            // 송신할 패킷 객체 생성
            byte[] buf = msg.getBytes();
            DatagramPacket packet = new DatagramPacket(buf, buf.length,
                    InetAddress.getByName(destIPInput.getText()),
                    Integer.valueOf(destPortInput.getText()));
            socket.send(packet);
            System.out.println("sent Msg : "+msg);
        } catch (Exception e) {
            socket.close();
            e.printStackTrace();
        }
    }

    public void dataReceiver(){
        new Thread() {
            public void run() {
                while( true ) { //서버가 나에게 보낸 문자 한 줄을 계속 받아서 처리
                    //패킷 수신용 DatagramPacket 객체 생성
                    byte[] buf = new byte[1024];
                    DatagramPacket packet = new DatagramPacket(buf, buf.length);
                    try {
                        socket.receive(packet);
                        String recvData = new String(packet.getData(), 0, packet.getLength()); // or getData().trim()
                        System.out.println("received Msg: "+recvData);
                        msgout.append(recvData+"\n");
                        msgout.setCaretPosition(msgout.getDocument().getLength());
                    } catch (IOException e) {
                        socket.close();
                        System.out.println("error at socket.receive(); will break and end thread");
                        break;
                    }
                }
            };
        }.start();
    }

    public static void main(String[] args) {
        UDPClientGUI client = new UDPClientGUI();
        client.onCreate(); //시작
    }
}
