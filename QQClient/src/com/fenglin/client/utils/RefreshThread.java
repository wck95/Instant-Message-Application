package com.fenglin.client.utils;

import java.awt.Color;
import java.io.IOException;
import java.net.Socket;
import java.util.Map;

import javax.swing.JLabel;

import com.fenglin.client.view.FriendListView;
import com.fenglin.commons.entity.User;
  import com.fenglin.tcp.Response;
import com.fenglin.tcp.SocketUtils;

public class RefreshThread extends Thread {

	private Socket socket ;

	private Map<String, JLabel> friendsJLabelMap;
	
	private FriendListView friendListView;

	private boolean isColse = true;
	
	public RefreshThread(Socket socket, FriendListView friendListView) {
		try {
			this.socket = socket;
			this.friendListView = friendListView;
			this.friendsJLabelMap = friendListView.friendsJLabelMap;
		} catch (Exception e) {
			e.printStackTrace();
		} 
		
	}

	public void close() {
		this.isColse = false;
	}
	
	@Override
	public void run() {
		while (isColse) {
			try {
				    Response resp = SocketUtils.readeResponse(socket);
				    if("online-infrom".equals(resp.getPath())) {
				    	User user = (User) resp.getData();
						JLabel jLabel = this.friendsJLabelMap.get(user.getUsername());
						if(jLabel != null) {
							jLabel.setForeground(Color.black);
//					  		jLabel.setEnabled(true);
					  		this.friendListView.validate();
						}
				    }else  if("msg-inform".equals(resp.getPath())) {
				    	User user = (User) resp.getData();
						JLabel jLabel = this.friendsJLabelMap.get(user.getUsername());
						if(jLabel != null) {
							jLabel.setForeground(Color.red);
					  		this.friendListView.validate();
						}
				    }else  if("offline".equals(resp.getPath())) {
				    	User user = (User) resp.getData();
						JLabel jLabel = this.friendsJLabelMap.get(user.getUsername());
						if(jLabel != null) {
							jLabel.setForeground(Color.gray);
					  		this.friendListView.validate();
						}
						close() ;
				    }
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
