package com.haifuns.zkdemo.lock;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.curator.framework.recipes.locks.InterProcessReadWriteLock;
import org.apache.curator.retry.ExponentialBackoffRetry;

import static com.haifuns.zkdemo.ZKConfigConstant.HOST;

/**
 * 可重入读写锁
 *
 * @author haifuns
 * @date 2022/1/5 22:07
 */
public class InterProcessReadWriteLockDemo {

    public static void main(String[] args) throws Exception {

        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
        CuratorFramework client = CuratorFrameworkFactory.newClient(HOST, retryPolicy);
        client.start();

        InterProcessReadWriteLock interProcessReadWriteLock = new InterProcessReadWriteLock(client, "");
        InterProcessMutex readLock = interProcessReadWriteLock.readLock();
        readLock.acquire();
        readLock.release();

        InterProcessMutex writeLock = interProcessReadWriteLock.writeLock();
        writeLock.acquire();
        writeLock.release();
    }
}
