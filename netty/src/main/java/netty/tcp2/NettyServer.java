package netty.tcp2;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.CharsetUtil;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class NettyServer {

	public static void main(String[] args) {

		// 创建两个线程组 bossGroup 和 workerGroup
		// bossGroup 只是处理连接请求, 真正的和客户端业务处理, 会交给workerGroup 完成
		// bossGroup 和 workerGroup 含有的子线程(NioEventLoop)的个数默认是实际cpu 核心数 * 2
		EventLoopGroup bossGroup = new NioEventLoopGroup(1);
		EventLoopGroup workerGroup = new NioEventLoopGroup();

		ServerBootstrap bootstrap = new ServerBootstrap();

		bootstrap.group(bossGroup, workerGroup) // 设置两个线程组
				.channel(NioServerSocketChannel.class) // 使用NioSocketChannel 作为服务器的通道实现
				.option(ChannelOption.SO_BACKLOG, 128) // 设置线程队列得到连接个数
				.childOption(ChannelOption.SO_KEEPALIVE, true) // 设置保持活动的连接状态
				.childHandler(new ChannelInitializer<SocketChannel>() { // 创建一个通道测试对象(匿名对象)
					@Override
					protected void initChannel(SocketChannel ch) throws Exception {
						ch.pipeline().addLast(new LineBasedFrameDecoder(1024)); //使用LineBasedFrameDecoder 解决粘包问题
						ch.pipeline().addLast(new StringDecoder());
						ch.pipeline().addLast(new StringEncoder());
						ch.pipeline().addLast(new NettyServerHandler());
					}
				}); // 给workGroup 的EventLoop 对应的管道设置处理器

		System.out.println("服务器已准备就绪...");

		try {
			// 启动服务器, 绑定端口并设置同步
			ChannelFuture channelFuture = bootstrap.bind(8080).sync();

			// 对关闭通道监听
			channelFuture.channel().closeFuture().sync();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			bossGroup.shutdownGracefully();
			workerGroup.shutdownGracefully();
		}
	}
}

/**
 * 自定义handler处理器
 */
class NettyServerHandler extends SimpleChannelInboundHandler<String> {

	private int count;

	/**
	 * 读取数据实际(这里可以读取客户端发送的消息)
	 * @param ctx 上下文对象, 含有管道pipeline, 通道channel, 地址
	 * @param msg 客户端发送的数据
	 */
	@Override
	public void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {

		System.out.println("=====================================================");
		System.out.println("服务器接收到数据: " + msg);
		System.out.println("服务器接收到消息量: " + (++this.count));
		System.out.println("=====================================================");

		ctx.writeAndFlush(msg + System.getProperty("line.separator"));
	}

	/**
	 * 数据读取完毕
	 */
	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
		// writeAndFlush 是write + flush
		// 将数据写入到缓存, 并刷新
		ctx.writeAndFlush("bye ~" + System.getProperty("line.separator"));
	}
}