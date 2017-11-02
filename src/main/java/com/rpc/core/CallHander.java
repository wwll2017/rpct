package com.rpc.core;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.Socket;

import com.rpc.utils.RpcClassUtils;

class CallHander implements Runnable
{
    Socket skt;

    CallHander(Socket skt)
    {
        this.skt = skt;
    }

    public void run()
    {
        try
        {
            InputStream ins = skt.getInputStream();
            ObjectInputStream ois = new ObjectInputStream(ins);

            //读取服务调用信息
            String serviceName = ois.readUTF();
            String methodName = (String) ois.readObject();
            Class<?>[] paramTypes = (Class<?>[]) ois.readObject();
            Object[] args = (Object[]) ois.readObject();

            //调用本地方法
            Object serviceInst = RpcClassUtils.getService_inst_rmi_map().get(serviceName);
            Method method = serviceInst.getClass().getMethod(methodName, paramTypes);
            Object returns = method.invoke(serviceInst, args);

            //返回调用结果
            OutputStream ous = skt.getOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(ous);

            oos.writeObject(returns);

            oos.close();
            ous.close();
            ois.close();
            ins.close();

            skt.close();

            System.out.println("service call end." + serviceName);
        }
        catch (IOException ex)
        {
            // TODO Auto-generated catch block
            ex.printStackTrace();
        }
        catch (ClassNotFoundException ex)
        {
            // TODO Auto-generated catch block
            ex.printStackTrace();
        }

        catch (IllegalAccessException ex)
        {
            // TODO Auto-generated catch block
            ex.printStackTrace();
        }
        catch (IllegalArgumentException ex)
        {
            // TODO Auto-generated catch block
            ex.printStackTrace();
        }
        catch (InvocationTargetException ex)
        {
            // TODO Auto-generated catch block
            ex.printStackTrace();
        }
        catch (NoSuchMethodException ex)
        {
            // TODO Auto-generated catch block
            ex.printStackTrace();
        }
        catch (SecurityException ex)
{
            // TODO Auto-generated catch block
            ex.printStackTrace();
        }

}
}
