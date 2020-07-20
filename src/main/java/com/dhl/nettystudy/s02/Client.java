/**
 * Copyright (c) 2013-Now http://denghailing.com All rights reserved.
 */
package com.dhl.nettystudy.s02;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.text.TabableView;

import org.checkerframework.common.reflection.qual.NewInstance;

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
import io.netty.util.ReferenceCountUtil;

/**
 * 
 * @author DHL
 * @version 2020年7月13日
 */

public class Client {

	private Channel channel = null;
	String df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(System.currentTimeMillis()));
	public void connect() {
		// 线程池
		EventLoopGroup group = new NioEventLoopGroup(1);

		Bootstrap b = new Bootstrap();

		try {
			ChannelFuture f = b.group(group).channel(NioSocketChannel.class).handler(new ClientChannelInitializer())
					.connect("localhost", 8889);

			f.addListener(new ChannelFutureListener() {
				@Override
				public void operationComplete(ChannelFuture future) throws Exception {
					if (!future.isSuccess()) {
						System.out.println("服务起连接失败!");
						ClientFrame.INSTANCE.updateText("服务起连接失败!");
					} else {
						System.out.println("服务起连接成功!");
						ClientFrame.INSTANCE.updateText("服务起连接成功!");
						channel = future.channel();
					}
				}
			});

			f.sync();
			// wait until close
			f.channel().closeFuture().sync();
			System.out.println("已经退出");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			group.shutdownGracefully();
		}
	}
	
	public void send(String msg) {
		ByteBuf buf = Unpooled.copiedBuffer(msg.getBytes());
		channel.writeAndFlush(buf);
		ClientFrame.INSTANCE.updateText(df+" "+channel.id()+":"+msg);
	}

	public static void main(String[] args) throws Exception {
		Client c = new Client();
		c.connect();
	}

	public void closeConnect() {
		if(channel!=null)
			this.send("_close_");
	}
}

class ClientChannelInitializer extends ChannelInitializer<SocketChannel> {

	@Override
	protected void initChannel(SocketChannel ch) throws Exception {
		ch.pipeline()
			.addLast(new TankMsgEncoder())
			.addLast(new ClientHandler());
	}

}

class ClientHandler extends ChannelInboundHandlerAdapter {

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		ByteBuf buf = null;
		try {
			buf = (ByteBuf) msg;
			byte[] bytes = new byte[buf.readableBytes()];
			buf.getBytes(buf.readerIndex(), bytes);
			String msgAccepted = new String(bytes);
			ClientFrame.INSTANCE.updateText("服务起返回："+msgAccepted);
		} finally {
			if (buf != null)
				ReferenceCountUtil.release(buf);
		}
	}

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		ctx.writeAndFlush(new TankMsg(5, 8));
	}

}