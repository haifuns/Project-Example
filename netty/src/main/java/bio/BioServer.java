package bio;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BioServer {

	public static void main(String[] args) throws IOException {
		// 创建线程池
		ExecutorService executorService = Executors.newCachedThreadPool();

		// 创建ServerSocket
		ServerSocket serverSocket = new ServerSocket(8080);
		System.out.println("server start");

		while (true) {

			// 监听, 等待客户端连接
			final Socket socket = serverSocket.accept();;
			System.out.println("client connect, thread: "+ Thread.currentThread());

			executorService.execute(() -> handler(socket));
		}
	}

	// 与客户端通信
	public static void handler(Socket socket) {
		byte[] bytes = new byte[1024];

		// 通过socket获取输入流
		try (InputStream inputStream = socket.getInputStream()) {
			// 循环读取客户端发送的数据
			while (true) {
				int read = inputStream.read(bytes);
				if (read != -1) {
					System.out.println("receive message: " + new String(bytes, 0 , read) + ", thread: "+ Thread.currentThread());
				} else {
					break;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			System.out.println("client close, thread: "+ Thread.currentThread());
		}
	}
}
