package 게임클라이언트;

import java.awt.Font;
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
//	private JLabel title;
	private JLabel idLabel;
	private JTextField idField;
	private JButton sendButton;
	private JLabel msgLabel;
	
	private String id;
	
	private Socket socket; // 연결소켓
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
		// 서버에 접속
		try {
			socket = new Socket(MainView.IP, MainView.PORT);
			if (socket != null) // socket이 null값이 아닐때 즉! 연결되었을때
			{	
				Connection(); // 연결 메소드를 호출
			}
		} catch (UnknownHostException e) {

		} catch (IOException e) {
			//textArea.append("소켓 접속 에러!!\n");
			msgLabel.setText("소켓 접속 에러!!\n");
		}

	}
	public void Connection() { // 실직 적인 메소드 연결부분
		try { // 스트림 설정
			is = socket.getInputStream();
			dis = new DataInputStream(is);
			os = socket.getOutputStream();
			dos = new DataOutputStream(os);
			//System.out.println("is : " + is + ", dis : " + dis +", os : " + os + ", dos : " +dos);
		} catch (IOException e) {
			//textArea.append("스트림 설정 에러!!\n");
			msgLabel.setText("스트림 설정 에러!!\n");
		}
		String send = Protocol.LOGIN;
		send = send + id;
		send = send + "$";
		send_Message(send); // 정상적으로 연결되면 나의 id를 전송
		System.out.println("Login ::" + send );
		Thread th = new Thread(new Runnable() { // 스레드를 돌려서 서버로부터 메세지를 수신
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
							msgLabel.setText("ID 오류!!");
							idField.setText("");
							return;		// 아이디 비밀번호 틀리면 쓰레드 종료
						}
						if(Command.equals("ALREADY_ACCESS$")){
							msgLabel.setText("이미 접속 중인 아이디 입니다.");
							idField.setText("");
							return;
						}
						
					} catch (IOException e) {
						//textArea.append("메세지 수신 에러!!\n");
						 msgLabel.setText("메세지 수신 에러!!\n");
						// 서버와 소켓 통신에 문제가 생겼을 경우 소켓을 닫는다
						try {
							os.close();
							is.close();
							dos.close();
							dis.close();
							socket.close();
							break; // 에러 발생하면 while문 종료
						} catch (IOException e1) {
						}
					}
				} // while문 끝
			}// run메소드 끝
		});
		th.start();
	}
	
	public void send_Message(String str) { // 서버로 메세지를 보내는 메소드
		try {
			byte[] bb;
			bb = str.getBytes();
			dos.write(bb); //.writeUTF(str);
		} catch (IOException e) {
			//textArea.append("메세지 송신 에러!!\n");
			msgLabel.setText("메세지 송신 에러!!\n");
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
		
		Font f1 = new Font("맑은 고딕",Font.PLAIN,40);
		
//		title = new JLabel("온라인 쿵쿵따!");
//		title.setBounds(300,100,300,50);
//		title.setFont(f1);
//		contentPane.add(title);
		
		idLabel = new JLabel("ID ");
		idLabel.setBounds(350,550,50,30);
		contentPane.add(idLabel);
		
		idField = new JTextField();
		idField.setBounds(400, 550, 100, 30);
		contentPane.add(idField);
		
		sendButton = new JButton("로그인");
		sendButton.setBounds(520,550,80,30);
		contentPane.add(sendButton);
		
		msgLabel = new JLabel();
		msgLabel.setBounds(400,590,150,30);
		contentPane.add(msgLabel);

		
		setVisible(true);
	}
	
	public void start() { // 액션이벤트 지정 메소드
		Myaction action = new Myaction();
		sendButton.addActionListener(action); // 내부클래스로 액션 리스너를 상속받은 클래스로
		idField.addActionListener(action);
	}
	
	class Myaction implements ActionListener // 내부클래스로 액션 이벤트 처리 클래스
	{
		@Override
		public void actionPerformed(ActionEvent e) {

			// 액션 이벤트가 sendBtn일때 또는 textField 에세 Enter key 치면
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
