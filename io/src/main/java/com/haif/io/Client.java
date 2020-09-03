package com.haif.io;

import java.io.*;
import java.net.Socket;

public class Client {

	public static void main(String[] args) throws IOException {

		// 创建Socket client
		Socket socket = new Socket("127.0.0.1", 8082);

		BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

		while (true) {

			// 等待用户输入
			BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in));
			String input = consoleReader.readLine();

			// 发送信息到服务器
			writer.write(input + "\n");
			writer.flush();

			// 读取服务器返回消息
			String resp = reader.readLine();
			System.out.println("服务器返回消息:" + resp);

			// 客户端退出
			if ("quit".equals(input)) {
				break;
			}
		}

	}
}
