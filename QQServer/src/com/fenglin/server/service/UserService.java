package com.fenglin.server.service;

import java.util.List;

import com.fenglin.commons.dao.DbExecute;
import com.fenglin.commons.entity.User;
import com.fenglin.commons.model.Record;
import com.fenglin.commons.utils.JacksonUtils;
import com.fenglin.commons.utils.RecordClassCast;
import com.fenglin.tcp.Request;
import com.fenglin.tcp.Response;

public class UserService {

	public Response register(Request request) {
		Response response = null;
		try {
			User user = (User) request.getData();

			String sql = "select * from tb_user where username= '" + user.getUsername() + "' " ;
			List<Record> list =  DbExecute.executeQuery(sql);
			System.out.println(list);
			if (list != null && list.size()>0) return new Response(212, "账号已存在,请重新输入账号", null, null);

			String sql1 = "insert into tb_user(username,password) values('" + user.getUsername() + "', "+"'" + user.getPassword() + "')";
			
			int rows = DbExecute.Insert(sql1,user);
			
			if( rows > 0) {
				List<Record> list1  = DbExecute.executeQuery(sql);
				User u = new User();
				RecordClassCast.RecordToObject(u, list1.get(0).getMap());
				response =  new Response(200, "注册成功!", JacksonUtils.obj2json(u), null);
			}else {
				response =  new Response(500, "注册失败: 网络异常,请稍后重试!", null, null);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return response;
	}

	public Response updateUserInfo(Request request) {

		Response response = null;
		try {
			String token = request.getToken();
			User user = (User) JacksonUtils.json2pojo(token, User.class);

			String sql1 = "update  tb_user set password = ?";

			int rows = DbExecute.Update(sql1,user);

			response = rows > 0 ? new Response(200, "修改成功!", token, null)
					: new Response(500, "修改失败: 网络异常,请稍后重试!", token, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return response;
	}

	public Response getFirends(Request request) {
		Response response = null;
		try {
			String token = request.getToken();
			User user = (User) JacksonUtils.json2pojo(token, User.class);

			if(user.getFirendsIds() != null && user.getFirendsIds().length()>0) {
				String[] ids = user.getFirendsIds().split(",");
				StringBuffer buffer = new StringBuffer();
				for (int i = 0; i < ids.length; i++) {
					if (i == ids.length - 1) {
						buffer.append("'" + ids[i] + "'");
					}else {
						buffer.append("'" + ids[i] + "',");
					}
				}
				System.out.println("ids="+buffer.toString());
				String sql = "select * from tb_user where id in (" + buffer.toString() + ")";

				List<Record> list = DbExecute.executeQuery(sql);
				response = list != null ? new Response(200, "好友列表获取成功!", token, list)
						: new Response(500, "好友列表获取失败: 网络异常,请稍后重试!", token, null);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return response;
	}

}
