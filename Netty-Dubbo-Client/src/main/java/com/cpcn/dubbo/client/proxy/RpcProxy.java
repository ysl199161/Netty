package com.cpcn.dubbo.client.proxy;

import com.cpcn.dubbo.api.rpc.Invocation;
import com.cpcn.dubbo.api.rpc.SomeService;
import com.cpcn.dubbo.client.discovery.ServiceDiscovery;
import com.cpcn.dubbo.client.discovery.ServiceDiscoveryImpl;
import com.cpcn.dubbo.client.handler.RpcClientHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * =======================
 * Author    Time
 * ysl       {date}
 * =======================
 **/
public class RpcProxy {


    public static <T> T create(Class<?> clazz, String prefix) {
        return (T) Proxy.newProxyInstance(clazz.getClassLoader(), new Class[]{clazz}, new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                // 若调用的是Object的方法，则直接进行本地调用
                if (Object.class.equals(method.getDeclaringClass())) {
                    return method.invoke(this, args);
                }
                // 远程调用
                return rpcInvoke(clazz, method, args, prefix);
            }


        });
    }

    private static Object rpcInvoke(Class<?> clazz, Method method, Object[] args, String prefix) throws Exception {
        //进行服务发现
        ServiceDiscovery serviceDiscovery = new ServiceDiscoveryImpl();
        String invoker = serviceDiscovery.discovery(clazz.getName());
        // 若zk中不存在该服务，则直接返回null
        if (invoker == null) {
            return null;
        }
        //进行远程调用
        RpcClientHandler handler = new RpcClientHandler();
        EventLoopGroup loopGroup = new NioEventLoopGroup();

        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(loopGroup)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel channel) throws Exception {
                            ChannelPipeline pipeline = channel.pipeline();
                            pipeline.addLast(new ObjectEncoder());
                            pipeline.addLast(new ObjectDecoder(Integer.MAX_VALUE,
                                    ClassResolvers.cacheDisabled(null)));
                            // 添加自定义处理器
                            pipeline.addLast(handler);
                        }
                    });
            //远程连接的ip与port
            String ip = invoker.split(":")[0];
            String port = invoker.split(":")[1];
            ChannelFuture future = bootstrap.connect(ip, Integer.valueOf(port)).sync();

            Invocation invocation = new Invocation();
            invocation.setClassName(clazz.getName());
            invocation.setMethodName(method.getName());
            invocation.setParamTypes(method.getParameterTypes());
            invocation.setParamValues(args);
            invocation.setPrefix(prefix);

            // 将调用参数发出
            future.channel().writeAndFlush(invocation).sync();

            future.channel().closeFuture().sync();

        } finally {
            loopGroup.shutdownGracefully();
        }
        return handler.getResult();
    }
}
