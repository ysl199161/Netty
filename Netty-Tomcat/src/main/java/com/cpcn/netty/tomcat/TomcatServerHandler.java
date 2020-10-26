package com.cpcn.netty.tomcat;

import com.cpcn.netty.servlet.CustomHttpRequest;
import com.cpcn.netty.servlet.CustomHttpResponse;
import com.cpcn.netty.servlet.CustomServlet;
import com.cpcn.netty.tomcat.impl.DefaultCustomHttpRequest;
import com.cpcn.netty.tomcat.impl.DefaultCustomHttpResponse;
import com.cpcn.netty.tomcat.impl.DefaultCustomServlet;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.HttpRequest;

import java.util.Map;

/*
 * </pre>
 * Modify Information:
 * Author        Date          Description
 * ============ =========== ============================
 * ysl          xxx          Create this file
 *
 */
public class TomcatServerHandler extends ChannelInboundHandlerAdapter {

    private Map<String, Object> instanceMap;

    public TomcatServerHandler(Map<String, Object> instanceMap) {
        this.instanceMap = instanceMap;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof HttpRequest) {
            HttpRequest request = (HttpRequest) msg;
            String servletName = request.uri().split("/")[1];

            CustomServlet servlet;
            if (instanceMap.containsKey(servletName)) {
                servlet = (CustomServlet) instanceMap.get(servletName);
            } else {
                servlet = new DefaultCustomServlet();
            }
            CustomHttpRequest req = new DefaultCustomHttpRequest(request);
            CustomHttpResponse res = new DefaultCustomHttpResponse(request, ctx);
            if (req.getMethod().equalsIgnoreCase("GET")) {
                servlet.doGet(req, res);
            }
            if (req.getMethod().equalsIgnoreCase("POST")) {
                servlet.doPost(req, res);
            }
        }
        ctx.close();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
