package com.haif.io.bio.server;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ChatServer {

	private int DEFAULT_PORT = 8888;
	private final String QUIT = "quit";

	private ExecutorService executorService;
	private ServerSocket serverSocket;
	private Map<Integer, Writer> connectedClients;

	public ChatServer() {
		executorService = Executors.newFixedThreadPool(10);
		connectedClients = new HashMap<>();
	}

	public synchronized void addClient(Socket socket) throws IOException {
		if (socket != null) {
			int port = socket.getPort();
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
			connectedClients.put(port, writer);
			System.out.println("客户端:" + port + "已连接");
		}
	}

	public synchronized void removeClient(Socket socket) throws IOException {
		if (socket != null) {
			int port = socket.getPort();
			if (connectedClients.containsKey(port)) {
				connectedClients.get(port).close();
				connectedClients.remove(port);
				System.out.println("客户端:" + port + "已断开连接");
			}
		}
	}

	public synchronized void forwardMessage(Socket socket, String message) throws IOException {
		for (Integer p : connectedClients.keySet()) {
			if (!p.equals(socket.getPort())) {
				Writer writer = connectedClients.get(p);
				writer.write(message);
				writer.flush();
			}
		}
	}

	public void start() {
		try {
			serverSocket = new ServerSocket(DEFAULT_PORT);
			System.out.println("服务端已启动:" + DEFAULT_PORT);

			while(true) {
				// 等待客户端连接
				Socket socket = serverSocket.accept();

				// 创建ChatHandler线程
				executorService.execute(new ChatHandler(this, socket));
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			close();
		}
	}

	public void close() {
		if (serverSocket != null) {
			try {
				serverSocket.close();
				System.out.println("服务端已关闭:" + DEFAULT_PORT);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public boolean readyToQuit(String msg) {
		return QUIT.equals(msg);
	}

	public static void main(String[] args) {
		ChatServer chatServer = new ChatServer();
		chatServer.start();
	}
}
