package com.haif.io.bio.client;

import java.io.*;

public class UserInputHandler implements Runnable {

	private UserClient userClient;

	public UserInputHandler(UserClient userClient) {
		this.userClient = userClient;
	}

	@Override
	public void run() {

		while (true) {

			try {
				// 等待用户输入
				BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in));
				String msg = null;
				msg = consoleReader.readLine();

				// 发送信息到服务器
				userClient.sendMsg(msg);

				// 客户端退出
				if (userClient.readyToQuit(msg)) {
					break;
				}
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
	}
}
