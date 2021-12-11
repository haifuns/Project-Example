package com.haifuns.zkdemo;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.barriers.DistributedDoubleBarrier;
import org.apache.curator.retry.ExponentialBackoffRetry;

/**
 * @author haifuns
 * @date 2021/11/30 23:23
 */
public class DoubleBarrierDemo {

    public static void main(String[] args) throws Exception {
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
        CuratorFramework client = CuratorFrameworkFactory.newClient("192.168.0.13:2181", retryPolicy);
        client.start();

        DistributedDoubleBarrier doubleBarrier = new DistributedDoubleBarrier(client, "/barrier/double", 10);

        // 阻塞，直到10个任务都调用enter继续执行
        doubleBarrier.enter();
        // 阻塞，直到10个任务都调用leave继续执行
        doubleBarrier.leave();
    }
}
