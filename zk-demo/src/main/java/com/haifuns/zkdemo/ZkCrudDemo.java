package com.haifuns.zkdemo;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static com.haifuns.zkdemo.ZKConfigConstant.HOST;

/**
 * @author haifuns
 * @date 2021/11/30 22:17
 */
public class ZkCrudDemo {

    public static void main(String[] args) throws Exception {
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
        CuratorFramework client = CuratorFrameworkFactory.newClient(HOST, retryPolicy);
        client.start();

        client.create().creatingParentsIfNeeded().forPath("/test/path", "100".getBytes(StandardCharsets.UTF_8));

        byte[] bytes = client.getData().forPath("/test/path");
        System.out.println(new String(bytes));

        List<String> children = client.getChildren().forPath("/test");
        System.out.println(children);

        client.delete().forPath("/test/path");

        client.close();
    }
}
