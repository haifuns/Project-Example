package com.haif.io.nio.chat;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Set;

public class ChatClient {

	private final String DEFAULT_HOST = "127.0.0.1";
	private final int DEFAULT_PORT = 8888;
	private final String QUIT = "quit";
	private final int DEFAULT_BUFFER = 1024;

	private SocketChannel client;
	private Selector selector;
	private ByteBuffer rBuffer = ByteBuffer.allocate(DEFAULT_BUFFER);
	private ByteBuffer wBuffer = ByteBuffer.allocate(DEFAULT_BUFFER);
	private Charset charset = StandardCharsets.UTF_8;

	public boolean readyToQuit(String msg) {
		return QUIT.equals(msg);
	}

	public void start() {
		try {
			client = SocketChannel.open();
			client.configureBlocking(false);

			selector = Selector.open();

			client.register(selector, SelectionKey.OP_CONNECT);
			client.connect(new InetSocketAddress(DEFAULT_HOST, DEFAULT_PORT));

			while (true) {
				selector.select();
				Set<SelectionKey> selectionKeys = selector.selectedKeys();

				for (SelectionKey selectionKey : selectionKeys) {
					handles(selectionKey);
				}
				selectionKeys.clear();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void handles(SelectionKey key) throws IOException {
		// CONNECT 连接就绪
		if (key.isConnectable()) {
			SocketChannel client = (SocketChannel) key.channel();

			if (client.isConnectionPending()) {
				client.finishConnect();
				new Thread(new UserInputHandler(this)).start();
			}
			client.register(selector, SelectionKey.OP_READ);
		}
		// READ
		else if (key.isReadable()) {
			SocketChannel client = (SocketChannel) key.channel();
			String msg = receive(client);
			if (msg.isEmpty()) {
				close(selector);
			} else {
				System.out.println(msg);
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

	public void send(String msg) throws IOException {
		if (msg.isEmpty()) {
			return;
		}

		wBuffer.clear();
		wBuffer.put(charset.encode("客户端消息["+client.socket().getPort()+"]: " + msg));
		// 写转读
		wBuffer.flip();
		while (wBuffer.hasRemaining()) {
			client.write(wBuffer);
		}

		if (readyToQuit(msg)) {
			close(selector);
		}
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

	public static void main(String[] args) {
		ChatClient client = new ChatClient();
		client.start();
	}
}

class UserInputHandler implements Runnable {

	private ChatClient chatClient;

	public UserInputHandler(ChatClient chatClient) {
		this.chatClient = chatClient;
	}

	@Override
	public void run() {

		while (true) {

			try {
				// 等待用户输入
				BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in));
				String msg = null;
				msg = consoleReader.readLine();

				// 发送信息到服务器
				chatClient.send(msg);

				// 客户端退出
				if (chatClient.readyToQuit(msg)) {
					break;
				}
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
	}
}
