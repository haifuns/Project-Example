package nio;

import java.nio.IntBuffer;

public class BasicBuffer {

	public static void main(String[] args) {

		// 创建一个IntBuff, 大小为5
		IntBuffer intBuffer = IntBuffer.allocate(5);

		// 向buffer存数据
		for (int i = 0; i < intBuffer.capacity(); i++) {
			intBuffer.put(i * 2);
		}

		// buffer转换, 读写转换
		intBuffer.flip();

		while (intBuffer.hasRemaining()) {
			System.out.println(intBuffer.get());
		}
	}
}
