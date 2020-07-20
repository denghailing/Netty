package com.dhl.nettystudy.s02;
//�ŵ��ͻ��˵�channelinitlizeʱ��ӵ�pipeline��������
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
//��������MessageToByteEncoder����Ϣ������ֽڣ� ��TankMsg����Ϣ������ֽڡ�
public class TankMsgEncoder extends MessageToByteEncoder<TankMsg> {
	//��д���뷽��������1��channel��������ģ�2���������3���ַ������󡣽�msgд��buf�С�
	@Override
	protected void encode(ChannelHandlerContext ctx, TankMsg msg, ByteBuf buf) throws Exception {
		buf.writeInt(msg.x);
		buf.writeInt(msg.y);
	}
}
