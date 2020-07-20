package com.dhl.nettystudy.s02;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.xml.bind.annotation.XmlElementDecl.GLOBAL;
import javax.xml.crypto.Data;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.internal.tcnative.Buffer;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.concurrent.GlobalEventExecutor;

public class Server {
	public static ChannelGroup clients = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
	
	public void serverStart()throws Exception {
		EventLoopGroup bossGroup = new NioEventLoopGroup(1);
		EventLoopGroup workerGroup = new NioEventLoopGroup(2);
		
		try {
			ServerBootstrap b = new ServerBootstrap();
			ChannelFuture f = b.group(bossGroup, workerGroup)
				.channel(NioServerSocketChannel.class)
				.childHandler(new ChannelInitializer<SocketChannel>() {
					@Override
					protected void initChannel(SocketChannel ch) throws Exception {
						ChannelPipeline pl = ch.pipeline();
						pl.addLast(new TankMsgDecoder()).addLast(new ServerChildHandler());
					}
				})
				.bind(8889)
				.sync();
			
			System.out.println("server started!");
			ServerFrame.INSTANCE.updateServerMsg("server started!");
			f.channel().closeFuture().sync(); //close()->ChannelFuture
		} finally {
			workerGroup.shutdownGracefully();
			bossGroup.shutdownGracefully();
		}
		
	}	
}

class ServerChildHandler extends ChannelInboundHandlerAdapter { //SimpleChannleInboundHandler Codec
	public String df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(System.currentTimeMillis()));
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		Server.clients.add(ctx.channel());
		System.out.println("客户端["+ctx.channel().id()+"]连接成功！");
		ServerFrame.INSTANCE.updateServerMsg("["+df+"]客户端["+ctx.channel().id()+"]连接成功！");
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		
		ByteBuf buf = null;
		try {
			TankMsg tMsg = (TankMsg)msg;
			System.out.println(tMsg.toString());
//			if(tMsg.equals("_close_")) {
//				System.out.println("["+df+" "+ctx.channel().id()+"]客户端要求退出。");
//				ServerFrame.INSTANCE.updateServerMsg("["+df+" "+ctx.channel().id()+"]客户端要求退出。");
//				Server.clients.remove(ctx.channel());
//				ctx.close();
//			} else {
//				Server.clients.writeAndFlush(msg);
//				ServerFrame.INSTANCE.updateClientMsg("["+df+" "+ctx.channel().id()+"]"+s);
//				ServerFrame.INSTANCE.updateServerMsg("["+df+" "+ctx.channel().id()+"]"+s);
//			}
		} finally {
			ReferenceCountUtil.release(msg);
		}
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		cause.printStackTrace();
		//删除出现异常的客户端channle，并关闭连接
		Server.clients.remove(ctx.channel());
		ctx.close();
	}
}
