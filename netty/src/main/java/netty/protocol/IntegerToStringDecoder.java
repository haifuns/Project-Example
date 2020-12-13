package netty.protocol;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;

import java.util.List;

public class IntegerToStringDecoder extends MessageToMessageDecoder<Integer> {

	@Override
	public void decode(ChannelHandlerContext ctx, Integer msg, List<Object> out) throws Exception {
		out.add(String.valueOf(msg)); //将Integer 消息转换为String 表示并将其添加到输出List 中
	}
}
