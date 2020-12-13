package netty.protocol;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * 接受一个Short类型的实例作为消息，将它编码为Short的原始类型值，并将它写入ByteBuf 中
 * 其将随后被转发给ChannelPipeline中的下一个ChannelOutboundHandler
 * 每个传出的Short值都将会占用ByteBuf中的2字节。
 */
public class ShortToByteEncoder extends MessageToByteEncoder<Short> {

	@Override
	protected void encode(ChannelHandlerContext ctx, Short msg, ByteBuf out) throws Exception {
		out.writeShort(msg);
	}
}
