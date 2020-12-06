package nio;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

public class FileChannelDemo4 {

	public static void main(String[] args) throws IOException {
		FileInputStream fileInputStream = new FileInputStream("a.png");
		FileOutputStream fileOutputStream = new FileOutputStream("b.png");
		// 获取各个流对应的fileChannel
		FileChannel sourceCh = fileInputStream.getChannel();
		FileChannel destCh = fileOutputStream.getChannel();
		// 使用 transferForm 完成拷贝
		destCh.transferFrom(sourceCh,0,sourceCh.size());
		// 关闭相关通道和流
		sourceCh.close();
		destCh.close();
		fileInputStream.close();
		fileOutputStream.close();
	}
}
