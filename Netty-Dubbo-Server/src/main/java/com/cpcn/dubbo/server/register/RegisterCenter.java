package com.cpcn.dubbo.server.register;

/**
 * <pre>
 *     定义注册规范，也就是注册的接口
 * Modify Information:
 * Author       Date           Description
 * ============ ============= ============================
 * yangshengli      2020/10/18        TODO
 * </pre>
 */
public interface RegisterCenter {
    /**
     * @param serviceName  服务名，即接业务口名称
     * @param serviceAddress  服务地址，也就是服务提供者的地址
     */
    void register(String serviceName,String serviceAddress) throws Exception;
}
