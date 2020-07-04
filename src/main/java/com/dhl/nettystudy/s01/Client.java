package com.dhl.nettystudy.s01;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import org.checkerframework.checker.units.qual.s;
//BIO 半双工，少用
public class Client {
	public static void main(String[] args) throws UnknownHostException, IOException{
		//未作io异常处理，出现异常会断掉
		Socket socket = new Socket("127.0.0.1",8888);
		//半双工：读写不能同时进行。
		socket.getOutputStream().write("helloServer".getBytes());
		socket.getOutputStream().flush();
		System.out.println("Write over,waitting for msg back...");
		byte[] bytes = new byte[1024];
		int len = socket.getInputStream().read(bytes);
		System.out.println(new String(bytes,0,len));
		socket.close();
	}
}
