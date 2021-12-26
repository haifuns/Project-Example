package com.haifuns.zkdemo;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.leader.LeaderLatch;
import org.apache.curator.retry.ExponentialBackoffRetry;

import static com.haifuns.zkdemo.ZKConfigConstant.HOST;

/**
 * @author haifuns
 * @date 2021/11/30 22:51
 */
public class LeaderLatchDemo {

    public static void main(String[] args) throws Exception {
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
        CuratorFramework client = CuratorFrameworkFactory.newClient(HOST, retryPolicy);
        client.start();

        LeaderLatch leaderLatch = new LeaderLatch(client, "/leader/latch");
        leaderLatch.start();
        // 阻塞等待，直到成为leader
        leaderLatch.await();

        boolean hasLeadership = leaderLatch.hasLeadership();
        System.out.println("hasLeadership: " + hasLeadership);

        Thread.sleep(Integer.MAX_VALUE);
    }
}
