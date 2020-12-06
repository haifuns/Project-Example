package nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Arrays;

/**
 * Scattering：将数据写入到buffer 时, 可以采用 buffer 数组，依次写入 [分散]
 * Gathering: 从buffer 读取数据时, 可以采用 buffer 数组，依次读
 */
public class ScatteringAndGatheringTest {

	public static void main(String[] args) throws IOException {
		//使用 ServerSocketChannel 和 SocketChannel 网络
		ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
		InetSocketAddress inetSocketAddress = new InetSocketAddress(7000);

		//绑定端口到 socket, 并启动
		serverSocketChannel.socket().bind(inetSocketAddress);

		//创建 buffer 数组
		ByteBuffer[] byteBuffers = new ByteBuffer[2];
		byteBuffers[0] = ByteBuffer.allocate(5);
		byteBuffers[1] = ByteBuffer.allocate(3);

		// 等客户端连接(telnet)
		SocketChannel socketChannel = serverSocketChannel.accept();
		// 假定从客户端接收8个字节
		int messageLength = 8;

		while (true) {

			int byteRead = 0;
			while(byteRead < messageLength) {
				long length = socketChannel.read(byteBuffers);
				byteRead += length;

				System.out.println("byteRead=" + byteRead);

				// 打印当前buffer 的position 和limit
				Arrays.stream(byteBuffers)
						.map(buffer -> "position=" + buffer.position() + ", limit=" + buffer.limit())
						.forEach(System.out::println);
			}

			// 将所有的 buffer 进行 flip
			Arrays.asList(byteBuffers).forEach(Buffer::flip);

			// 将数据读出显示到客户端
			long byteWrite = 0;
			while (byteWrite < messageLength) {
				long length = socketChannel.write(byteBuffers);
				byteWrite += length;
			}

			// 将所有的 buffer 进行 clear
			Arrays.asList(byteBuffers).forEach(Buffer::clear);

			System.out.println("byteRead=" + byteRead + ", byteWrite=" + byteWrite + ", messageLength=" + messageLength);
		}
	}
}
