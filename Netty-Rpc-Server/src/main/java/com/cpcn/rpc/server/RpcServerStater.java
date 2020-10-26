package com.cpcn.rpc.server;

/**
 * =======================
 * Author    Time
 * ysl       {date}
 * =======================
 **/
public class RpcServerStater {
    public static void main(String[] args) throws Exception {
        RpcServer rpcServer = new RpcServer();
        rpcServer.publish("com.cpcn.rpc.server.impl");
        rpcServer.start();
    }
}
