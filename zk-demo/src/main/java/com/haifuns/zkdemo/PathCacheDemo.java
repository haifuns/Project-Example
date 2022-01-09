package com.haifuns.zkdemo;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;

import static com.haifuns.zkdemo.ZKConfigConstant.HOST;

/**
 * 节点缓存
 *
 * @author haifuns
 * @date 2022/1/9 22:39
 */
public class PathCacheDemo {

    public static void main(String[] args) throws Exception {
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
        CuratorFramework client = CuratorFrameworkFactory.newClient(HOST,
                5000,
                3000,
                retryPolicy);
        client.start();

        // 把zk中的数据缓存到客户端
        // 可以针对缓存中的数据添加监听器观察zk中的数据变化
        PathChildrenCache pathChildrenCache = new PathChildrenCache(client, "/cache", true);
        pathChildrenCache.getListenable().addListener(new PathChildrenCacheListener() {
            @Override
            public void childEvent(CuratorFramework client, PathChildrenCacheEvent event) throws Exception {
                System.out.println(event);
            }
        });

        pathChildrenCache.start();

        Thread.sleep(Integer.MAX_VALUE);
    }
}
