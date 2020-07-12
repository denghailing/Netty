/**
 * Copyright (c) 2013-Now http://denghailing.com All rights reserved.
 */
package com.dhl.nettystudy.s02;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.TextArea;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.checkerframework.common.reflection.qual.NewInstance;
import org.checkerframework.common.value.qual.StaticallyExecutable;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.concurrent.Future;

/**
 * 
 * @author DHL
 * @version 2020年7月12日
 */
public class ClientFrame extends Frame {
	TextArea ta = new TextArea();
	TextField tf = new TextField();
	Client client = null;
	public ClientFrame(){
		this.setSize(600,400);
		this.setLocation(100,200);
		this.add(ta,BorderLayout.CENTER);
		this.add(tf,BorderLayout.SOUTH);
		this.setVisible(true);
		ta.setEditable(false);;
		tf.addActionListener(new ActionListener() {	
			@Override
			public void actionPerformed(ActionEvent e) {
				//把字符串发送到服务器
				Date t = new Date();
				SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				if(tf.getText()==null && tf.getText() == " "){
					System.err.println("消息不能为空！");
				}else client.send(tf.getText());
				ta.setText(ta.getText()+"\n"+df.format(t)+":【发送】"+tf.getText());
				tf.setText("");
			}
		});
		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e){
				System.exit(0);
			}
		});
		conetToServer();
	}
	private void conetToServer(){
		client = new Client(this);
		client.connect();
	}
	
	public static void main(String[] args){
		ClientFrame cFrame = new ClientFrame();
	}
	
}




