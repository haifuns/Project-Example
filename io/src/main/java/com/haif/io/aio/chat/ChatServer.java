package com.haif.io.aio.chat;

import java.io.Closeable;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ChatServer {

	private final String DEFAULT_HOST = "localhost";
	private final int DEFAULT_PORT = 8888;
	private final String QUIT = "quit";
	private final int DEFAULT_BUFFER = 1024;
	private final int DEFAULT_THREAD_POOL_SIZE = 8;

	private AsynchronousChannelGroup channelGroup;
	private AsynchronousServerSocketChannel serverChannel;
	private List<ClientHandler> connectClients;
	private Charset charset = StandardCharsets.UTF_8;

	public ChatServer() {
		this.connectClients = new ArrayList<>();
	}

	public boolean readyToQuit(String msg) {
		return QUIT.equals(msg);
	}

	public static void close(Closeable closeable) {
		if (closeable != null) {
			try {
				closeable.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void start() {

		try {
			ExecutorService executorService = Executors.newFixedThreadPool(DEFAULT_THREAD_POOL_SIZE);
			channelGroup = AsynchronousChannelGroup.withThreadPool(executorService);

			serverChannel = AsynchronousServerSocketChannel.open(channelGroup);
			serverChannel.bind(new InetSocketAddress(DEFAULT_HOST, DEFAULT_PORT));

			System.out.println("启动服务器, 监听端口: " + DEFAULT_PORT);

			while(true) {
				serverChannel.accept(null, new AcceptHandle());

				System.in.read();
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			close(serverChannel);
		}
	}

	private class AcceptHandle implements CompletionHandler<AsynchronousSocketChannel, Object> {

		@Override
		public void completed(AsynchronousSocketChannel clientChannel, Object attachment) {
			if (serverChannel.isOpen()) {
				serverChannel.accept(null, this);
			}

			if (clientChannel.isOpen()) {
				ByteBuffer buffer = ByteBuffer.allocate(DEFAULT_BUFFER);

				System.out.println(getClientName(clientChannel) + ": 已连接");

				ClientHandler clientHandler = new ClientHandler(clientChannel);
				// 将新用户添加到在线用户列表
				connectClients.add(clientHandler);
				clientChannel.read(buffer, buffer, clientHandler);
			}

		}

		@Override
		public void failed(Throwable exc, Object attachment) {
			System.out.println("连接失败: " + exc);
		}
	}

	private class ClientHandler implements CompletionHandler<Integer, Object> {

		private AsynchronousSocketChannel clientChannel;

		public ClientHandler(AsynchronousSocketChannel clientChannel) {
			this.clientChannel = clientChannel;
		}

		@Override
		public void completed(Integer result, Object attachment) {
			ByteBuffer buffer = (ByteBuffer)attachment;
			if (buffer != null) {
				// read
				if (result <= 0) {
					// 客户端异常
					connectClients.remove(this);
				} else {
					buffer.flip();
					String fwdMsg = receive(buffer);
					System.out.println(getClientName(clientChannel) + ": " + fwdMsg);
					forwardMsg(clientChannel, getClientName(clientChannel) + ": " + fwdMsg);
					buffer.clear();

					if (readyToQuit(fwdMsg)) {
						try {
							clientChannel.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
						System.out.println(getClientName(clientChannel) + "已断开连接");
					} else {
						clientChannel.read(buffer, buffer,this);
					}
				}
			}
		}

		@Override
		public void failed(Throwable exc, Object attachment) {

		}
	}

	public String getClientName(AsynchronousSocketChannel clientChannel) {
		try {
			return "客户端[" + ((InetSocketAddress)clientChannel.getRemoteAddress()).getPort() + "]";
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "";
	}

	public String receive(ByteBuffer buffer) {
		return String.valueOf(charset.decode(buffer));
	}

	public void forwardMsg(AsynchronousSocketChannel clientChannel, String fwdMsg) {
		for (ClientHandler handler : connectClients) {
			if (!handler.clientChannel.equals(clientChannel)) {

				try {
					//将要转发的信息写入到缓冲区中
					ByteBuffer buffer = charset.encode(getClientName(handler.clientChannel)+ ":" + fwdMsg);
					//将相应的信息写入到用户通道中,用户再通过获取通道中的信息读取到对应转发的内容
					handler.clientChannel.write(buffer, null, handler);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	public static void main(String[] args) {
		ChatServer server = new ChatServer();
		server.start();
	}
}
