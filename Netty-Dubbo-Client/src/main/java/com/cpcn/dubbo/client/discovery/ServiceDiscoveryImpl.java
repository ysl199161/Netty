package com.cpcn.dubbo.client.discovery;

import com.cpcn.dubbo.api.constant.ZkConstant;
import com.cpcn.dubbo.client.balance.RandomLoadBalance;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.retry.ExponentialBackoffRetry;

import java.util.List;

/**
 * =======================
 * Author    Time
 * ysl       {date}
 * =======================
 **/
public class ServiceDiscoveryImpl implements ServiceDiscovery {

    //zk的客户端
    private CuratorFramework curator;
    //发现的服务
    private List<String> invokers;

    public ServiceDiscoveryImpl() {
        curator = CuratorFrameworkFactory.builder()
                // 指定要连接的zk集群
                .connectString(ZkConstant.ZK_CLUSTER)
                // 指定连接超时时限
                .connectionTimeoutMs(10000)
                // 指定会话超时时限
                .sessionTimeoutMs(4000)
                // 指定重试机制：每重试一次，休眠1秒，最多重试10次
                .retryPolicy(new ExponentialBackoffRetry(1000, 10))
                .build();
        // 启动zk客户端
        curator.start();
    }

    public String discovery(String serviceName) throws Exception {
        String servicePath = ZkConstant.ZK_DUBBO_ROOT_PATH + "/" + serviceName;
        invokers = curator.getChildren().forPath(servicePath);

        if (invokers.size() == 0) {
            return null;
        }

        // 若只有一个提供者，则直接返回，不用负载均衡
        if (invokers.size() == 1) {
            return invokers.get(0);
        }

        // 为服务名称节点添加watcher监听
        registerWatcher(servicePath);

        //负载均衡去选择合适的服务
        return new RandomLoadBalance().balanceChoose(invokers);
    }

    private void registerWatcher(String servicePath) throws Exception {
        PathChildrenCache cache = new PathChildrenCache(curator, servicePath, true);
        // 为cache添加子节点列表变更的监听
        cache.getListenable().addListener((client, event) -> {
            invokers = curator.getChildren().forPath(servicePath);
        });

        cache.start();
    }
}
