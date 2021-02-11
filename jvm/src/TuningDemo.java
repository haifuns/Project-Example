/**
 * jstat 调优
 * -XX:NewSize=104857600 -XX:MaxNewSize=104857600 -XX:InitialHeapSize=209715200 -XX:MaxHeapSize=209715200 -XX:SurvivorRatio=8  -XX:MaxTenuringThreshold=15 -XX:PretenureSizeThreshold=3145728 -XX:+UseParNewGC -XX:+UseConcMarkSweepGC -XX:+PrintGCDetails -XX:+PrintGCTimeStamps -Xloggc:tgc.log
 */
public class TuningDemo {

	/**
	 * 整堆200MB, 年轻代100MB, Eden 80MB, 每块Survivor 10MB, 老年代100MB
	 */
	public static void main(String[] args) throws InterruptedException {
		Thread.sleep(30000);

		while(true) {
			loadData();
		}
	}

	private static void loadData() throws InterruptedException {
		byte[] data = null;
		for (int i = 0; i < 50; i++) { // 模拟50个请求
			data = new byte[100 * 1024]; // 模拟加载100KB数据
		}
		data = null;

		Thread.sleep(1000); // 模拟一秒左右发生
	}
}
