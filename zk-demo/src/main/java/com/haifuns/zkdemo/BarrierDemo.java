package com.haifuns.zkdemo;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.barriers.DistributedBarrier;
import org.apache.curator.retry.ExponentialBackoffRetry;

/**
 * @author haifuns
 * @date 2021/11/30 23:18
 */
public class BarrierDemo {

    public static void main(String[] args) throws Exception {
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
        CuratorFramework client = CuratorFrameworkFactory.newClient("192.168.0.13:2181", retryPolicy);
        client.start();

        DistributedBarrier distributedBarrier = new DistributedBarrier(client, "/barrier");
        distributedBarrier.waitOnBarrier();

        Thread.sleep(Integer.MAX_VALUE);
    }
}
