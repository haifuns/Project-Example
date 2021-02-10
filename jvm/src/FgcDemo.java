/**
 * Full GC
 * -XX:NewSize=10485760 -XX:MaxNewSize=10485760 -XX:InitialHeapSize=20971520 -XX:MaxHeapSize=20971520 -XX:SurvivorRatio=8  -XX:MaxTenuringThreshold=15 -XX:PretenureSizeThreshold=3145728 -XX:+UseParNewGC -XX:+UseConcMarkSweepGC -XX:+PrintGCDetails -XX:+PrintGCTimeStamps -Xloggc:fgc.log
 */
public class FgcDemo {

	/**
	 * 整堆20MB, 新生代10MB, 大对象晋升老年代阈值3MB
	 */
	public static void main(String[] args) {
		// 大对象直接晋升老年代
		byte[] array1 = new byte[4 * 1024 * 1024];
		array1 = null;

		byte[] array2 = new byte[2 * 1024 * 1024];
		byte[] array3 = new byte[2 * 1024 * 1024];
		byte[] array4 = new byte[2 * 1024 * 1024];
		byte[] array5 = new byte[128 * 1024];

		// Eden区空间不足触发Young GC(历次YGC平均存活对象为0空间担保成功), YGC后空间仍不足, 所有对象晋升老年代, 此时老年代空间不足触发Full GC
		byte[] array6 = new byte[2 * 1024 * 1024];
	}
}
