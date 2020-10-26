package com.cpcn.netty.tomcat;

/*
 * </pre>
 * Modify Information:
 * Author        Date          Description
 * ============ =========== ============================
 * ysl          xxx          Create this file
 *
 */
public class TomcatStarter {
    public static void main(String[] args) throws Exception {
        TomcatServer tomcatServer = new TomcatServer("com.cpcn.netty.webapp");
        tomcatServer.start();
    }
}
