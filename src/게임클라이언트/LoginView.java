package ����Ŭ���̾�Ʈ;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.StringTokenizer;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

public class LoginView extends JFrame {
	private JPanel contentPane;
	private JLabel idLabel;
	private JTextField idField;
	private JButton sendButton;
	private JLabel msgLabel;
	
	private String id;
	
	private Socket socket; // �������
	private  InputStream is;
	private  OutputStream os;
	private  DataInputStream dis;
	private  DataOutputStream dos;
	
	private ImageIcon backGround = new ImageIcon("img/LoginBackground2.jpg");
	private Image backGroundImg = backGround.getImage();
	
	public LoginView(){
		init();
		start();
	}
	
	public void network() {
		// ������ ����
		try {
			socket = new Socket(MainView.IP, MainView.PORT);
			if (socket != null) // socket�� null���� �ƴҶ� ��! ����Ǿ�����
			{	
				Connection(); // ���� �޼ҵ带 ȣ��
			}
		} catch (UnknownHostException e) {

		} catch (IOException e) {
			//textArea.append("���� ���� ����!!\n");
			msgLabel.setText("���� ���� ����!!\n");
		}

	}
	public void Connection() { // ���� ���� �޼ҵ� ����κ�
		try { // ��Ʈ�� ����
			is = socket.getInputStream();
			dis = new DataInputStream(is);
			os = socket.getOutputStream();
			dos = new DataOutputStream(os);
			//System.out.println("is : " + is + ", dis : " + dis +", os : " + os + ", dos : " +dos);
		} catch (IOException e) {
			//textArea.append("��Ʈ�� ���� ����!!\n");
			msgLabel.setText("��Ʈ�� ���� ����!!\n");
		}
		String send = Protocol.LOGIN;
		send = send + id;
		send = send + "$";
		send_Message(send); // ���������� ����Ǹ� ���� id�� ����
		System.out.println("Login ::" + send );
		Thread th = new Thread(new Runnable() { // �����带 ������ �����κ��� �޼����� ����
			@SuppressWarnings("null")
			@Override
			public void run() {
				while (true) {
					try {
						System.out.println("11");
						byte[] b = new byte[128];
						dis.read(b);
						String msg = new String(b);
						msg = msg.trim();
						String Command;
						String temp = msg;
						String chatMsg;
						if(!temp.equals("")){
							StringTokenizer s = new StringTokenizer(temp);
							Command = s.nextToken("$");
							String SubCommand = s.nextToken("\0");
						}
						else
							Command = "X";
						if(Command.equals("LOGINSUCCESS")){
							
							
							MainView mv = new MainView(id, is, os,dis, dos, socket);
							setVisible(false);
							return;
						}
						if(Command.equals("IDERROR")){
							//"IDERROR$"+Nickname
							msgLabel.setText("ID ����!!");
							idField.setText("");
							return;		// ���̵� ��й�ȣ Ʋ���� ������ ����
						}
						if(Command.equals("ALREADY_ACCESS$")){
							msgLabel.setText("�̹� ���� ���� ���̵� �Դϴ�.");
							idField.setText("");
							return;
						}
						
					} catch (IOException e) {
						//textArea.append("�޼��� ���� ����!!\n");
						 msgLabel.setText("�޼��� ���� ����!!\n");
						// ������ ���� ��ſ� ������ ������ ��� ������ �ݴ´�
						try {
							os.close();
							is.close();
							dos.close();
							dis.close();
							socket.close();
							break; // ���� �߻��ϸ� while�� ����
						} catch (IOException e1) {
						}
					}
				} // while�� ��
			}// run�޼ҵ� ��
		});
		th.start();
	}
	
	public void send_Message(String str) { // ������ �޼����� ������ �޼ҵ�
		try {
			byte[] bb;
			bb = str.getBytes();
			dos.write(bb); //.writeUTF(str);
		} catch (IOException e) {
			//textArea.append("�޼��� �۽� ����!!\n");
			msgLabel.setText("�޼��� �۽� ����!!\n");
		}
	}
	
	public void init(){
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, MainView.WIDTH, MainView.HEIGHT);
		contentPane = new JPanel(){
			public void paintComponent(Graphics g){
				g.drawImage(backGroundImg, 0, 0, null);
				setOpaque(false);
				super.paintComponent(g);
			}
		};

		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		idLabel = new JLabel("ID ");
		idLabel.setBounds(350,550,50,30);
		contentPane.add(idLabel);
		
		idField = new JTextField();
		idField.setBounds(400, 550, 100, 30);
		contentPane.add(idField);
		
		sendButton = new JButton("�α���");
		sendButton.setBounds(520,550,80,30);
		contentPane.add(sendButton);
		
		msgLabel = new JLabel();
		msgLabel.setBounds(400,590,150,30);
		contentPane.add(msgLabel);

		
		setVisible(true);
	}
	
	public void start() { // �׼��̺�Ʈ ���� �޼ҵ�
		Myaction action = new Myaction();
		sendButton.addActionListener(action); // ����Ŭ������ �׼� �����ʸ� ��ӹ��� Ŭ������
		idField.addActionListener(action);
	}
	
	class Myaction implements ActionListener // ����Ŭ������ �׼� �̺�Ʈ ó�� Ŭ����
	{
		@Override
		public void actionPerformed(ActionEvent e) {

			// �׼� �̺�Ʈ�� sendBtn�϶� �Ǵ� textField ���� Enter key ġ��
			if (e.getSource() == sendButton || e.getSource() == idField) 
			{
				setID(idField.getText());
				network();			
			}
					
		}
	}
	
	public String getID(){
		return id;
	}
	public void setID(String id){
		this.id = id;
	}
	public Socket getSocket(){
		return socket;
	}
	
}
