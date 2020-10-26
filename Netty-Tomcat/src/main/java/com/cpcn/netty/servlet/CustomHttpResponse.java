package com.cpcn.netty.servlet;

/*
 * </pre>
 * Modify Information:
 * Author        Date          Description
 * ============ =========== ============================
 * ysl          xxx          Create this file
 *
 */
public interface CustomHttpResponse {
    void write(String content) throws Exception;
}
