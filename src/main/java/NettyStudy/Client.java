package NettyStudy;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
//netty�ж����첽����
public class Client {
	public static void main(String[] args){
		//�̳߳أ�event:������¼���Loop:ѭ��ִ�У�group���顣�¼�������̳߳�
		//NioEventLoopGroup(args)��args��ʾ�߳�������Ĭ���Ǻ���*2
		EventLoopGroup group = new NioEventLoopGroup();
		//Bootstrap������������
		Bootstrap b = new Bootstrap();
//		try {
//			//���̳߳ؼ��ص������ࡣ
//			b.group(group)
//			.channel(NioSocketChannel.class)//ָ�����ӵ���������channel�����ͣ����Ի���NioSocketChannelΪSockeChannelת��Ϊ������ʽ
//			//handler:�����channel�����¼������Ǹ�handler����ChannelInitializer��channel��ʼ������
//			.handler(new ClientChannelInitislizer())
//			.connect("localhost",8889)//connect���첽����������ִ����ֱ��ִ����һ�����������ӳɹ��Ƿ�����û����sync����������
//			.sync();//�÷����ж��Ƿ����ӳɹ�����Ȼ������ִ��ֱ�����������쳣����
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}finally{
//			group.shutdownGracefully();
//		}
		//����sync����������д��
		try {
			//û��sync����������д������ChannelFuture�����������ӽ��
			ChannelFuture future = b.group(group)
			.channel(NioSocketChannel.class)
			.handler(new ClientChannelInitislizer())
			.connect("localhost",8889);
			//��Ӽ��������۲�����ʲô�������һ��������operationComplete������
			//ΪʲôҪ�Ӽ�������connect()���Ӳ�һ��ִ����ɾ��Ѿ�����ִ���ˣ�
			future.addListener(new ChannelFutureListener() {
				@Override
				public void operationComplete(ChannelFuture future) throws Exception {
					//���û�ɹ�
					if(!future.isSuccess()){
						System.out.println("not connected!");
					}else{
						System.out.println("has connected!");
					}
				}
			});
		} catch (Exception e) {
			// TODO: handle exception
		}
		
	}
}

class ClientChannelInitislizer extends ChannelInitializer<SocketChannel>{

	@Override
	protected void initChannel(SocketChannel ch) throws Exception {
		System.out.println("channel has initislizer!"+ch);
		//ch��ʼ���ɹ��󣬵����Զ����handler��������������¼���
		//ch.pipeline().addLast(handler);
	}
	
}