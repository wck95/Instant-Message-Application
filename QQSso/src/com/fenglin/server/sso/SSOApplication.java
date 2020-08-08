package com.fenglin.server.sso;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

import com.fenglin.commons.entity.User;
import com.fenglin.server.sso.service.LoginService;
import com.fenglin.tcp.Request;
import com.fenglin.tcp.Response;
import com.fenglin.tcp.SocketUtils;

public class SSOApplication {

	private static Map<String, User> map = new HashMap<String, User>();
	
	public static void main(String[] args) {
		DispathServlet();
	}

	public static void DispathServlet() {
		LoginService service = new LoginService();
		ServerSocket serverSocket;
		Response response = null;
		try {
			System.out.println("认证服务器已启动.....");
			serverSocket = new ServerSocket(80);  
			while (true) { 
				Socket socket = serverSocket.accept();
				 
				Request request = (Request) SocketUtils.readeRequest(socket);
				
				System.out.println("有人来登录了: ");
				
				if("login".equals(request.getPath())) response =  service.login(request); 
				
				if("loginCheck".equals(request.getPath())) response =  service.loginCheck(request); 
				
				SocketUtils.sendResponse(socket, response);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
 
	

}
