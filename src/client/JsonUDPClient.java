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
 * UDP ä�� ���α׷� ���� ���� - JSON Ȱ�� 
 * ������ 
 *  - invite ����� ���� 
 *  - ���ٽ�(Lambda) ���� 
 *  - JSON �������� �޽��� ��,����
 * 
 * @author 2014161141 S.Y.Cho
 *
 */
public class JsonUDPClient {
	private static final String TITLE = "UDP Chat using JSON";
	JFrame frame;

	// �޽��� ��� ����
	JTextArea msgout;
	JScrollPane msgScroll;

	// �޽��� �Է� ����
	JTextField msgInput;
	JButton sendBtn;
	JPanel msgPanel;

	// ������ IP, Port ����
	JButton setBtn;
	JTextField destIPInput;
	JTextField destPortInput;
	JTextField srcPortInput;

	// ��� ���� ����
	private DatagramSocket socket;
	private String destIP = "127.0.0.1"; // ������ IP �ʱⰪ
	private int destPort = 5060; // ������ Port �ʱⰪ
	private int srcPort = 5060; // ����� Port �ʱⰪ

	/**
	 * Ŭ���̾�Ʈ ���α׷� ������ ���ʷ� �����ų �޼ҵ�
	 */
	private void onCreate() {
		createSocket(); // ���� ����
		setView(); // GUI ȭ�� ����
		setEvent(); // �޽��� ������ �̺�Ʈ ó�� ����
		dataReceiver(); // �޽��� ���� ó���� ������
	}

	/**
	 * UI�� �׸��� �޼ҵ�
	 */
	private void setView() {
		frame = new JFrame(TITLE);
		frame.setSize(400, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		msgout = new JTextArea("UDP ä�ÿ� ���Ű� ȯ���մϴ�\n", 10, 40);
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
		setBtn = new JButton("����");
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
	 * DatagramSocket ���� �޼ҵ�
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
	 * Button�� �̺�Ʈ�� ����ϴ� �޼ҵ�
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
	 * �޽����� �����ϴ� �޼ҵ�
	 */
	private void sendMsg() {
		// UI ���� �ִ� �����͸� ������ ����
		String destIP = destIPInput.getText();
		int destPort = Integer.valueOf(destPortInput.getText());
		String msg = msgInput.getText();
		
		/**
		 * JSON �������� �޽��� ����
		 */
		JSONObject object = new JSONObject();
		object.put("DEST_IP", destIP);
		object.put("DEST_PORT", destPort);
		object.put("MESSAGE", msg);
		
		msgout.append("Me:" + msg + "\n"); // ���� �޽��� ǥ��
		msgInput.setText("");
		msgout.setCaretPosition(msgout.getDocument().getLength());
		msgInput.requestFocus();
		
		try {
			// �۽��� ��Ŷ ��ü ����
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
	 * �޽����� �����ϴ� �޼ҵ�
	 */
	private void dataReceiver() {
		new Thread() {
			public void run() {
				while(true) { // ������ ������ ���� ���� �� ���� ��� �޾Ƽ� ó��
					// ��Ŷ ���ſ� DatagramPacket ��ü ����
					byte[] buffer = new byte[1024];
					DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
					try {
						socket.receive(packet);
						String recvData = new String(packet.getData(), 0, packet.getLength());
						System.out.println("Received MSG : " + recvData);
						
						/**
						 * ������ JSON ���ڿ����� �޽��� ����
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
						// JSON �Ľ� �������� ������ �� ��� catch��
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
