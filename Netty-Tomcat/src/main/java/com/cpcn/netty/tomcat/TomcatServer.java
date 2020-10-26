package com.cpcn.netty.tomcat;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpServerCodec;

import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/*
 * </pre>
 * Modify Information:
 * Author        Date          Description
 * ============ =========== ============================
 * ysl          xxx          Create this file
 *
 */
public class TomcatServer {

    private String basePackage;

    private Map<String, String> classNameMap = new HashMap<>();
    private Map<String, Object> instanceMap = new HashMap<>();

    public TomcatServer(String basePackage) {
        this.basePackage = basePackage;
    }

    public void doRegist(String basePackage) throws Exception {
        URL resource = this.getClass().getClassLoader().getResource(basePackage.replaceAll("\\.", "/"));
        if (null == resource) {
            return;
        }
        File dir = new File(resource.getFile());
        for (File file : dir.listFiles()) {
            if (file.isDirectory()) {
                doRegist(basePackage + "." + file.getName());
            } else if (file.getName().endsWith(".class")) {
                String fileName = file.getName().replace(".class", "").trim();
                classNameMap.put(fileName.toLowerCase(), basePackage + "." + fileName);
                instanceMap.put(fileName.toLowerCase(), Class.forName(basePackage + "." + fileName).newInstance());
            }
        }
    }

    public void runServer() throws Exception {
        EventLoopGroup parentGroup = new NioEventLoopGroup();
        EventLoopGroup childGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(parentGroup, childGroup)
                    .option(ChannelOption.SO_BACKLOG, 1024)
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();
                            pipeline.addLast(new HttpServerCodec());
                            pipeline.addLast(new TomcatServerHandler(instanceMap));
                        }
                    });

            ChannelFuture future = bootstrap.bind(8888).sync();
            System.out.println("Tomcat启动成功，端口号8888");
            future.channel().closeFuture().sync();
        } finally {
            parentGroup.shutdownGracefully();
            childGroup.shutdownGracefully();
        }
    }

    public void start() throws Exception {
        doRegist(basePackage);
        runServer();
    }


}
