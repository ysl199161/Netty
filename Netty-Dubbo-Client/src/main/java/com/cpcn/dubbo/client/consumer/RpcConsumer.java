package com.cpcn.dubbo.client.consumer;

import com.cpcn.dubbo.api.rpc.SomeService;
import com.cpcn.dubbo.client.proxy.RpcProxy;

/**
 * =======================
 * Author    Time
 * ysl       {date}
 * =======================
 **/
public class RpcConsumer {
    public static void main(String[] args) {
        SomeService someService = RpcProxy.create(SomeService.class, "WeChat");
        if (null != someService) {
            System.out.println(someService.hello("Cugslyang"));
            System.out.println(someService.hashCode());
        }
    }
}
