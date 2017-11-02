package com.rpc.core;

import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.net.Socket;

class ProxyHander implements InvocationHandler
{
    String ip;
    int port;
    String serviceName;

    public ProxyHander(String serviceName, String ip, int port)
    {
        this.serviceName = serviceName;
        this.ip = ip;
        this.port = port;
    }

    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable
{
        System.out.println("ProxyHander satart.");
        Socket soc = new Socket(ip, port);

        OutputStream ous = soc.getOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(ous);

        oos.writeUTF(serviceName);
        oos.writeObject(method.getName());
        oos.writeObject(method.getParameterTypes());
        oos.writeObject(args);

        InputStream ins = soc.getInputStream();
        ObjectInputStream ois = new ObjectInputStream(ins);

        Object resp = ois.readObject();

        ois.close();
        ins.close();
        oos.close();
        ous.close();
        soc.close();

        return resp;
    }
}
