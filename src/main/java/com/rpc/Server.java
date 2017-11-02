package com.rpc;

import com.rpc.core.RpcCore;

public class Server
{
    public static void main(String[] args)
    {
        RpcCore bo = new RpcCore();

        bo.bindService();

    }

}
