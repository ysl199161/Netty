package com.cpcn.dubbo.server.register;

/**
 * =======================
 * Author    Time
 * ysl       {date}
 * =======================
 **/
public class ZkRegisterTest {

    public static void main(String[] args) throws Exception{
        RegisterCenter registerCenter = new ZkRegisterCenter();
        registerCenter.register("com.cpcn.dubbo.api.service.SomeService", "60.205.182.64:2181");
        System.in.read();
    }
}
