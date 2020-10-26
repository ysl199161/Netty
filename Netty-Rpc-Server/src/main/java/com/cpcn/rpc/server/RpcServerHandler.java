package com.cpcn.rpc.server;

import com.cpcn.rpc.api.Invocation;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.HashMap;
import java.util.Map;

/**
 * =======================
 * Author    Time
 * ysl       {date}
 * =======================
 **/
public class RpcServerHandler extends SimpleChannelInboundHandler<Invocation> {
    //服务注册表
    private Map<String, Object> registMap;

    public RpcServerHandler(Map<String, Object> registMap) {
        this.registMap = registMap;
    }

    protected void channelRead0(ChannelHandlerContext ctx, Invocation msg) throws Exception {
        Object result = "没有要访问的提供者";
        //判断注册表中是否存在指定的服务
        if (registMap.containsKey(msg.getClassName())) {
            //获取到相应的实例，再调用其方法
            Object privider = registMap.get(msg.getClassName());
            result = privider.getClass().getMethod(msg.getMethodName(), msg.getParamTypes()).invoke(privider, msg.getParams());
        }
        ctx.writeAndFlush(result);
        ctx.close();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
