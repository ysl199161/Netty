package com.cpcn.dubbo.client.discovery;

/**
 * 服务发现的规范
 * <pre>
 * Modify Information:
 * Author       Date           Description
 * ============ ============= ============================
 * yangshengli      2020/10/20        TODO
 * </pre>
 */
public interface ServiceDiscovery {
    String discovery(String serviceName) throws Exception;
}
