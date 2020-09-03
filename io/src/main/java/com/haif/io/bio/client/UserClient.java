package com.haif.io.bio.client;

import java.io.*;
import java.net.Socket;

public class UserClient {

	private final String DEFAULT_SERVER_HOST = "127.0.0.1";
	private int DEFAULT_PORT = 8888;
	private final String QUIT = "quit";

	private Socket socket;
	private BufferedReader reader;
	private BufferedWriter writer;

	// 发送消息
	public void sendMsg(String msg) throws IOException {
		if (!socket.isOutputShutdown()) {
			writer.write(msg + "\n");
			writer.flush();
		}
	}

	// 接收消息
	public String receiveMsg() throws IOException {
		String msg = null;
		if (!socket.isInputShutdown()) {
			msg = reader.readLine();
		}
		return msg;
	}

	// 检查退出
	public boolean readyToQuit(String msg) {
		return QUIT.equals(msg);
	}

	public void close() {
		if (writer != null) {
			try {
				writer.close();
				System.out.println("客户端"+ socket.getPort() +"已关闭");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void start() {
		try {
			socket = new Socket(DEFAULT_SERVER_HOST, DEFAULT_PORT);

			reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

			// 处理用户输入
			new Thread(new UserInputHandler(this)).start();

			// 处理服务器转发消息
			String msg = null;
			while ((msg = reader.readLine()) != null) {
				System.out.println(msg);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			close();
		}
	}

	public static void main(String[] args) {
		UserClient userClient = new UserClient();
		userClient.start();
	}
}
