package temparature_humidity_experiment;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.*;
import javax.swing.*;
 
public class Smartphone implements Runnable {
    private static final String DEST_IP_ADDR = "127.0.0.1";
    private static final int DEST_PORT = 7001;
    private static final int MY_PORT = 5555;
    private DatagramSocket socket;     // ��� ���� ����
    private JButton btnT, btnH,btnD ;
    private JLabel labelT, labelH, labelD;
 
    // Ŭ���̾�Ʈ ���α׷� ���� �� ���ʷ� �����ų �޼ҵ�
    public void onCreate() {
        createSocket(); // ���� ����
        setView(); // GUI ȭ�� ����
        setEvent(); // �޽��� ������ �̺�Ʈ ó�� ����. 
        dataReceiver(); // �޽��� ���� ó���� ������
    }
    // UI�� �����Ѵ�.
    public void setView() {
        JFrame frame = new JFrame("�½��� Ȯ�� ����Ʈ��");
        frame.setSize(400, 200);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(null); // ���̾ƿ� ���� setBounds()�� �̿��ؼ� ���� ��ġ�� �׸���
        btnT = new JButton("�µ� Ȯ��");
        btnH = new JButton("���� Ȯ��");
        btnD = new JButton("������ Ȯ��");
        btnT.setBounds(30, 30, 90, 25);
        btnH.setBounds(30, 65, 90, 25);
        btnD.setBounds(30, 100, 90, 25);
        labelT = new JLabel("30��");
        labelH = new JLabel("50%");
        labelD = new JLabel("50%");
        labelT.setBounds(130, 30, 50, 25);
        labelH.setBounds(130, 65, 50, 25);
        labelD.setBounds(130, 100, 50, 25);
        frame.add(btnT);
        frame.add(btnH);
        frame.add(btnD);
        frame.add(labelT);
        frame.add(labelH);
        frame.add(labelD);
        frame.setVisible(true);
    }
 
    private void createSocket() {
        try {
            System.out.println("creating socket with port="+MY_PORT); // ���� ������ port
            socket = new DatagramSocket(MY_PORT);            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
 
    // �̺�Ʈ�� ����Ѵ�
    private void setEvent() {
        btnT.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //��ư�� ������ �� ������ �ڵ� �ۼ�
                sendMsg("TEMP");
            }
        });
        btnH.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendMsg("HMDT");
            }
        });
        btnD.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendMsg("dust");
            }
        });
    }
    private void sendMsg(String msg) {
        try {
            // �۽��� ��Ŷ ��ü ����
            byte[] buf = msg.getBytes();
            DatagramPacket packet = new DatagramPacket(buf, buf.length, 
                    InetAddress.getByName(DEST_IP_ADDR), DEST_PORT);
            socket.send(packet);
            System.out.println("sent Msg : "+msg);
        } catch (Exception e) {
            socket.close();
            e.printStackTrace();
        }        
    }
 
    @Override
    public void run() {
        while( true ) { //������ ������ ���� ��Ŷ(�޽���)�� ��� �޾Ƽ� ó�� 
            //��Ŷ ���ſ� DatagramPacket ��ü ����
            byte[] buf = new byte[1024];
            DatagramPacket packet = new DatagramPacket(buf, buf.length);
            try {
                socket.receive(packet);
                String recvData = new String(packet.getData(), 0, packet.getLength()); // or getData().trim()
                // �½����� ������Ʈ �ؾ���.
                parseMessage(recvData);
            } catch (IOException e) {
                socket.close();
                System.out.println("error at socket.receive(); will break and end thread");
                break;
            }
        }
    }
    
    private void parseMessage(String msg) {
        System.out.println("received Msg: "+msg);
        // temp=30, humidity=40 �̷� ���·� �޽����� ���ŵ� ����
        String kv[] = msg.split("=");
        switch(kv[0]) {
        case "temp":
            labelT.setText(kv[1] + "��");
            break;
        case "humidity":
            labelH.setText(kv[1] + "%");
            break;
        case "dust":
            labelH.setText(kv[1] + "%");
            break;
        }
    }
    public void dataReceiver(){
        Thread th = new Thread(this);
        th.start();
        // ���� Ŭ������ Runnable �������̽��� �����߱� ������
        // this�� �����ؼ� start()�� �����ϸ� run()�޼ҵ尡 ������� ����� 
    }
 
    public static void main(String[] args) {
        Smartphone client = new Smartphone();
        client.onCreate(); //����
    }
}