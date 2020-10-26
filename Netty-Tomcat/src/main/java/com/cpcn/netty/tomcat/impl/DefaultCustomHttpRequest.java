package com.cpcn.netty.tomcat.impl;

import com.cpcn.netty.servlet.CustomHttpRequest;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.QueryStringDecoder;

import java.util.List;
import java.util.Map;

/*
 * </pre>
 * Modify Information:
 * Author        Date          Description
 * ============ =========== ============================
 * ysl          xxx          Create this file
 *
 */
public class DefaultCustomHttpRequest implements CustomHttpRequest {

    private HttpRequest httpRequest;

    public DefaultCustomHttpRequest(HttpRequest httpRequest) {
        this.httpRequest = httpRequest;
    }

    @Override
    public String getUri() {
        return httpRequest.uri();
    }

    @Override
    public String getMethod() {
        return httpRequest.method().name();
    }

    @Override
    public Map<String, List<String>> getParameters() {
        QueryStringDecoder decoder = new QueryStringDecoder(httpRequest.uri());
        return decoder.parameters();
    }

    @Override
    public List<String> getParameters(String name) {
        return getParameters().get(name);
    }

    @Override
    public String getParameter(String name) {
        List<String> list = getParameters(name);
        if (null == list || list.isEmpty()) {
            return null;
        }
        return list.get(0);
    }

    @Override
    public String getPath() {
        QueryStringDecoder decoder = new QueryStringDecoder(httpRequest.uri());
        return decoder.path();
    }
}
