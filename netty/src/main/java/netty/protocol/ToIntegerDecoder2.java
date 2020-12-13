package netty.protocol;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ReplayingDecoder;

import java.util.List;

/**
 * 扩展ReplayingDecoder<Void>以将字节解码为消息
 */
public class ToIntegerDecoder2 extends ReplayingDecoder<Void> {

	@Override
	public void decode(ChannelHandlerContext ctx, ByteBuf in, //传入的ByteBuf 是ReplayingDecoderByteBuf
	                   List<Object> out) throws Exception {
		out.add(in.readInt()); //从入站ByteBuf 中读取一个int，并将其添加到解码消息的List 中
	}
}
