package com.haif.io.bio.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class ChatHandler implements Runnable {

	private ChatServer chatServer;
	private Socket socket;

	public ChatHandler(ChatServer chatServer, Socket socket) {
		this.chatServer = chatServer;
		this.socket = socket;
	}

	@Override
	public void run() {
		try {
			// 存储新上线用户
			chatServer.addClient(socket);

			// 读取用户发送的消息
			BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

			String msg = null;
			while((msg = reader.readLine()) != null) {
				String fwdMsg = "客户端:" + socket.getPort() + "消息:" + msg + "\n";
				System.out.println(fwdMsg);

				// 消息转发
				chatServer.forwardMessage(socket, fwdMsg);

				// 检查用户退出
				if (chatServer.readyToQuit(msg)) {
					break;
				}
			}

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				chatServer.removeClient(socket);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
