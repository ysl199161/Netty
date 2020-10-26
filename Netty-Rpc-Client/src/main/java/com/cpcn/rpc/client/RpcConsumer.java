package com.cpcn.rpc.client;

import com.cpcn.rpc.api.SomeServer;

/**
 * =======================
 * Author    Time
 * ysl       {date}
 * =======================
 **/
public class RpcConsumer {
    public static void main(String[] args) {

        SomeServer someServer = RpcProxy.create(SomeServer.class);
        System.out.println(someServer.sayHello("杨胜利"));
        System.out.println(someServer.hashCode());
    }
}
