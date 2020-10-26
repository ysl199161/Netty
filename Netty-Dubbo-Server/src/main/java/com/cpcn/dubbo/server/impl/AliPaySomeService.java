package com.cpcn.dubbo.server.impl;

import com.cpcn.dubbo.api.rpc.SomeService;

/**
 * =======================
 * Author    Time
 * ysl       {date}
 * =======================
 **/
public class AliPaySomeService implements SomeService {
    public String hello(String name) {
        return "AliPay,Say:Hello," + name;
    }
}
