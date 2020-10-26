package com.cpcn.dubbo.client.balance;

import java.util.List;

/**
 * 负载均衡的标准
 * <pre>
 * Modify Information:
 * Author       Date           Description
 * ============ ============= ============================
 * yangshengli      2020/10/20        TODO
 * </pre>
 */
public interface LoadBalance {
    String balanceChoose(List<String> invokers);
}
