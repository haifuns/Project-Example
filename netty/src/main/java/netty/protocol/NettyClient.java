package netty.protocol;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.protobuf.ProtobufEncoder;

import java.nio.charset.StandardCharsets;

public class NettyClient {
	public static void main(String[] args) {
		// 客户端需要一个事件循环组
		EventLoopGroup group = new NioEventLoopGroup();

		// 创建客户端启动对象
		// 注意客户端使用的不是ServerBootstrap 而是Bootstrap
		Bootstrap bootstrap = new Bootstrap();
		// 设置相关参数
		bootstrap.group(group) //设置线程组
				.channel(NioSocketChannel.class) // 设置客户端通道的实现类(反射)
				.handler(new ChannelInitializer<SocketChannel>() {
					@Override
					protected void initChannel(SocketChannel ch) throws Exception {
						ChannelPipeline pipeline = ch.pipeline();
						pipeline.addLast(new MyMessageEncoder()); //加入编码器
						pipeline.addLast(new MyMessageDecoder()); //加入解码器
						pipeline.addLast(new NettyClientHandler()); //加入自己的处理器
					}
				});

		System.out.println("客户端准备就绪");

		try {
			// 启动客户端去连接服务器端
			ChannelFuture channelFuture = bootstrap.connect("127.0.0.1", 8080).sync();
			// 监听关闭通道
			channelFuture.channel().closeFuture().sync();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			group.shutdownGracefully();
		}
	}
}

class NettyClientHandler extends SimpleChannelInboundHandler<MessageProtocol> {

	private int count;

	/**
	 * 当通道就绪就会触发该方法
	 */
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		for(int i = 0; i < 5; i++) {
			String msg = "hello ~ " + i;
			byte[] content = msg.getBytes(StandardCharsets.UTF_8);
			int length = msg.getBytes(StandardCharsets.UTF_8).length;

			//创建协议包对象
			MessageProtocol messageProtocol = new MessageProtocol();
			messageProtocol.setLen(length);
			messageProtocol.setContent(content);

			ctx.writeAndFlush(messageProtocol);
		}
	}

	/**
	 * 当通道有读取事件时触发
	 */
	@Override
	public void channelRead0(ChannelHandlerContext ctx, MessageProtocol msg) throws Exception {
		int len = msg.getLen();
		byte[] content = msg.getContent();

		System.out.println("=====================================================");
		System.out.println("客户端接收到消息, 长度=" + len + " 内容=" + new String(content, StandardCharsets.UTF_8));
		System.out.println("客户端接收消息包数量: " + (++this.count));
		System.out.println("=====================================================");
	}

	/**
	 * 异常事件
	 */
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		cause.printStackTrace();
		ctx.close();
	}
}