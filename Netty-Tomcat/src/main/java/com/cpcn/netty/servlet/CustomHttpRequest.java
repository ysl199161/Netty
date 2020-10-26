package com.cpcn.netty.servlet;

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
public interface CustomHttpRequest {
    String getUri();

    String getMethod();

    Map<String, List<String>> getParameters();

    List<String> getParameters(String name);

    String getParameter(String name);

    String getPath();
}