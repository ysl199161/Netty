package com.cpcn.rpc.client;

import com.cpcn.rpc.api.Invocation;
import com.cpcn.rpc.api.SomeServer;
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
    public static <T> T create(final Class<?> clazz) {
        return (T) Proxy.newProxyInstance(clazz.getClassLoader(), new Class[]{clazz}, new InvocationHandler() {
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                //如果调用的是object的方法，则直接进行本地调用
                if (Object.class.equals(method.getDeclaringClass())) {
                    return method.invoke(this, args);
                }
                //远程调用
                return rpcInvoke(clazz, method, args);
            }


        });
    }

    private static Object rpcInvoke(Class<?> clazz, Method method, Object[] args) throws Exception {

        final RpcClientHandler rpcClientHandler = new RpcClientHandler();
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();
                            pipeline.addLast(new ObjectEncoder());
                            pipeline.addLast(new ObjectDecoder(Integer.MAX_VALUE, ClassResolvers.cacheDisabled(null)));
                            pipeline.addLast(rpcClientHandler);
                        }
                    });
            ChannelFuture future = bootstrap.connect("127.0.0.1", 8888).sync();

            Invocation invocation = new Invocation();
            invocation.setClassName(clazz.getName());
            invocation.setMethodName(method.getName());
            invocation.setParamTypes(method.getParameterTypes());
            invocation.setParams(args);

            //发送到服务端
            future.channel().writeAndFlush(invocation).sync();


            future.channel().closeFuture().sync();
        } finally {
            group.shutdownGracefully();
        }
        return rpcClientHandler.getResult();
    }
}
