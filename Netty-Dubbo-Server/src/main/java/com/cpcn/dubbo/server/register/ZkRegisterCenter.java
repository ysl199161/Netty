package com.cpcn.dubbo.server.register;

import com.cpcn.dubbo.api.constant.ZkConstant;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;

/**
 * =======================
 * Author    Time
 * ysl       {date}
 * =======================
 **/
public class ZkRegisterCenter implements RegisterCenter {

    private CuratorFramework curator;

    public ZkRegisterCenter() {
        initCurator();
    }

    private void initCurator() {
        curator = CuratorFrameworkFactory.builder()
                //指定连接的zk集群地址
                .connectString(ZkConstant.ZK_CLUSTER)
                //连接超时
                .connectionTimeoutMs(10000)
                //会话超时
                .sessionTimeoutMs(4000)
                // 指定重试机制：每重试一次，休眠1秒，最多重试10次
                .retryPolicy(new ExponentialBackoffRetry(1000, 10))
                .build();
        //启动zk客户端
        curator.start();
    }

    public void register(String serviceName, String serviceAddress) throws Exception {
        //注册的服务的节点名称(持久节点)
        String servicePath = ZkConstant.ZK_DUBBO_ROOT_PATH + "/" + serviceName;
        //节点不存在就要创建
        if (curator.checkExists().forPath(servicePath) == null) {
            curator.create()
                    //如果父节点不存在则创建
                    .creatingParentsIfNeeded()
                    //指定节点类型（持久的）
                    .withMode(CreateMode.PERSISTENT)
                    //最后指定节点名称
                    .forPath(servicePath);
        }

        //为注册的服务地址创建节点（临时节点）
        String providerPath = servicePath + "/" + serviceAddress;
        if (curator.checkExists().forPath(providerPath) == null) {
            String path = curator.create()
                    .withMode(CreateMode.EPHEMERAL)
                    .forPath(providerPath);
        }
    }
}
