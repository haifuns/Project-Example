package com.haifuns.zkdemo.lock;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.curator.retry.ExponentialBackoffRetry;

import static com.haifuns.zkdemo.ZKConfigConstant.HOST;

/**
 * 可重入锁
 * @author haifuns
 * @date 2021/12/29 22:16
 */
public class ReentrantLockDemo {

    public static void main(String[] args) throws Exception {

        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
        CuratorFramework client = CuratorFrameworkFactory.newClient(HOST, retryPolicy);
        client.start();

        InterProcessMutex interProcessMutex = new InterProcessMutex(client, "/locks/lock_01");
        // 获取锁
        interProcessMutex.acquire();
        // 释放锁
        interProcessMutex.release();
    }
}
