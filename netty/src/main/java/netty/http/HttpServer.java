package netty.http;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;

import java.net.URI;

public class HttpServer {

	public static void main(String[] args) {
		EventLoopGroup bossGroup = new NioEventLoopGroup();
		EventLoopGroup workerGroup = new NioEventLoopGroup();

		ServerBootstrap bootstrap = new ServerBootstrap();

		bootstrap.group(bossGroup, workerGroup)
				.channel(NioServerSocketChannel.class)
				.childHandler(new CustomHttpServerInitializer());

		try {
			ChannelFuture channelFuture = bootstrap.bind(8080).sync();
			channelFuture.channel().closeFuture().sync();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			bossGroup.shutdownGracefully();
			workerGroup.shutdownGracefully();
		}
	}
}

class CustomHttpServerInitializer extends ChannelInitializer<SocketChannel> {

	@Override
	protected void initChannel(SocketChannel ch) throws Exception {
		// 得到管道
		ChannelPipeline pipeline = ch.pipeline();

		// 加入一个netty提供的http编解码器
		pipeline.addLast("httpServerCodec", new HttpServerCodec());
		// 增加一个自定义handler
		pipeline.addLast(new CustomHttpServer());
	}
}

class CustomHttpServer extends SimpleChannelInboundHandler<HttpObject> {

	/**
	 * 读取客户端数据
	 */
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, HttpObject msg) throws Exception {
		if (msg instanceof HttpRequest) {
			System.out.println("pipeline hash = " + ctx.pipeline().hashCode() + "handler hash = " + this.hashCode());

			System.out.println("客户端地址: " + ctx.channel().remoteAddress());

			// 请求信息
			HttpRequest httpRequest = (HttpRequest) msg;
			// 获取URI, 过滤指定资源
			URI uri = new URI(httpRequest.uri());
			if ("/favicon.ico".equals(uri.getPath())) {
				return;
			}

			// 回复信息给浏览器
			ByteBuf content = Unpooled.copiedBuffer("hello ~", CharsetUtil.UTF_8);

			// 构造http响应, 即httpResponse
			FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, content);

			response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/plain");
			response.headers().set(HttpHeaderNames.CONTENT_LENGTH, content.readableBytes());

			ctx.writeAndFlush(response);
		}
	}
}
