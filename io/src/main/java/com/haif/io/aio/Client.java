package com.haif.io.aio;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class Client {

	private final String DEFAULT_HOST = "localhost";
	private final int DEFAULT_PORT = 8888;
	AsynchronousSocketChannel clientChannel;

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
			Future<Void> future = clientChannel.connect(new InetSocketAddress(DEFAULT_HOST, DEFAULT_PORT));
			future.get();

			// 等待用户输入
			BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in));
			while(true) {
				String input = consoleReader.readLine();
				byte[] bytes = input.getBytes();
				ByteBuffer buffer = ByteBuffer.wrap(bytes);

				Future<Integer> writeResult = clientChannel.write(buffer);
				writeResult.get();
				buffer.flip();

				Future<Integer> readResult = clientChannel.read(buffer);
				readResult.get();

				String echo = new String(buffer.array());
				buffer.clear();

				System.out.println(echo);
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

	public static void main(String[] args) {
		Client client = new Client();
		client.start();
	}
}
