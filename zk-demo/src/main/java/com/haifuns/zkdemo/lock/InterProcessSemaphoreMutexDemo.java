/*
 * Copyright (c) 2022 maoyan.com
 * All rights reserved.
 *
 */
package com.haifuns.zkdemo.lock;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.locks.InterProcessSemaphoreMutex;
import org.apache.curator.retry.ExponentialBackoffRetry;

import static com.haifuns.zkdemo.ZKConfigConstant.HOST;

/**
 * 不可重入锁
 *
 * @author haifun
 * @date  2022/1/5 8:17 下午
 */
public class InterProcessSemaphoreMutexDemo {

    public static void main(String[] args) throws Exception {

        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
        CuratorFramework client = CuratorFrameworkFactory.newClient(HOST, retryPolicy);
        client.start();

        InterProcessSemaphoreMutex interProcessSemaphoreMutex = new InterProcessSemaphoreMutex(client, "/locks/lock_02");
        interProcessSemaphoreMutex.acquire();
        interProcessSemaphoreMutex.release();
    }
}
