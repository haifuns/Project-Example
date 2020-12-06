package nio;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class FileChannelDemo3 {
	public static void main(String[] args) throws IOException {
		FileInputStream fileInputStream = new FileInputStream("1.txt");
		FileChannel inFileChannel = fileInputStream.getChannel();

		FileOutputStream fileOutputStream = new FileOutputStream("2.txt");
		FileChannel outFileChannel = fileOutputStream.getChannel();

		ByteBuffer buffer = ByteBuffer.allocate(512);

		// //循环读取
		while(true) {
			// 重置buffer
			buffer.clear();
			int read = inFileChannel.read(buffer);
			// 已读完
			if (read == -1) {
				break;
			}
			// 将buffer 中的数据写入到2.txt
			buffer.flip();
			outFileChannel.write(buffer);
		}

		fileInputStream.close();
		fileOutputStream.close();
	}
}
