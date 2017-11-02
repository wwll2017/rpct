package com.rpc.constant;

import com.rpc.utils.PropertiesUntils;

public class RpcConstant
{
    public static int port;
    public static String zkconn;
    public static String rootpath = "/rpcser";

    static
    {
        port = Integer.valueOf((String) PropertiesUntils.getProps().get("rmibindport"));
        zkconn = (String) PropertiesUntils.getProps().get("zookeeperconn");
    }

}
