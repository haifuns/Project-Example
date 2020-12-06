package nio;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

/**
 * MappedByteBuffer 可让文件直接在内存(堆外内存)修改, 操作系统不需要拷贝一次
 */
public class MappedByteBufferTest {

	public static void main(String[] args) throws IOException {
		RandomAccessFile randomAccessFile = new RandomAccessFile("1.txt", "rw");
		// 获取对应的通道
		FileChannel fileChannel = randomAccessFile.getChannel();

		// param1: 读写模式, param2: 可以修改的起始位置, param3: 映射到内存的大小(不是索引大小)即1.txt有多少字节映射到内存
		MappedByteBuffer mappedByteBuffer = fileChannel.map(FileChannel.MapMode.READ_WRITE, 0, 5);

		mappedByteBuffer.put(0, (byte) 'A');
		mappedByteBuffer.put(3, (byte) 'B');
		// mappedByteBuffer.put(5, (byte) 'C'); // IndexOutOfBoundsException

		randomAccessFile.close();
	}
}
