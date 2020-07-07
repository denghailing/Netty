package NettyStudy;

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
//netty中都是异步方法
public class Client {
	public static void main(String[] args){
		//线程池，event:网络的事件，Loop:循环执行，group：组。事件处理的线程池
		//NioEventLoopGroup(args)：args表示线程数量，默认是核数*2
		EventLoopGroup group = new NioEventLoopGroup();
		//Bootstrap：辅助启动类
		Bootstrap b = new Bootstrap();
//		try {
//			//把线程池加载到启动类。
//			b.group(group)
//			.channel(NioSocketChannel.class)//指定连接到服务器的channel的类型，可以换掉NioSocketChannel为SockeChannel转换为阻塞形式
//			//handler:当这个channel上有事件交给那个handler处理，ChannelInitializer：channel初始化器，
//			.handler(new ClientChannelInitislizer())
//			.connect("localhost",8889)//connect是异步方法，连接执行完直接执行下一步，不管连接成功是否（下面没调用sync（）方法）
//			.sync();//该方法判断是否连接成功，不然他向下执行直到他结束，异常处理
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}finally{
//			group.shutdownGracefully();
//		}
//		//不含sync（）方法的写法
		try {
			//没用sync（）方法的写法，用ChannelFuture类来接受连接结果
			ChannelFuture future = b.group(group)
			.channel(NioSocketChannel.class)
			.handler(new ClientChannelInitislizer())
			.connect("localhost",8889);
			//添加监听器，观察结果是什么样，结果一出来调用operationComplete方法。
			//为什么要加监听器，connect()连接不一定执行完成就已经向下执行了，
			future.addListener(new ChannelFutureListener() {
				@Override
				public void operationComplete(ChannelFuture future) throws Exception {
					//如果没成功
					if(!future.isSuccess()){
						System.out.println("not connected!");
					}else{
						System.out.println("has connected!");
					}
				}
			});
			future.sync();
			System.out.println(".......");
			
		} catch (Exception e) {
			// TODO: handle exception
		}finally {
			group.shutdownGracefully();
		}
	//end	
	}
}

class ClientChannelInitislizer extends ChannelInitializer<SocketChannel>{

	@Override
	protected void initChannel(SocketChannel ch) throws Exception {
		//System.out.println("channel has initislizer!"+ch);
		//ch初始化成功后，调用自定义的handler来处理服务器的事件。
		//ch.pipeline().addLast(handler);
		ch.pipeline().addLast(new CilentHandler());
	}
}
class CilentHandler extends ChannelInboundHandlerAdapter{

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		
	}

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		ByteBuf buf = Unpooled.copiedBuffer("hello dhl".getBytes());
		ctx.writeAndFlush(buf);
	}
	
}