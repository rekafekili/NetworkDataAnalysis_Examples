package temparature_humidity_experiment;

import java.awt.GridLayout;
import java.io.IOException;
import java.net.*;
import javax.swing.*;
import javax.swing.event.*;
 
public class Device extends JFrame implements Runnable {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final String DEST_IP_ADDR = "127.0.0.1";
    private static final int DEST_PORT = 5555;
    private static final int MY_PORT = 7001;
    private DatagramSocket socket;     // ��� ���� ����
    private JTextField temp, humidity, dust;
 
    // Ŭ���̾�Ʈ ���α׷� ���� �� ���ʷ� �����ų �޼ҵ�
    public void onCreate() {
        createSocket(); // ���� ����
        setView(); // GUI ȭ�� ����
        dataReceiver(); // �޽��� ���� ó���� ������        
    }
    // UI�� �����Ѵ�.
    public void setView() {
        setTitle("�½��� ���� IoT ��ġ");
        setLocationRelativeTo(null); // ȭ�� �߾ӿ� ������ ��ġ
        setSize(300, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        JPanel panel = new JPanel(new GridLayout(3,4));
        panel.add( new JLabel("���� �µ�(��):") ) ;
        temp = new JTextField("30");
        panel.add( temp );
        JSlider tempSlider = new JSlider(0,100,30);
        panel.add( tempSlider );
        
        panel.add( new JLabel("���� ����(%):") ) ;
        humidity = new JTextField("50");
        panel.add( humidity );
        JSlider humiditySlider = new JSlider(0,100,30);
        panel.add( humiditySlider );
        
        panel.add( new JLabel("���� ��(%):") ) ;
        humidity = new JTextField("40");
        panel.add( dust );
        JSlider dustSlider = new JSlider(0,100,30);
        panel.add( dustSlider );
        
        tempSlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                temp.setText(""+tempSlider.getValue());
            }
        });
        humiditySlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                humidity.setText(""+humiditySlider.getValue());
            }
        });
        
        humiditySlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
            	dust.setText(""+dustSlider.getValue());
            }
        });
        
        // �����̴� �ʱⰪ ����
        tempSlider.setValue(30); 
        humiditySlider.setValue(50);
        dustSlider.setValue(40);
 
        add(panel);        
        setVisible(true);
    }
 
    private void createSocket() {
        try {
            System.out.println("creating socket with port="+MY_PORT); // ���� ������ port
            socket = new DatagramSocket(MY_PORT);            
        } catch (Exception e) {
            e.printStackTrace();
        }
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
        // TEMP, HMDT �̷� ���·� �޽����� ���ŵ� ����
        switch(msg) {
        case "TEMP":
            sendMsg( "temp=" + temp.getText() );
            break;
        case "HMDT":
            sendMsg( "humidity=" + humidity.getText() );
            break;
        case "density":
            sendMsg( "dust=" + dust.getText() );
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
        Device iot1 = new Device();
        iot1.onCreate(); //����
    }
}
