package com.cpcn.netty.webapp;

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
public class SomeServlet extends CustomServlet {
    @Override
    public void doGet(CustomHttpRequest request, CustomHttpResponse response) throws Exception {
        String param = request.getParameter("name");
        String uri = request.getUri();
        String method = request.getMethod();
        String path = request.getPath();

        String content = "method = " + method + "\n" +
                "uri = " + uri + "\n" +
                "path = " + path + "\n" +
                "param = " + param;
        response.write(content);
    }

    @Override
    public void doPost(CustomHttpRequest req, CustomHttpResponse res) throws Exception {
        doGet(req, res);
    }
}
