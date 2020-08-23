package com.fenglin.client.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.Socket;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import com.fenglin.client.service.UserService;
import com.fenglin.commons.entity.User;
import com.fenglin.commons.utils.JacksonUtils;
import com.fenglin.commons.utils.PropertiesUtils;
import com.fenglin.commons.utils.ViewMessageBox;
import com.fenglin.tcp.Request;
import com.fenglin.tcp.Response;
import com.fenglin.tcp.SocketUtils;

public class LoginView extends JFrame {

	private static final long serialVersionUID = 3024614666505474881L;

	private JPanel northJPanel;
	private JLabel loginBackJLabel;

	private JPanel logoJPanel;
	private JLabel logoJLabel;

	private JLabel usernameJLabel;
	private JTextField usernameJTextField;
	private JLabel passwordJLabel;
	private JPasswordField pwdJPasswordField;
	private JPanel centerJPanel;

	private JButton loginJButton;
	private JButton registerJButton;
	private JButton resetJButton;
	private JPanel southJPanel;

	private Socket socket;
	private FriendListView friendListView;
	private final PropertiesUtils cfg = PropertiesUtils.createPropertiesUtils("resources/application.properties");

	public void createFrame() {

		ImageIcon imageIcon = new ImageIcon(
				LoginView.class.getClassLoader().getResource("resources/static/images/login_back.png"));
		loginBackJLabel = new JLabel("", imageIcon, JLabel.CENTER);

		northJPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		northJPanel.add(loginBackJLabel);
		this.add(northJPanel, BorderLayout.NORTH);

		ImageIcon imageIcon1 = new ImageIcon(
				LoginView.class.getClassLoader().getResource("resources/static/images/qq.png"));
		logoJLabel = new JLabel("", imageIcon1, JLabel.CENTER);
		logoJPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		logoJPanel.add(logoJLabel);
		this.add(logoJPanel, BorderLayout.WEST);

		usernameJLabel = new JLabel("用户账号:");
		usernameJLabel.setFont(new Font(Font.DIALOG, Font.PLAIN, 20));
		usernameJTextField = new JTextField(20);
		usernameJTextField.setText("QQ号码/手机号/邮箱");
		usernameJTextField.setFont(new Font(Font.DIALOG, Font.PLAIN, 20));

		passwordJLabel = new JLabel("用户密码:");
		passwordJLabel.setFont(new Font(Font.DIALOG, Font.PLAIN, 20));
		pwdJPasswordField = new JPasswordField(20);
		pwdJPasswordField.setFont(new Font(Font.DIALOG, Font.PLAIN, 20));

		registerJButton = new JButton("还没账号?注册");
		registerJButton.setFont(new Font(Font.DIALOG, Font.PLAIN, 20));

		resetJButton = new JButton("重置");
		resetJButton.setFont(new Font(Font.DIALOG, Font.PLAIN, 20));

		loginJButton = new JButton("登录");
		loginJButton.setBackground(new Color(113, 191, 234));
		Dimension preferredSize = new Dimension(160, 40); // 设置尺寸
		loginJButton.setPreferredSize(preferredSize); // 设置按钮大小
		loginJButton.setVerticalAlignment(SwingConstants.CENTER);
		loginJButton.setFont(new Font(Font.DIALOG, Font.PLAIN, 20));

		southJPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		southJPanel.add(registerJButton);
		southJPanel.add(resetJButton);
		southJPanel.add(loginJButton);

		centerJPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		centerJPanel.add(usernameJLabel);
		centerJPanel.add(usernameJTextField);
		centerJPanel.add(passwordJLabel);
		centerJPanel.add(pwdJPasswordField);

		centerJPanel.add(southJPanel);

		this.add(centerJPanel, BorderLayout.CENTER);

		// 监听鼠标点击登录按钮
		loginJButtonListener(loginJButton);
		resetJButtonListener(resetJButton);
		registerJButtonListener(registerJButton);

		this.setTitle("QQ登录");
		this.setBounds(505, 305, 560, 410);
		this.setVisible(true);
		this.setResizable(false);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	private void resetJButtonListener(JButton resetJButton2) {
		resetJButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				super.mouseClicked(e);
				usernameJTextField.setText("");
				pwdJPasswordField.setText("");
			}
		});
	}

	public void loginJButtonListener(JButton loginJButton) {
		loginJButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				super.mouseClicked(e);
				login();
			}
		});
		pwdJPasswordField.addKeyListener(new KeyListener() {

			@Override
			public void keyTyped(KeyEvent e) {
			}

			@Override
			public void keyReleased(KeyEvent e) {
			}

			@Override
			public void keyPressed(KeyEvent e) {
				// 监听密码框敲击回车发送登录请求事件
				if (10 == e.getKeyCode())
					login();
			}
		});
	}

	private void login() {
		try {
			String username = usernameJTextField.getText();
			char[] pwdChar = pwdJPasswordField.getPassword();
			String password = new String(pwdChar);
			System.out.println(username + "登录了!");
			if (fromIsNullCheck(username, password)) {
				ViewMessageBox.Warning(LoginView.this, "用户名或密码不能为空!");
				return;
			}

			this.socket = new Socket(cfg.getValue("QQServerIP"), 80);
			User us = new User(username, password);
			Request request = new Request("get", "login", JacksonUtils.obj2json(us), us);
			SocketUtils.sendRequest(socket, request);

			Response result = SocketUtils.readeResponse(socket);
			System.out.println(result);
			// 登录成功 进入好友列表页面
			if (result != null && result.getState() == 200) {
				if ("login".equals(result.getPath())) {
					LoginView.this.dispose();
					User user = (User) result.getData();
					System.out.println(user.getUsername() + result.getMessage());
					this.friendListView = new FriendListView(user,socket);
					this.friendListView.createFrame();
				}else {
					ViewMessageBox.Warning(LoginView.this, result.getMessage());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void registerJButtonListener(JButton regJButton) {
		regJButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				super.mouseClicked(e);
				LoginView.this.dispose();
				RegisterView regView = new RegisterView();
				regView.createFrame();
			}
		});
	}

	private boolean fromIsNullCheck(String username, String password) {
		if (username.isEmpty() && "QQ号码/手机号/邮箱".equals(username)) return true;
		if (password.isEmpty()) return true;
		return false;
	}

 

}
