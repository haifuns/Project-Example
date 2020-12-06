package nio;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class OldIOserver {
	public static void main(String[] args) throws IOException {
		ServerSocket serverSocket = new ServerSocket(7001);

		while(true) {
			Socket socket = serverSocket.accept();
			DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());

			try {
				byte[] bytes = new byte[4096];
				while(true) {
					int readCount = dataInputStream.read(bytes);
					if (-1 == readCount) {
						break;
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}

class OldIOClient {
	public static void main(String[] args) throws IOException {
		Socket socket = new Socket("127.0.0.1", 7001);

		FileInputStream fileInputStream = new FileInputStream("test1.zip");
		DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());

		byte[] bytes = new byte[4096];
		long readCount = 0;
		long total = 0;

		long startTime = System.currentTimeMillis();

		while((readCount = fileInputStream.read(bytes)) >= 0) {
			total += readCount;
			dataOutputStream.write(bytes);
		}

		System.out.println("发送的总字节数= " + total + ", 耗时: " + (System.currentTimeMillis() - startTime));

		dataOutputStream.close();
		socket.close();
		fileInputStream.close();
	}
}
