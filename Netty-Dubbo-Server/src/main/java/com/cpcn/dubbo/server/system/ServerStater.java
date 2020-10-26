package com.cpcn.dubbo.server.system;

import com.cpcn.dubbo.server.register.RegisterCenter;
import com.cpcn.dubbo.server.register.ZkRegisterCenter;

/**
 * =======================
 * Author    Time
 * ysl       {date}
 * =======================
 **/
public class ServerStater {
    public static void main(String[] args) throws Exception {
        RpcServer server = new RpcServer();
        RegisterCenter registerCenter = new ZkRegisterCenter();
        server.publish("com.cpcn.dubbo.server.impl", registerCenter, "127.0.0.1:8888");
        server.start();
    }
}
