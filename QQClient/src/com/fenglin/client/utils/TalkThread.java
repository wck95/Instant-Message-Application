package com.fenglin.client.utils;

import java.net.Socket;

import javax.swing.JTextArea;

import com.fenglin.commons.entity.Message;
import com.fenglin.tcp.Request;
import com.fenglin.tcp.SocketUtils;

public class TalkThread extends Thread {

	private Socket socket;

	private JTextArea jTextArea;
	
	private boolean isColse = true;

	public TalkThread(Socket socekt, JTextArea jTextArea) {
		super();
		this.socket = socekt;
		this.jTextArea = jTextArea;
	}

	public void close() {
		this.isColse = false;
	}
	
	@Override
	public void run() {
		while (isColse) {
			try {
				Request requ = SocketUtils.readeRequest(socket);
				System.out.println("requ.getPath()="+requ.getPath());
				if("receiveMsg".equals(requ.getPath()) || "talk".equals(requ.getPath())) {
					Message message = (Message) requ.getData();
					String msg = message.getMsg();
					jTextArea.append(msg+"\n");
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
