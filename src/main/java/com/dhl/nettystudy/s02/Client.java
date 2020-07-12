/**
 * Copyright (c) 2013-Now http://denghailing.com All rights reserved.
 */
package com.dhl.nettystudy.s02;

import java.text.SimpleDateFormat;
import java.util.Date;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * 
 * @author DHL
 * @version 2020年7月13日
 */
public class Client {
	private Channel channel = null;
	private ClientFrame cFrame;
	public Client(ClientFrame cFrame){
		this.cFrame = cFrame;
	}
	public void connect(){
		EventLoopGroup elgroup = new NioEventLoopGroup();
		Bootstrap b = new Bootstrap();
		try {
			Date t = new Date();
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			b.group(elgroup)
			.channel(NioSocketChannel.class)
			.handler(new ClientHandlerInitlize())
			.connect("localhost",8889)
			.addListener(new ChannelFutureListener() {			
				@Override
				public void operationComplete(ChannelFuture future) throws Exception {
					if(!future.isSuccess())
						System.out.println(df.format(t)+"连接服务器失败！");
					else {
						System.out.println(df.format(t)+"连接服务器成功！");
						cFrame.ta.setText(df.format(t)+"连接服务器成功！...");
						channel = future.channel();
					}
				}
			})
			.sync()
			.channel()
			.closeFuture()
			.sync();
			System.out.println("......");
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			elgroup.shutdownGracefully();
		}
	}
	class ClientHandlerInitlize extends ChannelInitializer<SocketChannel>{
		@Override
		protected void initChannel(SocketChannel ch) throws Exception {
			ch.pipeline().addLast(new CilentHandler());
		}
	}
	
	class CilentHandler extends ChannelInboundHandlerAdapter{
	
//		@Override
//		public void channelActive(ChannelHandlerContext ctx) throws Exception {
//			ByteBuf buf = Unpooled.copiedBuffer("hello".getBytes());
//			ctx.writeAndFlush(buf);
//		}
	
		@Override
		public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
			ByteBuf buf = null;
			try {
				buf = (ByteBuf)msg;
				byte[] bytes = new byte[buf.readableBytes()];
				buf.getBytes(buf.readerIndex(), bytes);
				//System.out.println("返回："+new String(bytes));
				String msgAccept = new String(bytes);
				System.out.println("返回："+msgAccept);
				cFrame.ta.setText(cFrame.ta.getText()+"\n"+msgAccept);
			} catch (Exception e) {
				
			}
		}
		
	}

	public void send(String msg) {
		//System.err.println("aaa");
		ByteBuf buf = Unpooled.copiedBuffer(msg.getBytes());
		channel.writeAndFlush(buf);
	}

}
