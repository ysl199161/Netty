package com.cpcn.rpc.client;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * =======================
 * Author    Time
 * ysl       {date}
 * =======================
 **/
public class RpcClientHandler extends SimpleChannelInboundHandler<Object> {

    private Object result;

    public Object getResult() {
        return this.result;
    }

    protected void channelRead0(ChannelHandlerContext ctx, Object obj) throws Exception {
        //obj为远程调用的计算结果
        this.result = obj;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
