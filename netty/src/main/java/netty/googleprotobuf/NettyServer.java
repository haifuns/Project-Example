package netty.googleprotobuf;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.util.CharsetUtil;

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
				.childHandler(new ChannelInitializer<SocketChannel>() {//创建一个通道初始化对象(匿名对象)
					//给pipeline 设置处理器
					@Override
					protected void initChannel(SocketChannel ch) throws Exception {

						ChannelPipeline pipeline = ch.pipeline();
						//在pipeline加入ProtoBufDecoder
						//指定对哪种对象进行解码
						pipeline.addLast("decoder", new ProtobufDecoder(DataInfo.Message.getDefaultInstance()));
						pipeline.addLast(new NettyServerHandler());
					}
				}); // 给workGroup 的EventLoop 对应的管道设置处理器

		System.out.println("服务器已准备就绪");

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
class NettyServerHandler extends SimpleChannelInboundHandler<DataInfo.Message> {

	/**
	 * 读取数据实际(这里可以读取客户端发送的消息)
	 * @param ctx 上下文对象, 含有管道pipeline, 通道channel, 地址
	 * @param msg 客户端发送的数据
	 */
	@Override
	public void channelRead0(ChannelHandlerContext ctx, DataInfo.Message msg) throws Exception {

		//根据dataType 来显示不同的信息
		DataInfo.Message.DataType dataType = msg.getDataType();
		if(dataType == DataInfo.Message.DataType.StudentType) {
			DataInfo.Student student = msg.getStudent();
			System.out.println("学生 id=" + student.getId() + " 姓名=" + student.getName());
		} else if(dataType == DataInfo.Message.DataType.WorkerType) {
			DataInfo.Worker worker = msg.getWorker();
			System.out.println("工人 姓名=" + worker.getName() + " 年龄=" + worker.getAge());
		} else {
			System.out.println("传输的类型不正确");
		}
	}

	/**
	 * 数据读取完毕
	 */
	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
		// writeAndFlush 是write + flush
		// 将数据写入到缓存, 并刷新
		ctx.writeAndFlush(Unpooled.copiedBuffer("bye ~", CharsetUtil.UTF_8));
	}
}
