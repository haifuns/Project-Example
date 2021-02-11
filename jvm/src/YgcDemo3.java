/**
 * GC年龄阈值判定模拟
 * -XX:NewSize=10485760 -XX:MaxNewSize=10485760 -XX:InitialHeapSize=20971520 -XX:MaxHeapSize=20971520 -XX:SurvivorRatio=8  -XX:MaxTenuringThreshold=15 -XX:PretenureSizeThreshold=10485760 -XX:+UseParNewGC -XX:+UseConcMarkSweepGC -XX:+PrintGCDetails -XX:+PrintGCTimeStamps -Xloggc:ygc3.log
 */
public class YgcDemo3 {

	/**
	 * 15次GC后进入老年代
	 */
	public static void main(String[] args) {

		byte[] array1 = new byte[64 * 1024];

		for (int i = 0; i < 9; i++) {                  // 1 2 3 4 5 6  7  8  9
			byte[] array2 = new byte[2 * 1024 * 1024]; //   2 4 6 8 10 12 14 16
			array2 = null;
			byte[] array3 = new byte[6 * 1024 * 1024]; // 1 3 5 7 9 11 13 15 17
			array3 = null;
		}
	}
}
