package com.cpcn.dubbo.client.balance;

import java.util.List;
import java.util.Random;

/**
 * =======================
 * Author    Time
 * ysl       {date}
 * =======================
 **/
public class RandomLoadBalance implements LoadBalance {
    @Override
    public String balanceChoose(List<String> invokers) {
        return invokers.get(new Random().nextInt(invokers.size()));
    }
}
