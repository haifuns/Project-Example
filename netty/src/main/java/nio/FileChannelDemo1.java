package nio;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class FileChannelDemo1 {

	public static void main(String[] args) throws IOException {
		String message = "hello channel";
		// 创建一个输出流 -> channel
		FileOutputStream fileOutputStream = new FileOutputStream("d:\\file01.txt");
		// 通过fileOutputStream 获取对应的FileChannel, fileChannel 真实类型是FileChannelImpl
		FileChannel fileChannel = fileOutputStream.getChannel();
		// 创建缓冲区ByteBuffer
		ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
		// 将文本放进byteBuffer
		byteBuffer.put(message.getBytes());
		// 对byteBuffer 进行flip
		byteBuffer.flip();
		// 将byteBuffer 数据写入fileChannel
		fileChannel.write(byteBuffer);
		fileOutputStream.close();
	}
}
