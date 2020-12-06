package nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.Set;

/**
 * 服务端
 */
public class GroupChatServer {

	private Selector selector;
	private ServerSocketChannel listenerChannel;
	private final int PORT = 6667;

	public GroupChatServer() {

		try {
			// 得到Selector
			selector = Selector.open();
			// 得到ServerSocketChannel
			listenerChannel = ServerSocketChannel.open();
			// 绑定端口
			listenerChannel.bind(new InetSocketAddress(PORT));
			// 设置非阻塞模式
			listenerChannel.configureBlocking(false);
			// 将该listenChannel注册到 selector
			listenerChannel.register(selector, SelectionKey.OP_ACCEPT);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void listener() {

		try {
			while(true) {
				int count = selector.select(2000);
				// 有事件处理
				if (count > 0) {
					// 遍历得到selectionKey集合
					Set<SelectionKey> selectionKeys = selector.selectedKeys();
					Iterator<SelectionKey> iterator = selectionKeys.iterator();

					while(iterator.hasNext()) {
						SelectionKey selectionKey = iterator.next();
						if (selectionKey.isAcceptable()) {
							// 监听到accept
							// 获得socketChannel
							SocketChannel socketChannel = listenerChannel.accept();
							// 非阻塞
							socketChannel.configureBlocking(false);
							//将该socketChannel注册到selector
							socketChannel.register(selector, SelectionKey.OP_READ);
							System.out.println(socketChannel.getRemoteAddress() + " 已上线");
						} else if (selectionKey.isReadable()) {
							readData(selectionKey);
						}

						// 防止重复处理
						iterator.remove();
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 读取客户端消息
	 */
	private void readData(SelectionKey selectionKey) {
		// 取到关联的socketChannel
		SocketChannel socketChannel = (SocketChannel) selectionKey.channel();
		ByteBuffer buffer = ByteBuffer.allocate(1024);
		try {
			// 读消息
			int count = socketChannel.read(buffer);
			if (count > 0) {
				String msg = new String(buffer.array());
				System.out.println(socketChannel.getRemoteAddress() + " 消息: " + msg);
				sendInfo(socketChannel, msg);
			}
		} catch (IOException e) {
			try {
				System.out.println(socketChannel.getRemoteAddress() + " 已下线");
				// 取消注册
				selectionKey.cancel();
				// 关闭通道
				socketChannel.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
			e.printStackTrace();
		}
	}

	/**
	 * 向其它的客户端转发消息
	 */
	private void sendInfo(SocketChannel selfChannel, String msg) {
		System.out.println("开始转发消息");
		try {
			// 遍历所有注册到selector上的socketChannel, 并排除self
			for (SelectionKey key : selector.keys()) {
				Channel channel = key.channel();
				if(channel instanceof SocketChannel && channel != selfChannel) {
					// 将消息写到channel
					((SocketChannel)channel).write(ByteBuffer.wrap(msg.getBytes()));
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("转发完成");
	}

	public static void main(String[] args) {
		GroupChatServer chatServer = new GroupChatServer();
		chatServer.listener();
	}
}
