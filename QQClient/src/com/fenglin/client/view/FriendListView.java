package com.fenglin.client.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.Socket;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import com.fenglin.client.service.FirendService;
import com.fenglin.client.utils.RefreshThread;
import com.fenglin.commons.entity.User;
import com.fenglin.commons.model.Record;
import com.fenglin.commons.utils.JacksonUtils;
import com.fenglin.commons.utils.PropertiesUtils;
import com.fenglin.commons.utils.RecordClassCast;
import com.fenglin.commons.utils.ViewMessageBox;
import com.fenglin.tcp.Request;
import com.fenglin.tcp.Response;
import com.fenglin.tcp.SocketUtils;

/**
* @auther 作者: wangchengkai
* @Email  邮箱: 1137102347@qq.com
* @date 创建时间: 2020年3月24日  
* @Description 类说明: 登录成功后进入的聊天主界面
*/
public class FriendListView extends JFrame {

	private static final long serialVersionUID = 8210510969137661782L;
	
	private JScrollPane jScrollPane = null;
	private JPanel jPanel = null;
	
	private  User user;
	private FirendService service ;
	
	private Map<String, Record> friendsMap = new HashMap<String, Record>();
	
	public Map<String, JLabel> friendsJLabelMap = new HashMap<String, JLabel>();
	
	private List<Record> firendsList;
	
	private Socket ssoSocket ;
	
	private RefreshThread thread; 
	
	private final PropertiesUtils cfg = PropertiesUtils.createPropertiesUtils("resources/application.properties");		

	public FriendListView(User user,Socket socket )  {
		this.service = new FirendService();
		this.user = user;
		this.ssoSocket = socket;
		try {
			if(user.getFirendsIds() != null && user.getFirendsIds().length()>0) {
				Response result = service.dispose(user, cfg.getValue("QQServerIP"), 9999, "getFirends", "get",true);
				if(result == null) {
					ViewMessageBox.Warning(FriendListView.this, "服务器连接超时,请检查网络连接!");
					return;
				}
				if(result.getState() == 200) this.firendsList = (List<Record>) result.getData();
				else ViewMessageBox.Warning(FriendListView.this, result.getMessage());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void createFrame() {
		if(this.firendsList != null && this.firendsList.size() > 0) {
			jPanel = new JPanel(new GridLayout(firendsList.size(),1,5,5));
			jPanel.setPreferredSize(new Dimension(240, 650));
			for (Record r : firendsList ) {
				String friendName = r.getStr("username");
				friendsMap.put(friendName, r);
				ImageIcon imageIcon = new ImageIcon(FriendListView.class.getClassLoader()
						.getResource(cfg.getValue("imagePath")+"/"+r.getStr("avatar")));
				JLabel jLabel = new JLabel(friendName,imageIcon, JLabel.LEFT);
				friendsJLabelMap.put(friendName, jLabel);
				if(r.getInt("online") == 0) {
					jLabel.setForeground(Color.gray);

				}
				talkButton(jLabel, r);
				jPanel.add(jLabel);
			}
			jScrollPane = new JScrollPane(jPanel);
			this.add(jScrollPane,BorderLayout.CENTER);
		}
		
		this.setTitle(user.getUsername()+"的好友列表");
		this.setFont(new Font(Font.DIALOG, Font.PLAIN, 18));
		this.setBounds(533, 55, 350, 650);
		this.setVisible(true);
		this.setResizable(false);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		//监听好友列表窗口被关闭
		isCodeWindowListener();
		
		//监听好友上线和下线
		refrest();
	}
	
	private void talkButton(JLabel jLabel,Record r ){
		jLabel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if(e.getClickCount() == 2) {
				    try {
				    	Record r = friendsMap.get(((JLabel)e.getSource()).getText());
				    	User firends = new User();
						RecordClassCast.RecordToObject(firends, r.getMap());
						
				    	Socket socket = new Socket("localhost",9998);
				    	
				    	Request request = new Request("post", "talklink", JacksonUtils.obj2json(user), r.getInt("id"), "localhost", 8081) ; 
				    	SocketUtils.sendRequest(socket, request);
				    	
						TalkView talkView = new TalkView(user, firends, socket);
						talkView.createFrame();
						
					} catch (Exception e1) {
						e1.printStackTrace();
					}
				}
			}
		});
	}
	
	public void refrest() {
		thread = new RefreshThread(ssoSocket, this);
		thread.start();
	}
	
	private void isCodeWindowListener() {
		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				 //当聊天页面被关闭时需要将socket和线程thread关闭否者会报错
				try {
					System.out.println("好友列表窗口关闭了!");
					Socket socket = new Socket("localhost",80);
					Request requ = new Request("post", "logOut",  JacksonUtils.obj2json(user),user);
					SocketUtils.sendRequest(socket, requ);
					thread.close();
					socket.close();
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		});
	}
}
  