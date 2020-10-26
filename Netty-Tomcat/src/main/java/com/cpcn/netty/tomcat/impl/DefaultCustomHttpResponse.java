package com.cpcn.netty.tomcat.impl;

import com.cpcn.netty.servlet.CustomHttpResponse;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpUtil;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.util.internal.StringUtil;

/*
 * </pre>
 * Modify Information:
 * Author        Date          Description
 * ============ =========== ============================
 * ysl          xxx          Create this file
 *
 */
public class DefaultCustomHttpResponse implements CustomHttpResponse {

    private HttpRequest httpRequest;
    private ChannelHandlerContext context;

    public DefaultCustomHttpResponse(HttpRequest httpRequest, ChannelHandlerContext context) {
        this.httpRequest = httpRequest;
        this.context = context;
    }

    @Override
    public void write(String content) throws Exception {
        if (StringUtil.isNullOrEmpty(content)) {
            return;
        }

        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK,
                Unpooled.wrappedBuffer(content.getBytes("UTF-8")));
        HttpHeaders headers = response.headers();
        headers.set(HttpHeaderNames.CONTENT_TYPE, "text/json");
        headers.set(HttpHeaderNames.CONTENT_LENGTH, response.content().readableBytes());
        headers.set(HttpHeaderNames.EXPIRES, 0);
        if (HttpUtil.isKeepAlive(httpRequest)) {
            headers.set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
        }
        context.writeAndFlush(response);
    }
}
