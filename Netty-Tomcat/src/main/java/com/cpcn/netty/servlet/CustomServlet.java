package com.cpcn.netty.servlet;

/*
 * </pre>
 * Modify Information:
 * Author        Date          Description
 * ============ =========== ============================
 * ysl          xxx          Create this file
 *
 */
public abstract class CustomServlet {

    public abstract void doGet(CustomHttpRequest req, CustomHttpResponse res) throws Exception;

    public abstract void doPost(CustomHttpRequest req, CustomHttpResponse res) throws Exception;


}
