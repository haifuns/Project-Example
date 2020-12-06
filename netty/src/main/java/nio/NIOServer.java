package nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class NIOServer {

	public static void main(String[] args) throws IOException {
		// 创建 ServerSocketChannel -> ServerSocket
		ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
		// 得到一个 Selector 对象
		Selector selector = Selector.open();
		// 绑定一个端口6666, 在服务器端监听
		serverSocketChannel.socket().bind(new InetSocketAddress(6666));
		// 设置为非阻塞
		serverSocketChannel.configureBlocking(false);
		// 把serverSocketChannel注册到selector 关心事件为OP_ACCEPT
		serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

		// 循环等待客户端连接
		while (true) {
			// 等待1s没有事件发生就返回
			if (selector.select(1000) == 0) {
				System.out.println("服务器等待1s, 无连接");
				continue;
			}
			// 如果返回的>0, 表示已经获取到关注的事件, 就获取相关的selectionKey集合
			Set<SelectionKey> selectionKeys = selector.selectedKeys();

			Iterator<SelectionKey> iterator = selectionKeys.iterator();

			while(iterator.hasNext()) {
				// 获取到 SelectionKey
				SelectionKey key = iterator.next();
				// 根据 key 对应的通道发生的事件做相应处理
				// 如果是 OP_ACCEPT, 有新的客户端连接
				if (key.isAcceptable()) {
					// 给该客户端生成一个 SocketChannel
					SocketChannel socketChannel = serverSocketChannel.accept();
					System.out.println("客户端已连接, socketChannel: " + socketChannel.hashCode());
					// 将SocketChannel设置为非阻塞
					socketChannel.configureBlocking(false);
					// 将socketChannel注册到selector, 关注事件为OP_READ, 同时给socketChannel关联一个 Buffer
					socketChannel.register(selector, SelectionKey.OP_READ, ByteBuffer.allocate(1024));
				}

				// 发生OP_READ
				if (key.isReadable()) {
					// 通过key反向获取对应的channel
					SocketChannel channel = (SocketChannel) key.channel();
					// 获取到channel关联的buffer
					ByteBuffer buffer = (ByteBuffer) key.attachment();
					channel.read(buffer);
					System.out.println("客户端消息: " + new String(buffer.array()));

				}

				// 手动从集合中移动当前的selectionKey, 防止重复操作
				iterator.remove();
			}
		}
	}
}
