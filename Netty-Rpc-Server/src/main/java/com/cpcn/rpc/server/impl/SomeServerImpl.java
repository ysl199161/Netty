package com.cpcn.rpc.server.impl;

import com.cpcn.rpc.api.SomeServer;

/**
 * =======================
 * Author    Time
 * ysl       {date}
 * =======================
 **/
public class SomeServerImpl implements SomeServer {
    public String sayHello(String name) {
        return name + "，你好！";
    }
}
