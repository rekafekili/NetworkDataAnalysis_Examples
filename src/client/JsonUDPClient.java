package client;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;

import org.json.simple.*;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.net.*;

/**
 * UDP 채팅 프로그램 응용 예제 - JSON 활용 
 * 변경점 
 *  - invite 기능을 삭제 
 *  - 람다식(Lambda) 적용 
 *  - JSON 형식으로 메시지 송,수신
 * 
 * @author 2014161141 S.Y.Cho
 *
 */
public class JsonUDPClient {
	private static final String TITLE = "UDP Chat using JSON";
	JFrame frame;

	// 메시지 출력 영역
	JTextArea msgout;
	JScrollPane msgScroll;

	// 메시지 입력 영역
	JTextField msgInput;
	JButton sendBtn;
	JPanel msgPanel;

	// 목적지 IP, Port 설정
	JButton setBtn;
	JTextField destIPInput;
	JTextField destPortInput;
	JTextField srcPortInput;

	// 통신 소켓 변수
	private DatagramSocket socket;
	private String destIP = "127.0.0.1"; // 목적지 IP 초기값
	private int destPort = 5060; // 목적지 Port 초기값
	private int srcPort = 5060; // 출발지 Port 초기값

	/**
	 * 클라이언트 프로그램 구동시 최초로 실행시킬 메소드
	 */
	private void onCreate() {
		createSocket(); // 소켓 생성
		setView(); // GUI 화면 생성
		setEvent(); // 메시지 보내는 이벤트 처리 설정
		dataReceiver(); // 메시지 수신 처리할 쓰레드
	}

	/**
	 * UI를 그리는 메소드
	 */
	private void setView() {
		frame = new JFrame(TITLE);
		frame.setSize(400, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		msgout = new JTextArea("UDP 채팅에 오신걸 환영합니다\n", 10, 40);
		msgout.setLineWrap(true);
		msgout.setEditable(false);
		msgScroll = new JScrollPane(msgout, 
				JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

		msgInput = new JTextField();
		sendBtn = new JButton("SEND");

		msgPanel = new JPanel(new BorderLayout());
		msgPanel.add(msgInput, BorderLayout.CENTER);
		msgPanel.add(sendBtn, BorderLayout.EAST);

		JPanel destPanel = new JPanel();
		JLabel destIPLabel = new JLabel("dest IP:");
		JLabel destPortLabel = new JLabel("dest port:");
		destIPInput = new JTextField(destIP);
		destPortInput = new JTextField("" + destPort);

		JLabel srcPortLabel = new JLabel("src port:");
		srcPortInput = new JTextField("" + srcPort);
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

	/**
	 * DatagramSocket 생성 메소드
	 */
	private void createSocket() {
		try {
			System.out.println("Creating Socket with Port = " + srcPort);
			socket = new DatagramSocket(srcPort);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Button의 이벤트를 등록하는 메소드
	 */
	private void setEvent() {
		setBtn.addActionListener(event -> {
			srcPort = Integer.parseInt(srcPortInput.getText());
			System.out.println("Entered Port = " + srcPort);
			if (socket == null) {
				createSocket();
				dataReceiver();
			} else {
				System.out.println("Now Socket Port = " + socket.getLocalPort());
			}

			if (srcPort != socket.getLocalPort()) {
				socket.close();
				createSocket();
				dataReceiver();
			}
		});

		msgInput.addActionListener(enterEvent -> sendMsg());
		sendBtn.addActionListener(event -> sendMsg());
	}

	/**
	 * 메시지를 전송하는 메소드
	 */
	private void sendMsg() {
		// UI 위에 있는 데이터를 변수로 저장
		String destIP = destIPInput.getText();
		int destPort = Integer.valueOf(destPortInput.getText());
		String msg = msgInput.getText();
		
		/**
		 * JSON 형식으로 메시지 생성
		 */
		JSONObject object = new JSONObject();
		object.put("DEST_IP", destIP);
		object.put("DEST_PORT", destPort);
		object.put("MESSAGE", msg);
		
		msgout.append("Me:" + msg + "\n"); // 보낸 메시지 표시
		msgInput.setText("");
		msgout.setCaretPosition(msgout.getDocument().getLength());
		msgInput.requestFocus();
		
		try {
			// 송신할 패킷 객체 생성
			byte[] buffer = object.toString().getBytes();
			DatagramPacket packet = new DatagramPacket(buffer, buffer.length, InetAddress.getByName(destIP), destPort);
			socket.send(packet);
			System.out.println("Sent MSG : " + object.toJSONString());
		} catch(Exception e) {
			socket.close();
			e.printStackTrace();
		}
	}
	
	/**
	 * 메시지를 수신하는 메소드
	 */
	private void dataReceiver() {
		new Thread() {
			public void run() {
				while(true) { // 서버가 나에게 보낸 문자 한 줄을 계속 받아서 처리
					// 패킷 수신용 DatagramPacket 객체 생성
					byte[] buffer = new byte[1024];
					DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
					try {
						socket.receive(packet);
						String recvData = new String(packet.getData(), 0, packet.getLength());
						System.out.println("Received MSG : " + recvData);
						
						/**
						 * 수신한 JSON 문자열에서 메시지 추출
						 */
						JSONParser jsonParser = new JSONParser();
						JSONObject object = (JSONObject)jsonParser.parse(recvData);
						recvData = (String)object.get("MESSAGE");
						
						msgout.append(recvData + "\n");
						msgout.setCaretPosition(msgout.getDocument().getLength());
					} catch(IOException e) {
						socket.close();
						System.out.println("Error at socket.receive(); will break and end thred");
						break;
					} catch(ParseException e) {
						// JSON 파싱 과정에서 에러가 날 경우 catch문
						System.out.println("Error at JSONParser!");
						e.printStackTrace();
					}
					
				}
			}
		}.start();
	}

	public static void main(String[] args) {
		JsonUDPClient client = new JsonUDPClient();
		client.onCreate();
	}
}
