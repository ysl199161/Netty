package com.cpcn.dubbo.server.handler;

import com.cpcn.dubbo.api.rpc.Invocation;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.concurrent.EventExecutorGroup;

import java.lang.reflect.InvocationHandler;
import java.util.Map;

/**
 * =======================
 * Author    Time
 * ysl       {date}
 * =======================
 **/
public class RpcServerHandler extends SimpleChannelInboundHandler<Invocation> {

    private Map<String, Object> registMap;
    private String providerPackage;

    public RpcServerHandler(Map<String, Object> registMap, String providerPackage) {
        this.providerPackage = providerPackage;
        this.registMap = registMap;
    }

    protected void channelRead0(ChannelHandlerContext ctx, Invocation msg) throws Exception {
        Object result = "没有要访问的提供者";
        //接口全名称
        String className = msg.getClassName();
        //简单接口名
        String simpleClassName = className.substring(className.lastIndexOf(".") + 1);
        //实现类全名
        String key = providerPackage + "." + msg.getPrefix() + simpleClassName;
        // 判断注册表中是否存在指定服务(提供者类名-->提供者实例)
        if (registMap.containsKey(key)) {
            // 获取到相应的提供者实例，然后调用其相应方法
            Object provider = registMap.get(key);
            result = provider.getClass().getMethod(msg.getMethodName(), msg.getParamTypes())
                    .invoke(provider, msg.getParamValues());
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
