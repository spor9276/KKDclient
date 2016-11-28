package ����Ŭ���̾�Ʈ;

import java.awt.Color;
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
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.border.EmptyBorder;


public class MainView extends JFrame {
	public static final int WIDTH = 900;
	public static final int HEIGHT = 700;
	public static final String IP = "127.0.0.1";
	public static final int PORT = 30000;
	
	private String id;
	private String password;
	
	private Socket socket; // �������
	private InputStream is;
	private OutputStream os;
	private DataInputStream dis;
	private DataOutputStream dos;
	
	private int first_flag = 1;
	
	private JPanel contentPane;
	private JTextField textField; // ���� �޼��� ���°�
	private ImageIcon suggestWordLabelImg = new ImageIcon("img/suggestWord.png");
	private JLabel suggestWordLabel = new JLabel(suggestWordLabelImg);
	JButton sendBtn; // ���۹�ư
	// JTextArea textArea; // ���ŵ� �޼����� ��Ÿ�� ����
	JTextPane textArea; // �̸�Ƽ���̳� �̹��� �����ֱ� ���ؼ��� JtextPane�� ����Ѵ�.
	
	private ImageIcon gameBackGround = new ImageIcon("img/gameBackground.png");//
	private Image gameBackGroundImg = gameBackGround.getImage();//
	

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
		
		System.out.println("mainview ����");
		
		init();
		start();
		//network();
		Connection();
	}

	
	public void network() {
		// ������ ����
		try {
			socket = new Socket(IP, PORT);

			if (socket != null) // socket�� null���� �ƴҶ� ��! ����Ǿ�����
			{
				Connection(); // ���� �޼ҵ带 ȣ��
			}
		} catch (UnknownHostException e) {

		} catch (IOException e) {
			//textArea.append("���� ���� ����!!\n");
			append_Message("���� ���� ����!!\n");
		}

	}
	public void Connection() { // ���� ���� �޼ҵ� ����κ�
		try { // ��Ʈ�� ����
			
			is = socket.getInputStream();
			dis = new DataInputStream(is);
			os = socket.getOutputStream();
			dos = new DataOutputStream(os);
			
			
		} catch (IOException e) {
			//textArea.append("��Ʈ�� ���� ����!!\n");
			append_Message("��Ʈ�� ���� ����!!\n");
		}
		append_Message("���ӵǾ����ϴ�\n");
		String send = Protocol.LOGIN;
		send = send + id;
		send_Message(send); // ���������� ����Ǹ� ���� id�� ����			
		Thread th = new Thread(new Runnable() { // �����带 ������ �����κ��� �޼����� ����
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
						if(!temp.equals("")){
							StringTokenizer s = new StringTokenizer(temp);
							Command = s.nextToken("$");
							chatMsg = s.nextToken("\0");
							
							System.out.println("Command : " + Command);
							System.out.println("msg : " + chatMsg);
						}
						else
							Command = "X";
						if(Command.equals("CHAT")){
							append_Message(chatMsg + "\n");
							System.out.println("ä�ø޽��� ����");
						}
					
					} catch (IOException e) {
						append_Message("�޼��� ���� ����!!\n");
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
			append_Message("�޼��� �۽� ����!!\n");
		}
	}
	
	public void init() { // ȭ�鱸�� �޼ҵ�
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, WIDTH, HEIGHT);
		contentPane = new JPanel(){
			public void paintComponent(Graphics g){
				g.drawImage(gameBackGroundImg, 0, 0, null);
				setOpaque(false);
				super.paintComponent(g);
			}
		};
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		suggestWordLabel.setBounds(300, 50, 218, 57);
		contentPane.add(suggestWordLabel);
		
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
		sendBtn = new JButton("��   ��");
		sendBtn.setBounds(720, 620, 80, 30);
		contentPane.add(sendBtn);
		textArea.setEnabled(false); // ����ڰ� �������ϰ� ���´�
		setVisible(true);
	}
	
	public void start() { // �׼��̺�Ʈ ���� �޼ҵ�
		Myaction action = new Myaction();
		sendBtn.addActionListener(action); // ����Ŭ������ �׼� �����ʸ� ��ӹ��� Ŭ������
		textField.addActionListener(action);
	}
	
	class Myaction implements ActionListener //ä�� ����Ŭ������ �׼� �̺�Ʈ ó�� Ŭ����
	{
		@Override
		public void actionPerformed(ActionEvent e) {

			// �׼� �̺�Ʈ�� sendBtn�϶� �Ǵ� textField ���� Enter key ġ��
			if (e.getSource() == sendBtn || e.getSource() == textField) 
			{
				String msg = null;
				msg = String.format("CHAT$[%s] %s\n", id, textField.getText());
				send_Message(msg);
				textField.setText(""); // �޼����� ������ ���� �޼��� ����â�� ����.
				textField.requestFocus(); // �޼����� ������ Ŀ���� �ٽ� �ؽ�Ʈ �ʵ�� ��ġ��Ų��
				
			}

		}

	}
}
