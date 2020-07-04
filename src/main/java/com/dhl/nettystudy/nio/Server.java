package com.dhl.nettystudy.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class Server {
	public static void main(String[] args) throws IOException{
		//ServerSocketChannel:全双工
		ServerSocketChannel ssc = ServerSocketChannel.open();
		ssc.socket().bind(new InetSocketAddress("127.0.0.1",8888));
		//设置他的阻塞态是false。默认是阻塞状态，true。
		ssc.configureBlocking(false);
		
		System.out.println("Server started ,listening on:"+ssc.getLocalAddress());
		//Selector ：大管家。轮询看下个有消息
		Selector selector = Selector.open();
		//注册selector
		ssc.register(selector, SelectionKey.OP_ACCEPT);
		while(true){
			selector.select(); //阻塞形式，有消息selector返回
			Set<SelectionKey> keySelectors = selector.selectedKeys();
			Iterator<SelectionKey> iterator = keySelectors.iterator();
			while(iterator.hasNext()){
				SelectionKey key = iterator.next();
				iterator.remove();
				handle(key);
			}
		}
	}

	private static void handle(SelectionKey key) {
		if(key.isAcceptable()){
			try {
				ServerSocketChannel ssc = (ServerSocketChannel)key.channel();
				SocketChannel sc = ssc.accept();
				sc.configureBlocking(false);
				sc.register(key.selector(),SelectionKey.OP_READ);
			} catch (Exception e) {
				e.printStackTrace();
			}finally{}
		}else if(key.isReadable()){
			SocketChannel sc = null;
			try {
				sc = (SocketChannel)key.channel();
				ByteBuffer buffer = ByteBuffer.allocate(512);
				buffer.clear();
				int len = sc.read(buffer);
				
				if(len != -1){
					System.out.println(new String(buffer.array(),0,len));
				}
				ByteBuffer btoWriteBuffer = ByteBuffer.wrap("HelloClient".getBytes());
				sc.write(btoWriteBuffer);
			} catch (Exception e) {
				e.printStackTrace();
			}finally{
				if(sc != null){
					try {
						sc.close();
					} catch (Exception e2) {
						e2.printStackTrace();
					}
				}
			}
		}
		
	}
}
