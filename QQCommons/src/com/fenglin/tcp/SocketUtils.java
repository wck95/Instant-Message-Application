package com.fenglin.tcp;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;

import com.fenglin.commons.entity.BaseEntity;
import com.fenglin.commons.utils.JacksonUtils;

public class SocketUtils {

	public static void  sendRequest(Socket socket, Request request ) throws IOException {
		OutputStream out = socket.getOutputStream();
		ObjectOutputStream objOut = new ObjectOutputStream(out);
		objOut.writeObject(request);
	}
	
	public static void  farwardRequest(Socket socket, Request request ) throws IOException {
		OutputStream out = socket.getOutputStream();
		ObjectOutputStream objOut = new ObjectOutputStream(out);
		objOut.writeObject(request);
	}
	
	public static void sendResponse(Socket socket, Response response) throws IOException {
		OutputStream outputStream = socket.getOutputStream();
		ObjectOutputStream objOutStream = new ObjectOutputStream(outputStream);
		objOutStream.writeObject(response);
	} 
	
	public static Response readeResponse(Socket socket) throws IOException, ClassNotFoundException {
		InputStream input = socket.getInputStream();
		ObjectInputStream objInput = new ObjectInputStream(input);
	    return (Response) objInput.readObject();
	}
	
	public static Request readeRequest(Socket socket) throws IOException, ClassNotFoundException {
		InputStream input = socket.getInputStream();
		ObjectInputStream objInput = new ObjectInputStream(input);
	    return (Request) objInput.readObject();
	}

	
	public Response dispose(BaseEntity entity, String ip, int port, String path, String method) {
		 Response result = null;
		 try {
			Socket socket = new Socket(ip,port);
		
			Request request = new Request();
			request.setToken(JacksonUtils.obj2json(entity));
			request.setData(entity);
			request.setPath(path);
			request.setMethod(method);
			sendRequest(socket, request);
			
		    result = (Response) readeResponse(socket);
		} catch (Exception  e) {
			e.printStackTrace();
		} 
		 return result;
	 }
}
