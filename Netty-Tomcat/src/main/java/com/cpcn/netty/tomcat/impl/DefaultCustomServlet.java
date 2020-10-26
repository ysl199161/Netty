package com.cpcn.netty.tomcat.impl;

import com.cpcn.netty.servlet.CustomHttpRequest;
import com.cpcn.netty.servlet.CustomHttpResponse;
import com.cpcn.netty.servlet.CustomServlet;

/*
 * </pre>
 * Modify Information:
 * Author        Date          Description
 * ============ =========== ============================
 * ysl          xxx          Create this file
 *
 */
public class DefaultCustomServlet extends CustomServlet {
    @Override
    public void doGet(CustomHttpRequest req, CustomHttpResponse res) throws Exception {
        String servletName = req.getUri().split("/")[1];
        String content = "404 - no this servlet : " + servletName;
        res.write(content);
    }

    @Override
    public void doPost(CustomHttpRequest req, CustomHttpResponse res) throws Exception {
        doGet(req, res);
    }
}
