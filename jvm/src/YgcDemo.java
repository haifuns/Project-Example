/**
 * Young GC模拟
 * -XX:NewSize=5242880 -XX:MaxNewSize=5242880 -XX:InitialHeapSize=10485760 -XX:MaxHeapSize=10485760 -XX:SurvivorRatio=8 -XX:PretenureSizeThreshold=10485760 -XX:+UseParNewGC -XX:+UseConcMarkSweepGC -XX:+PrintGCDetails -XX:+PrintGCTimeStamps -Xloggc:ygc.log
 */
public class YgcDemo {

    /**
     * -XX:NewSize=5242880 新生代5MB
     * -XX:MaxNewSize=5242880 新生代最大5MB
     * -XX:InitialHeapSize=10485760 初始堆10MB
     * -XX:MaxHeapSize=10485760 最大堆10MB
     * -XX:SurvivorRatio=8 Eden:S1:S2=8:1:1即Eden4M,Survivor每块0.5M
     * -XX:PretenureSizeThreshold=10485760 大对象阈值10MB
     * -XX:+UseParNewGC 新生代使用ParNew收集器
     * -XX:+UseConcMarkSweepGC 老年代使用CMS收集器
     * -XX:+PrintGCDetails 打印详细GC日志
     * -XX:+PrintGCTimeStamps 打印每次GC发生的时间
     * -Xloggc:ygc.log 指定GC日志
     */
    public static void main(String[] args) {
        // Eden +1M
        byte[] array1 = new byte[1024 * 1024];
        // Eden +1M
        array1 = new byte[1024 * 1024];
        // Eden +1M
        array1 = new byte[1024 * 1024];
        // 前面3个数组成为垃圾对象
        array1 = null;

        // Eden剩余空间不足2M, 触发YGC
        byte[] array2 = new byte[2 * 1024 * 1024];
    }
}
