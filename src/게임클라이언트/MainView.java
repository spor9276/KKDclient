package 게임클라이언트;

import java.awt.Color;
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
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.border.EmptyBorder;


public class MainView extends JFrame {
	public static final int WIDTH = 900;
	public static final int HEIGHT = 700;
	public static final String IP = "127.0.0.1";
	public static final int PORT = 30000;
	
	private int Master_flag = 0;
	
	private String id;
	private String password;
	
	private Socket socket; // 연결소켓
	private InputStream is;
	private OutputStream os;
	private DataInputStream dis;
	private DataOutputStream dos;
	
	private int first_flag = 1;
	
	private JPanel contentPane;
	private JTextField textField; // 보낼 메세지 쓰는곳
	JButton sendBtn; // 전송버튼
	JButton startBtn;	//게임 시작 버튼
	JButton readyBtn;	//게임 준비 버튼
	
	// JTextArea textArea; // 수신된 메세지를 나타낼 변수
	JTextPane textArea; // 이모티콘이나 이미지 보여주기 위해서는 JtextPane을 사용한다.

	private ImageIcon userCharacter[] = {new ImageIcon("img/jayZ.png"), new ImageIcon("img/muzi.png"), new ImageIcon("img/neo.png"), new ImageIcon("img/prodo.png")};
	private JLabel userImg[] = new JLabel[4];
	
	public void append_Message(String str) {		
	//	append_Icon(icon1);		
		int len = textArea.getDocument().getLength(); // same value as
        textArea.setCaretPosition(len); // place caret at the end (with no selection)
        textArea.setCaretColor(Color.BLACK);
        textArea.replaceSelection(str); // there is no selection, so inserts at caret
	}
	
	public void append_My_Message(String str){
		int len = textArea.getDocument().getLength(); // same value as
        textArea.setCaretPosition(len); // place caret at the end (with no selection)
        textArea.setCaretColor(Color.YELLOW);
        textArea.replaceSelection(str); // there is no selection, so inserts at caret
		
	}

	public MainView(String id,InputStream is, OutputStream os, DataInputStream dis, DataOutputStream dos, Socket socket) {
		this.id = id;
		this.is = is;
		this.os = os;
		this.socket = socket;
		this.dis= dis;
		this.dos =dos;
		
		System.out.println("mainview 시작");
		
		init();
		start();
		//network();
		Connection();
	}

	
	public void network() {
		// 서버에 접속
		try {
			socket = new Socket(IP, PORT);

			if (socket != null) // socket이 null값이 아닐때 즉! 연결되었을때
			{
				Connection(); // 연결 메소드를 호출
			}
		} catch (UnknownHostException e) {

		} catch (IOException e) {
			//textArea.append("소켓 접속 에러!!\n");
			append_Message("소켓 접속 에러!!\n");
		}

	}
	public void Connection() { // 실직 적인 메소드 연결부분
		try { // 스트림 설정
			
			is = socket.getInputStream();
			dis = new DataInputStream(is);
			os = socket.getOutputStream();
			dos = new DataOutputStream(os);
			
			
		} catch (IOException e) {
			//textArea.append("스트림 설정 에러!!\n");
			append_Message("스트림 설정 에러!!\n");
		}
		append_Message("접속되었습니다\n");
		String send = Protocol.LOGIN;
		send = send + id;
		send_Message(send); // 정상적으로 연결되면 나의 id를 전송			
		Thread th = new Thread(new Runnable() { // 스레드를 돌려서 서버로부터 메세지를 수신
			@SuppressWarnings("null")
			@Override
			public void run() {
				while (true) {
					try {
						byte[] b = new byte[128];
						dis.read(b);
						String msg = new String(b);
						msg = msg.trim();
						String temp = msg;
						String Command = null;
						String chatMsg = null;
						System.out.println(msg);
						if(!temp.equals("")){
							StringTokenizer s = new StringTokenizer(temp);
							Command = s.nextToken("$");
							chatMsg = s.nextToken("\0");
							
							System.out.println("Command : " + Command);
							System.out.println("msg : " + chatMsg);
						}
						else
							Command = "X";
						
						
						switch(Command){
						case "CHAT":
							append_Message(chatMsg + "\n");
							System.out.println("채팅메시지 도착");
							break;
						case "MASTER":	//방장인지
							append_Message("방장입니다.\n");
							Master_flag = 1;
							readyBtn.setVisible(false);
							startBtn.setVisible(true);
							startBtn.setEnabled(true);
							break;
						case "DISC":
							append_Message(chatMsg + " 접속 종료\n");
							break;
						case "START":
							append_Message("게임 시작!\n");
							break;
						case "READY":
							append_Message("준비\n");
							break;
						case "CANCEL":
							append_Message("취소\n");
							break;
						default:
							break;
						}
						
						
					
					} catch (IOException e) {
						append_Message("메세지 수신 에러!!\n");
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
			append_Message("메세지 송신 에러!!\n");
		}
	}
	
	public void init() { // 화면구성 메소드
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, WIDTH, HEIGHT);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		
		int userx = 30;
		for(int i = 0; i < userImg.length; i++){
			userImg[i] = new JLabel(userCharacter[i]);
			userImg[i].setBounds(userx, 280, 140, 200);
			contentPane.add(userImg[i]);
			userx += 160;
		}
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(0, 490, 700, 130);
		contentPane.add(scrollPane);
		// textArea = new JTextArea();
		textArea = new JTextPane();
		scrollPane.setViewportView(textArea);
		// textArea.setForeground(new Color(255,0,0));
		textArea.setDisabledTextColor(new Color(0, 0, 0));
		textField = new JTextField();
		textField.setBounds(0, 620, 700, 30);
		contentPane.add(textField);
		textField.setColumns(10);
		sendBtn = new JButton("전   송");
		sendBtn.setBounds(720, 620, 80, 30);
		contentPane.add(sendBtn);
		textArea.setEnabled(false); // 사용자가 수정못하게 막는다
		
		readyBtn = new JButton("게임준비");
		readyBtn.setBounds(720,580, 100,30);
		readyBtn.setEnabled(true);
		contentPane.add(readyBtn);
		
		
		startBtn = new JButton("게임시작");
		startBtn.setBounds(720,580, 100,30);
		startBtn.setEnabled(false);
		startBtn.setVisible(false);
		contentPane.add(startBtn);
		
		setVisible(true);
	}
	
	public void start() { // 액션이벤트 지정 메소드
		sendAction action = new sendAction();
		sendBtn.addActionListener(action); // 내부클래스로 액션 리스너를 상속받은 클래스로
		textField.addActionListener(action);
		
		startAction startaction = new startAction();
		startBtn.addActionListener(startaction);
		
		readyAction readyaction = new readyAction();
		readyBtn.addActionListener(readyaction);
		
	}
	
	class sendAction implements ActionListener //채팅 내부클래스로 액션 이벤트 처리 클래스
	{
		@Override
		public void actionPerformed(ActionEvent e) {

			// 액션 이벤트가 sendBtn일때 또는 textField 에세 Enter key 치면
			if (e.getSource() == sendBtn || e.getSource() == textField) 
			{
				String msg = null;
				msg = String.format("CHAT$[%s] %s\n", id, textField.getText());
				send_Message(msg);
				textField.setText(""); // 메세지를 보내고 나면 메세지 쓰는창을 비운다.
				textField.requestFocus(); // 메세지를 보내고 커서를 다시 텍스트 필드로 위치시킨다
			}
		}
	}
	class startAction implements ActionListener //채팅 내부클래스로 액션 이벤트 처리 클래스
	{
		@Override
		public void actionPerformed(ActionEvent e) {
			send_Message(Protocol.GAMESTART + "");
			System.out.println("게임시작버튼");
			startBtn.setEnabled(false);
		}
	}
	class readyAction implements ActionListener //채팅 내부클래스로 액션 이벤트 처리 클래스
	{
		@Override
		public void actionPerformed(ActionEvent e) {
			JButton jbutton = (JButton)e.getSource();
			
			if(jbutton.getText().equals("게임준비")){
				send_Message(Protocol.GAMEREADY + "");
				System.out.println("준비버튼");
				jbutton.setText("준비취소");
			}
			else {
				send_Message(Protocol.CANCEL_READY+"");
				jbutton.setText("게임준비");
				System.out.println("준비취소");
			}
		}
	}
}
