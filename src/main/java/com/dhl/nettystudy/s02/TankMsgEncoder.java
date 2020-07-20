package com.dhl.nettystudy.s02;
//放到客户端的channelinitlize时候加到pipeline责任链中
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
//编码器，MessageToByteEncoder把消息编码成字节， 把TankMsg类信息编码成字节。
public class TankMsgEncoder extends MessageToByteEncoder<TankMsg> {
	//重写编码方法，参数1、channel句柄上下文，2、编码对象，3、字符流对象。将msg写到buf中。
	@Override
	protected void encode(ChannelHandlerContext ctx, TankMsg msg, ByteBuf buf) throws Exception {
		buf.writeInt(msg.x);
		buf.writeInt(msg.y);
	}
}
