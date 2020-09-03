package com.haif.io.nio;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class FileCopyDemo {

	public static void close(Closeable closeable) {
		if (closeable != null) {
			try {
				closeable.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static void benchmark(FileCopyRunner fileCopyRunner, String runnerName, File source, File target) {

		long elapsed = 0L;
		for (int i = 0; i < 5; i++) {
			long startTime = System.currentTimeMillis();
			fileCopyRunner.copyFile(source, target);
			elapsed += System.currentTimeMillis() - startTime;
			target.delete();
		}

		System.out.println(runnerName + ": " + elapsed / 5);
	}

	public static void main(String[] args) {

		FileCopyRunner noBufferStreamCopy = new FileCopyRunner() {
			@Override
			public void copyFile(File source, File target) {
				InputStream fin = null;
				OutputStream fout = null;

				try {
					fin = new FileInputStream(source);
					fout = new FileOutputStream(target);

					int result;
					while((result = fin.read()) != -1) {
						fout.write(result);
					}

				} catch (IOException e) {
					e.printStackTrace();
				} finally {
					close(fin);
					close(fout);
				}
			}
		};

		FileCopyRunner bufferedStreamCopy = new FileCopyRunner() {
			@Override
			public void copyFile(File source, File target) {
				InputStream fin = null;
				OutputStream fout = null;

				try {
					fin = new BufferedInputStream(new FileInputStream(source));
					fout = new BufferedOutputStream(new FileOutputStream(target));

					byte[] buffer = new byte[1024];

					int result;
					while ((result = fin.read(buffer)) != -1) {
						fout.write(buffer, 0 ,result);
					}
				} catch (IOException e) {
					e.printStackTrace();
				} finally {
					close(fin);
					close(fout);
				}
			}
		};

		FileCopyRunner nioBufferCopy = new FileCopyRunner() {
			@Override
			public void copyFile(File source, File target) {
				FileChannel fin = null;
				FileChannel fout = null;

				try {
					fin = new FileInputStream(source).getChannel();
					fout = new FileOutputStream(target).getChannel();

					ByteBuffer buffer = ByteBuffer.allocate(1024);

					while (fin.read(buffer) != -1) {
						// 读模式->写模式
						buffer.flip();
						while(buffer.hasRemaining()) {
							fout.write(buffer);
						}
						buffer.clear();
					}
				} catch (IOException e) {
					e.printStackTrace();
				} finally {
					close(fin);
					close(fout);
				}
			}
		};

		FileCopyRunner nioTransferCopy = new FileCopyRunner() {
			@Override
			public void copyFile(File source, File target) {
				FileChannel fin = null;
				FileChannel fout = null;

				try {
					fin = new FileInputStream(source).getChannel();
					fout = new FileOutputStream(target).getChannel();

					long transferred = 0L;
					long size = fin.size();
					while (transferred != size) {
						transferred += fin.transferTo(0, size, fout);
					}
				} catch (IOException e) {
					e.printStackTrace();
				} finally {
					close(fin);
					close(fout);
				}
			}
		};

		File smallFile = new File("D:/GIT/Project-Example/io/src/main/resources/small.txt");
		File smallFileCopy = new File("D:/GIT/Project-Example/io/src/main/resources/smallcopy.txt");

		System.out.println("===Copying small file===");
		benchmark(noBufferStreamCopy, "noBufferStreamCopy", smallFile, smallFileCopy);
		benchmark(bufferedStreamCopy, "bufferedStreamCopy", smallFile, smallFileCopy);
		benchmark(nioBufferCopy, "nioBufferCopy", smallFile, smallFileCopy);
		benchmark(nioTransferCopy, "nioTransferCopy", smallFile, smallFileCopy);

		System.out.println("===Copying big file===");

		File bigFile = new File("D:/GIT/Project-Example/io/src/main/resources/big.txt");
		File bigFileCopy = new File("D:/GIT/Project-Example/io/src/main/resources/bigcopy.txt");

		//benchmark(noBufferStreamCopy, "noBufferStreamCopy", bigFile, bigFileCopy);
		benchmark(bufferedStreamCopy, "bufferedStreamCopy", bigFile, bigFileCopy);
		benchmark(nioBufferCopy, "nioBufferCopy", bigFile, bigFileCopy);
		benchmark(nioTransferCopy, "nioTransferCopy", bigFile, bigFileCopy);

		System.out.println("===Copying huge file===");

		File hugeFile = new File("D:/GIT/Project-Example/io/src/main/resources/huge.txt");
		File hugeFileCopy = new File("D:/GIT/Project-Example/io/src/main/resources/hugecopy.txt");

		//benchmark(noBufferStreamCopy, "noBufferStreamCopy", hugeFile, hugeFileCopy);
		benchmark(bufferedStreamCopy, "bufferedStreamCopy", hugeFile, hugeFileCopy);
		benchmark(nioBufferCopy, "nioBufferCopy", hugeFile, hugeFileCopy);
		benchmark(nioTransferCopy, "nioTransferCopy", hugeFile, hugeFileCopy);
	}
}
