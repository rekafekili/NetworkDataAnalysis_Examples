package pyh_question;

import javax.swing.*;
import javax.swing.border.LineBorder;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;


public class projectClient1 {
    private static final String DEST_IP_ADDR = "127.0.0.1";
    private static final int DEST_PORT = 7001;
    private static final int MY_PORT = 5555;
    private DatagramSocket socket;     // 통신 소켓 변수
	private JButton btn1, btn2;
	private JFrame frame;
	private JPanel msgPanel, panel1, panel2;
	private JTextField pNum;
	
	private void onCreate() {
		createSocket(); // 소켓 생성
	    setFirstView(); // GUI 화면 생성
	    dataReceiver(); // 메시지 수신 처리할 쓰레드
	}

	private void dataReceiver() {
		
	}


	protected void setResView() { //예약자 GUI 화면 
		frame = new JFrame("예약자"); 
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);//프레임 완전하게 끄기
		frame.setSize(300,200);
		frame.setVisible(true);
		
		frame.setLayout(new GridLayout(5,2)); //5줄로 만들어주기
		frame.add(new JLabel("   ")); //첫 번째 칸 공백 
			        
		panel1 = new JPanel();
		panel1.add(new JLabel("   인원 수 :"));
		pNum=new JTextField(18);
		panel1.add(pNum);
		frame.add(panel1);
		
		panel2 = new JPanel();
		panel2.add(new JLabel("전화번호 :"));
		pNum=new JTextField(18);
		panel2.add(pNum);
		frame.add(panel2);
		
		frame.add(new JLabel(" ")); //세 번째 칸 공백
		JButton button = new JButton("확인");
		frame.add(button);
		frame.setVisible(true);
		
		button.addActionListener(new ActionListener() { //접수자 버튼을 눌렀을 때
            @Override
            public void actionPerformed(ActionEvent e) {
            	sendMsg(pNum.getText().toString()); //접수자 GUI 나옴 
            }

        });
		
	}

	private void setAcceptView() { //접수자 화면 GUI
		frame = new JFrame("접수자");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);//프레임 완전하게 끄기
		frame.setSize(300,200);
		frame.setVisible(true);
			        
			       //Layout 배치설정자
		frame.setLayout(new GridLayout(5,2)); //5줄로 만들어주기
		frame.add(new JLabel("   ")); //첫 번째 칸 공백 
			        
		panel1 = new JPanel();
		panel1.add(new JLabel("인원 수 :"));
		pNum=new JTextField(20);
		panel1.add(pNum);
		frame.add(panel1);
		
		frame.add(new JLabel(" ")); //세 번째 칸 공백
		JButton button = new JButton("확인");
		frame.add(button);
		frame.setVisible(true);

		button.addActionListener(new ActionListener() { //접수자 버튼을 눌렀을 때
            @Override
            public void actionPerformed(ActionEvent e) {
            	sendMsg(pNum.getText().toString()); //접수자 GUI 나옴 
            }

        });
		
	}

	private void sendMsg(String msg) {
        try {
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
	
	private void setFirstView() { //초기화면 GUI
		frame = new JFrame("입장");
		frame.setResizable(false); //창 크기 조정 못 하게 함
        frame.setLayout(null); //레이아웃 없이 setBounds()를 이용해서 절대 위치에 그리자
		frame.setSize(450, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		btn1 = new JButton("접수자");
		btn2 = new JButton("예약자");
 
		btn1.setBounds(0, 0, 222, 272); //button 크기 설정 
		btn2.setBounds(222, 0, 222, 272); //button 크기 설정 
		
		frame.add(btn1);
		frame.add(btn2);
		frame.setVisible(true);	
		
		btn1.addActionListener(new ActionListener() { //접수자 버튼을 눌렀을 때
            @Override
            public void actionPerformed(ActionEvent e) {
            	setAcceptView(); //접수자 GUI 나옴 
            }
        });
		btn2.addActionListener(new ActionListener() { //예약자 버튼 눌렀을 때
            @Override
            public void actionPerformed(ActionEvent e) {
            	setResView(); //예약자 GUI 나옴 
            }
        });
		
	}

    private void createSocket() {
        try {
            System.out.println("creating socket with port="+MY_PORT); // 내가 수신할 port
            socket = new DatagramSocket(MY_PORT);            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public static void main(String[] args) {
        projectClient1 client = new projectClient1();
        client.onCreate(); //시작
    }
    

}
