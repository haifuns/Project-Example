package com.haif.io.aio.chat;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class ChatClient {

	private final String DEFAULT_HOST = "localhost";
	private final int DEFAULT_PORT = 8888;
	private final String QUIT = "quit";
	private final int DEFAULT_BUFFER = 1024;
	private Charset charset = StandardCharsets.UTF_8;
	private AsynchronousSocketChannel clientChannel;

	public static void close(Closeable closeable) {
		if (closeable != null) {
			try {
				closeable.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void start() {
		try {
			clientChannel = AsynchronousSocketChannel.open();
			clientChannel.connect(new InetSocketAddress(DEFAULT_HOST, DEFAULT_PORT));
			//启动一个新的线程，处理用户的输入
			new Thread(new UserInputHandler(this)).start();

			ByteBuffer byteBuffer = ByteBuffer.allocate(DEFAULT_BUFFER);
			// 读取服务器转发的消息
			while(true){
				//启动异步读操作，以从该通道读取到给定的缓冲器字节序列
				Future<Integer> readResult = clientChannel.read(byteBuffer);
				//Future的get方法返回读取的字节数或-1如果没有字节可以读取，因为通道已经到达流终止。
				int result = readResult.get();
				if(result <= 0){
					// 服务器异常
					System.out.println("服务器断开");
					close(clientChannel);
					// 0是正常退出，非0是不正常退出
					System.exit(1);
				}else {
					byteBuffer.flip(); //准备读取
					String msg = String.valueOf(charset.decode(byteBuffer));
					byteBuffer.clear();
					System.out.println(msg);
				}
			}

		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		} finally {
			close(clientChannel);
		}

	}

	public void send(String msg){
		if (msg.isEmpty()){
			//没有必要向服务器发送空白的消息从而占用资源
			return;
		}
		ByteBuffer byteBuffer = charset.encode(msg);
		Future<Integer> writeResult = clientChannel.write(byteBuffer);
		try {
			writeResult.get();
		} catch (InterruptedException | ExecutionException e) {
			System.out.println("消息发送失败");
			e.printStackTrace();
		}
	}

	private class UserInputHandler implements Runnable{
		private ChatClient chatclient;
		public UserInputHandler(ChatClient chatClient){
			this.chatclient = chatClient;
		}
		/**
		 *
		 */
		@Override
		public void run() {
			try {
				//等待用户输入的消息
				BufferedReader consoleReader = new BufferedReader(
						new InputStreamReader(System.in)
				);
				while(true){
					String input = consoleReader.readLine();
					//向服务器发送消息
					chatclient.send(input);
					//检查用户是否准备退出
					if(chatclient.readyToQuit(input)){
						break;
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public boolean readyToQuit(String msg) {
		return QUIT.equals(msg);
	}

	public static void main(String[] args) {
		ChatClient client = new ChatClient();
		client.start();
	}
}
