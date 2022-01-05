package com.haifuns.zkdemo.lock;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.locks.InterProcessLock;
import org.apache.curator.framework.recipes.locks.InterProcessMultiLock;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.curator.retry.ExponentialBackoffRetry;

import java.util.ArrayList;
import java.util.List;

import static com.haifuns.zkdemo.ZKConfigConstant.HOST;

/**
 * 联锁
 *
 * @author haifuns
 * @date 2022/1/5 23:23
 */
public class InterProcessMultiLockDemo {

    public static void main(String[] args) throws Exception {
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
        CuratorFramework client = CuratorFrameworkFactory.newClient(HOST, retryPolicy);
        client.start();

        InterProcessLock lock1 = new InterProcessMutex(client, "/locks/lock_01");
        InterProcessLock lock2 = new InterProcessMutex(client, "/locks/lock_02");
        InterProcessLock lock3 = new InterProcessMutex(client, "/locks/lock_03");

        List<InterProcessLock> locks = new ArrayList<>();
        locks.add(lock1);
        locks.add(lock2);
        locks.add(lock3);

        InterProcessMultiLock multiLock = new InterProcessMultiLock(locks);
        multiLock.acquire();
        multiLock.release();
    }
}
