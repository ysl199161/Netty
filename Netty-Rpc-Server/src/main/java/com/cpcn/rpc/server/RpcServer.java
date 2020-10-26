package com.cpcn.rpc.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.ServerSocketChannel;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;

import java.io.File;
import java.net.URL;
import java.util.*;

/**
 * =======================
 * Author    Time
 * ysl       {date}
 * =======================
 **/
public class RpcServer {
    //服务注册表
    public static final Map<String, Object> registMap = new HashMap<String, Object>();
    //服务接口的实现类
    public static final List<String> cachClass = Collections.synchronizedList(new ArrayList<String>());

    public void publish(String basePackage) throws Exception {
        getCachClass(basePackage);
        doRegist();
    }

    private void getCachClass(String basePackage) {
        String sourcePath = basePackage.replace(".", "/");
        URL resource = this.getClass().getClassLoader().getResource(sourcePath);

        if (resource == null) return;

        //将url转化为files
        File dir = new File(resource.getFile());
        for (File file : dir.listFiles()) {
            if (file.isDirectory()) {
                getCachClass(basePackage + "." + file.getName());
            } else if (file.getName().endsWith(".class")) {
                cachClass.add(basePackage + "." + file.getName().replace(".class", ""));
            }
        }
    }

    private void doRegist() throws Exception {
        if (cachClass.size() == 0) {
            return;
        }
        for (String className : cachClass) {
            Class<?> clazz = Class.forName(className);
            registMap.put(clazz.getInterfaces()[0].getName(), clazz.newInstance());
            System.out.println("注册的服务名：" + clazz.getInterfaces()[0].getName() + "，对应的实例为：" + clazz.getName());
        }
    }

    public void start() throws Exception {
        EventLoopGroup parentGroup = new NioEventLoopGroup();
        EventLoopGroup childGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(parentGroup, childGroup)
                    //当服务端处理请求的线程全部用完时，临时存放已经完成了三次握手的请求
                    .option(ChannelOption.SO_BACKLOG, 1024)
                    //指定是否使用心跳机制来维护长连接
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();
                            pipeline.addLast(new ObjectEncoder());
                            pipeline.addLast(new ObjectDecoder(Integer.MAX_VALUE, ClassResolvers.cacheDisabled(null)));
                            pipeline.addLast(new RpcServerHandler(registMap));
                        }
                    });

            ChannelFuture future = bootstrap.bind(8888).sync();
            System.out.println("服务已启动");
            future.channel().closeFuture().sync();
        } finally {
            parentGroup.shutdownGracefully();
            childGroup.shutdownGracefully();
        }

    }


}
