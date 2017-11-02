package com.rpc;

import com.rpc.core.RpcCore;

public class Client
{
    public static void main(String[] args)
    {
        RpcCore bo = new RpcCore();

        Inttest test = bo.callService(Inttest.class);

        System.out.println("client return is:" + test.tests("heheh", 290300));
    }
}
