package com.haifuns.zkdemo.lock;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.locks.InterProcessSemaphoreV2;
import org.apache.curator.framework.recipes.locks.Lease;
import org.apache.curator.retry.ExponentialBackoffRetry;

import static com.haifuns.zkdemo.ZKConfigConstant.HOST;

/**
 * 信号量
 * @author haifuns
 * @date 2022/1/4 22:18
 */
public class SemaphoreDemo {

    public static void main(String[] args) throws Exception {
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
        CuratorFramework client = CuratorFrameworkFactory.newClient(HOST, retryPolicy);
        client.start();

        InterProcessSemaphoreV2 semaphore = new InterProcessSemaphoreV2(client, "/semaphore/semaphore_01", 3);
        // 获取信号
        Lease lease = semaphore.acquire();
        // 返还信号
        semaphore.returnLease(lease);
    }
}
