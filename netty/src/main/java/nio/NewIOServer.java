package nio;

import java.io.FileInputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

public class NewIOServer {
	public static void main(String[] args) throws Exception {
		InetSocketAddress address = new InetSocketAddress(7001);
		ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
		ServerSocket serverSocket = serverSocketChannel.socket();
		serverSocket.bind(address);

		ByteBuffer byteBuffer = ByteBuffer.allocate(4096);

		while (true) {
			SocketChannel socketChannel = serverSocketChannel.accept();
			int readCount = 0;
			while (-1 != readCount) {
				try {
					readCount = socketChannel.read(byteBuffer);
				} catch (Exception ex) {
					ex.printStackTrace();
					break;
				}

				// 倒带, position = 0, mark作废
				byteBuffer.rewind();
			}
		}
	}
}

class NewIOClient {
	public static void main(String[] args) throws Exception {
		SocketChannel socketChannel = SocketChannel.open();
		socketChannel.connect(new InetSocketAddress("localhost", 7001));
		String filename = "test1.zip";
		// 得到一个文件 channel
		FileChannel fileChannel = new FileInputStream(filename).getChannel();
		// 准备发送
		long startTime = System.currentTimeMillis();
		// linux下, 一个transferTo方法就可以完成传输
		// windows下, 一次调用transferTo只能发送8m, 超过8m需要分段传输文件
		int length = (int) fileChannel.size();
		int count = length / (8 * 1024 * 1024 ) + 1;
		long transferCount = 0;
		for (int i = 0; i < count; i++) {
			// transferTo 底层使用到零拷贝
			transferCount += fileChannel.transferTo(transferCount, fileChannel.size(), socketChannel);
		}

		System.out.println("发送的总字节数= " + transferCount + ", 耗时: " + (System.currentTimeMillis() - startTime));
		// 关闭
		fileChannel.close();
	}
}
