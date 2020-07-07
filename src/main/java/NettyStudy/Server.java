package NettyStudy;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.internal.tcnative.Buffer;
import io.netty.util.ReferenceCountUtil;

public class Server {
	public static void main(String[] args) throws IOException {
		EventLoopGroup bossgroup = new NioEventLoopGroup(1);
		EventLoopGroup workgroup = new NioEventLoopGroup(2);
		try {
			ServerBootstrap  serverBootstrap = new ServerBootstrap();
			ChannelFuture future = serverBootstrap.group(bossgroup,workgroup)
			.channel(NioServerSocketChannel.class)
			.childHandler(new ChannelInitializer<SocketChannel>() {
				@Override
				protected void initChannel(SocketChannel ch) throws Exception {
					//System.out.println(Thread.currentThread().getId());
					ChannelPipeline pl = ch.pipeline();
					pl.addLast(new ServerChildHandler());
   				}
				
			})
			.bind(8889)
			.sync();
			System.out.println("Server started!");
			future.channel().closeFuture().sync();
			//如果有人调用了close（）方法，返回值是closeFuture，如果没有人调用close（）方法，closeFuture永远等待。
		} catch (InterruptedException e) {
			e.printStackTrace();
		}finally {
			workgroup.shutdownGracefully();
			bossgroup.shutdownGracefully();
			System.out.println("Server closed!");
		}
		
	}
}

class ServerChildHandler extends ChannelInboundHandlerAdapter{

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		ByteBuf buf = null;
		try{
			buf = (ByteBuf)msg;
			System.out.println(buf);
			System.out.println(buf.refCnt());
		}finally {
			if(buf != null)
				ReferenceCountUtil.release(msg);
			System.out.println(buf.refCnt());
		}

	}
}
