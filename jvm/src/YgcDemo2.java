/**
 * 动态年龄判定模拟
 * -XX:NewSize=10485760 -XX:MaxNewSize=10485760 -XX:InitialHeapSize=20971520 -XX:MaxHeapSize=20971520 -XX:SurvivorRatio=8  -XX:MaxTenuringThreshold=15 -XX:PretenureSizeThreshold=10485760 -XX:+UseParNewGC -XX:+UseConcMarkSweepGC -XX:+PrintGCDetails -XX:+PrintGCTimeStamps -Xloggc:ygc2.log
 */
public class YgcDemo2 {

	/**
	 * 动态年龄判定进入老年代模拟
	 * 参数配置: 总堆20MB, 新生代10MB, Eden:From Survivor:To Survivor=8:1:1, 老年代10MB, 大对象直接进老年代阈值10MB, GC年龄达到15进入老年代
	 *
	 * 对象进入老年代时机:
	 * 1. gc年龄达到15次
	 * 2. 动态年龄判定，如果Survivor区域内年龄1+年龄2+···+年龄n的对象总和大于Survivor区的50%, 此时年龄n以上的对象进入老年代
	 * 3. 如果一次Young GC后存活的对象太多无法放入Survivor区则直接进入老年代
	 * 4. 大对象直接进入老年代
	 */
	public static void main(String[] args) {
		byte[] array1 = new byte[2 * 1024 * 1024];
		array1 = new byte[2 * 1024 * 1024];
		array1 = new byte[2 * 1024 * 1024];
		array1 = null;

		byte[] array2 = new byte[128 * 1024];

		// 第一次YGC
		byte[] array3 = new byte[2 * 1024 * 1024];

		array3 = new byte[2 * 1024 * 1024];
		array3 = new byte[2 * 1024 * 1024];
		array3 = new byte[128 * 1024];

		// 第二次YGC, 触发Survivor区动态年龄判定
		byte[] array4 = new byte[2 * 1024 * 1024];
	}
}
