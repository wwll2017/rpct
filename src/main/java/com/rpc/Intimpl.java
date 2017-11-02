package com.rpc;

import com.rpc.anno.Rmi;

@Rmi(servicename = Inttest.name)
public class Intimpl implements Inttest
{

    public String tests(String aa, int bb)
    {
        System.out.println(aa + "+sdfds+" + bb);
        return aa + bb;
    }

}
