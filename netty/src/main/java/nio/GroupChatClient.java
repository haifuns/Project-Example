package nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Scanner;

/**
 * 客户端
 */
public class GroupChatClient {

	private final String HOST = "127.0.0.1";
	private final int PORT = 6667;
	private Selector selector;
	private SocketChannel socketChannel;
	private String username;

	public GroupChatClient() {
		try {
			selector = Selector.open();
			// 连接服务器
			socketChannel = SocketChannel.open(new InetSocketAddress(HOST, PORT));
			// 非阻塞
			socketChannel.configureBlocking(false);
			// 将channel注册到selector
			socketChannel.register(selector, SelectionKey.OP_READ);
			username = socketChannel.getLocalAddress().toString().substring(1);
			System.out.println(username + " 准备完毕");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void sendInfo(String msg) {
		msg = username + "： " + msg;
		try {
			socketChannel.write(ByteBuffer.wrap(msg.getBytes()));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void readMsg() {
		try {
			int readChannels = selector.select();
			// 有可以用的通道
			if (readChannels > 0) {
				Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
				while(iterator.hasNext()) {
					SelectionKey key = iterator.next();
					if (key.isReadable()) {
						SocketChannel socketChannel = (SocketChannel) key.channel();
						ByteBuffer buffer = ByteBuffer.allocate(1024);
						// 从channel读出消息
						socketChannel.read(buffer);
						String msg = new String(buffer.array());
						System.out.println(msg);
					}
					iterator.remove();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		GroupChatClient chatClient = new GroupChatClient();

		new Thread(new Runnable() {
			@Override
			public void run() {
				while (true) {
					chatClient.readMsg();
					try {
						Thread.sleep(3000);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}).start();

		Scanner scanner = new Scanner(System.in);
		while (scanner.hasNextLine()) {
			String s = scanner.nextLine();
			chatClient.sendInfo(s);
		}
	}
}
