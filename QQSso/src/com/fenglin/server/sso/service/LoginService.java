package com.fenglin.server.sso.service;

import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.codec.digest.DigestUtils;

import com.fenglin.commons.dao.DbExecute;
import com.fenglin.commons.entity.User;
import com.fenglin.commons.model.Record;
import com.fenglin.tcp.Request;
import com.fenglin.tcp.Response;
import com.fenglin.tcp.SocketUtils;
import com.fenglin.commons.utils.RecordClassCast;

public class LoginService {

	private Map<Integer, User> loginMap = new HashMap<Integer, User>();
	
	public Response login(Request request) {
		
		Response response = null; 
		User user = (User) request.getData();
		
		String sql = "select * from tb_user where username= '"+user.getUsername()+"' ";
		
		Record record = (Record) DbExecute.findBy(sql);
		if(record == null) return new Response(210,"账号不存在",null,null);
		System.out.println(record.toString());
		try {
			String MD5Password = DigestUtils.md5Hex(user.getPassword());
			 if(MD5Password.equals(record.getStr("password"))) {
				 User u = new User();
				 RecordClassCast.RecordToObject(u, record.getMap());
			     response = new Response(200,"登录成功!", record.toString(), u);
			     
			    //登陆后既上线了, 将用户信息添加到上线map中
			     loginMap.put(u.getId(),u);
			     
			}else {
				response = new Response(211,"密码输入有误,请重新尝试", null, null) ;
			}
					 
		} catch (Exception e) {
			e.printStackTrace();
		}
	 return response;
	}

	public Response loginCheck(Request request){
		Response response = null; 
		int id = (int) request.getData();
		User u = loginMap.get(id);
		if(u == null) response = new Response(200, id+"不在线", request.getToken(), null);
		else response = new Response(200, id+"在线", request.getToken(), u);
		return response;
	}
	
}
