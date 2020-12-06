package nio;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class FileChannelDemo2 {

	public static void main(String[] args) throws IOException {
		// 创建文件流
		File file = new File("d:\\file01.txt");
		FileInputStream fileInputStream = new FileInputStream(file);
		// 通过fileInputStream 获取对应的FileChannel -> 实际类型FileChannelImpl
		FileChannel fileChannel = fileInputStream.getChannel();
		// 创建缓冲区
		ByteBuffer byteBuffer = ByteBuffer.allocate((int) file.length());
		//将通道的数据读入到byteBuffer
		fileChannel.read(byteBuffer);
		System.out.println(new String(byteBuffer.array()));
		fileInputStream.close();
	}
}
