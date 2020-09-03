package com.haif.io;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

	public static void main(String[] args) throws IOException{

		// 创建ServerSocket
		ServerSocket serverSocket = new ServerSocket(8082);

		System.out.println("服务器启动,监听端口:8082");

		while (true) {
			// 监听，阻塞，等待客户端连接
			final Socket socket = serverSocket.accept();
			System.out.println("客户端["+socket.getPort()+"]已连接");

			BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

			// 读取客户端发送的消息
			String msg = null;
			while ((msg = reader.readLine()) != null) {

				System.out.println("收到客户端["+ socket.getPort() + "]消息: " + msg);

				// 回复客户端
				writer.write("server resp: " + msg + "\n");
				writer.flush();

				if ("quit".equals(msg)) {
					System.out.println("客户端["+ socket.getPort() + "]已断开连接");
					break;
				}
			}
		}
	}
}
