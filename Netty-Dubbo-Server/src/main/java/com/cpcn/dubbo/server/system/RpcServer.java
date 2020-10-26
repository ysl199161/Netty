package com.cpcn.dubbo.server.system;

import com.cpcn.dubbo.server.handler.RpcServerHandler;
import com.cpcn.dubbo.server.register.RegisterCenter;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * =======================
 * Author    Time
 * ysl       {date}
 * =======================
 **/
public class RpcServer {
    //服务注册表，key：服务接口名，value：真正去执行的类
    private Map<String, Object> registMap = new HashMap<String, Object>();
    //存储指定包中所有的提供者类名
    private List<String> classCache = new ArrayList<String>();

    private String providerPackage;
    private String serviceAddress;


    //将指定包中的所有提供者进行发布（写入到服务注册表）
    public void publish(String providerPackage, RegisterCenter center, String serviceAddress) throws Exception {
        // 将指定包中的所有.class文件的类名写入到classCache中
        getProviderClass(providerPackage);
        // 真正注册
        doRgister(center, serviceAddress);
        this.providerPackage = providerPackage;
        this.serviceAddress = serviceAddress;
    }


    private void getProviderClass(String providerPackage) {
        // 获取指定包中的资源
        String resourcePath = providerPackage.replaceAll("\\.", "/");
        URL resource = this.getClass().getClassLoader()
                // com.abc.rpc.service  -> com/abc/rpc/service
                .getResource(resourcePath);
        // 若目录中没有任何资源，则直接结束
        if (resource == null) return;
        // 将URL资源转化为file
        File dir = new File(resource.getFile());
        // 遍历指定包及其子孙包中所有的文件，查找.class文件
        for (File file : dir.listFiles()) {
            // 若当前遍历文件为目录，则递归
            if (file.isDirectory()) {
                getProviderClass(providerPackage + "." + file.getName());
            } else if (file.getName().endsWith(".class")) {
                // 获取简单类名
                String fileName = file.getName().replace(".class", "").trim();
                // 将.class的全限定类名添加到缓存集合
                classCache.add(providerPackage + "." + fileName);
            }
        }
    }


    private void doRgister(RegisterCenter center, String serviceAddress) throws Exception {
        // 1. 写入到registerMap
        // 2. 写入到zk

        // 若没有提供者类，则直接结束
        if (classCache.size() == 0) return;

        // 将所有提供者写入到注册表
        for (String className : classCache) {
            Class<?> clazz = Class.forName(className);
            String interfaceName = clazz.getInterfaces()[0].getName();
            // 注意：这里的key由原来的接口变为了实现类名
            registMap.put(className, clazz.newInstance());
            center.register(interfaceName, serviceAddress);
        }
    }

    //启动服务端
    public void start() throws Exception {
        EventLoopGroup parentGroup = new NioEventLoopGroup();
        EventLoopGroup childGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(parentGroup, childGroup)
                    //当服务端用于处理客户端连接请求时，线程不够时，缓存连接请求
                    .option(ChannelOption.SO_BACKLOG, 1024)
                    //使用心跳机制来维护长连接
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    //用于指定channel的类型
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();
                            pipeline.addLast(new ObjectEncoder());
                            pipeline.addLast(new ObjectDecoder(Integer.MAX_VALUE,
                                    ClassResolvers.cacheDisabled(null)));
                            // 添加自定义处理器
                            pipeline.addLast(new RpcServerHandler(registMap, providerPackage));
                        }
                    });
            //服务端的ip及端口
            String ip = serviceAddress.split(":")[0];
            String port = serviceAddress.split(":")[1];

            ChannelFuture future = bootstrap.bind(ip, Integer.valueOf(port)).sync();
            System.out.println("server 已启动");
            future.channel().closeFuture().sync();
        } finally {
            childGroup.shutdownGracefully();
            parentGroup.shutdownGracefully();
        }
    }
}
