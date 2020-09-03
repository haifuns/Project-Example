package com.haif.io.nio.chat;

import java.io.Closeable;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Set;

public class ChatServer {

	private final int DEFAULT_PORT = 8888;
	private final String QUIT = "quit";
	private final int DEFAULT_BUFFER = 1024;

	private ServerSocketChannel server;
	private Selector selector;
	private ByteBuffer rBuffer = ByteBuffer.allocate(DEFAULT_BUFFER);
	private ByteBuffer wBuffer = ByteBuffer.allocate(DEFAULT_BUFFER);
	private Charset charset = StandardCharsets.UTF_8;
	private int port;

	public ChatServer(int port) {
		this.port = port;
	}

	public void handles(SelectionKey key) throws IOException {
		// ACCEPT事件:与客户端建立连接
		if (key.isAcceptable()) {
			ServerSocketChannel server = (ServerSocketChannel) key.channel();
			SocketChannel client = server.accept();
			client.configureBlocking(false);
			client.register(selector, SelectionKey.OP_READ);
			System.out.println("客户端已连接, 端口:" + client.socket().getPort());
		}

		// READ: 客户端发送消息
		else if (key.isReadable()) {
			SocketChannel client = (SocketChannel) key.channel();
			String fwdMsg = receive(client);

			if (fwdMsg.isEmpty()) {
				// 客户端异常
				key.cancel();
				selector.wakeup();
				System.out.println("客户端连接异常, 已断开, 端口:" + client.socket().getPort());
			} else {
				forwardMsg(client, fwdMsg);

				if (readyToQuit(fwdMsg)) {
					key.cancel();
					selector.wakeup();
					System.out.println("客户端已断开连接, 端口:" + client.socket().getPort());
				}
			}
		}
	}

	public String receive(SocketChannel client) throws IOException {
		rBuffer.clear();
		while (client.read(rBuffer) > 0) {
			rBuffer.flip();
		}
		return String.valueOf(charset.decode(rBuffer));
	}

	public void forwardMsg(SocketChannel client, String msg) throws IOException {
		for (SelectionKey key : selector.keys()) {
			Channel channel = key.channel();
			if (channel instanceof ServerSocketChannel) {
				continue;
			}

			if (key.isValid() && !channel.equals(client)) {
				wBuffer.clear();
				wBuffer.put(charset.encode(msg));
				// 写转读
				wBuffer.flip();
				while (wBuffer.hasRemaining()) {
					((SocketChannel)channel).write(wBuffer);
				}
			}
		}
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
			server = ServerSocketChannel.open();
			// 设置非阻塞
			server.configureBlocking(false);
			server.socket().bind(new InetSocketAddress(port));

			selector = Selector.open();
			server.register(selector, SelectionKey.OP_ACCEPT);

			System.out.println("启动服务器， 监听端口:" + port);

			while (true) {
				selector.select();
				Set<SelectionKey> selectionKeys = selector.selectedKeys();

				for (SelectionKey selectionKey : selectionKeys) {
					// 处理被触发事件
					handles(selectionKey);
				}
				selectionKeys.clear();
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			close(selector);
		}
	}

	public static void main(String[] args) {
		ChatServer chatServer = new ChatServer(8888);
		chatServer.start();
	}
}
