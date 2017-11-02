package com.rpc.core;

import java.io.IOException;
import java.lang.reflect.Proxy;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

import com.alibaba.fastjson.JSON;
import com.rpc.constant.RpcConstant;
import com.rpc.utils.RpcClassUtils;

public class RpcCore
{
    static ZooKeeper zk;

    static
    {
        try
        {
            zk = new ZooKeeper(RpcConstant.zkconn, 3000, null);
        }
        catch (IOException ex)
        {
            // TODO Auto-generated catch block
            ex.printStackTrace();
        }
    }

    public void bindService()
    {
        System.out.println("bindService start.");
        ServerSocket serskt = null;

        registerZk();
        System.out.println("registService end.");

        ExecutorService threadPool = Executors.newCachedThreadPool();

        try
        {
            serskt = new ServerSocket(RpcConstant.port);

            while (true)
            {
                final Socket sckt = serskt.accept();

                threadPool.execute(new CallHander(sckt));
            }
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
        }
        finally
        {
            try
            {
                if (null != serskt)
                {
                    serskt.close();
                }
            }
            catch (IOException ex)
            {
                ex.printStackTrace();
            }
        }
    }

    private void registerZk()
    {
        Map<String, Object> serviceMap = RpcClassUtils.getService_inst_rmi_map();
        if (!serviceMap.isEmpty())
        {
            Set<String> keys = serviceMap.keySet();
            Iterator<String> keyIter = keys.iterator();

            try
            {
                String localIp = InetAddress.getLocalHost().getHostAddress();
                String localAddress = localIp + ":" + RpcConstant.port;

                Stat dirExist = zk.exists(RpcConstant.rootpath, false);

                if (dirExist == null)
                {
                    zk.create(RpcConstant.rootpath, null, Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
                }

                while (keyIter.hasNext())
                {
                    String serviceName = keyIter.next();

                    Stat serDir = zk.exists(RpcConstant.rootpath + "/" + serviceName, false);

                    if (serDir == null)
                    {
                        zk.create(RpcConstant.rootpath + "/" + serviceName, null, Ids.OPEN_ACL_UNSAFE,
                                  CreateMode.PERSISTENT);
                    }

                    zk.create(RpcConstant.rootpath + "/" + serviceName + "/" + localIp, localAddress.getBytes(),
                              Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
                }

            }
            catch (IOException ex)
            {
                // TODO Auto-generated catch block
                ex.printStackTrace();
            }
            catch (KeeperException ex)
            {
                // TODO Auto-generated catch block
                ex.printStackTrace();
            }
            catch (InterruptedException ex)
            {
                // TODO Auto-generated catch block
                ex.printStackTrace();
            }

        }
    }

    @SuppressWarnings("unchecked")
    public <T> T callService(Class<T> service)
    {
        String serviceName = service.getName();
        System.out.println("callService:" + serviceName);

        try
        {
            List<String> providers = zk.getChildren(RpcConstant.rootpath + "/" + serviceName, false);
            System.out.println("providers is:" + JSON.toJSONString(providers));

            if (providers != null && providers.size() > 0)
            {
                String provide = providers.get((int) (System.currentTimeMillis() % providers.size()));
                byte[] conns = zk.getData(RpcConstant.rootpath + "/" + serviceName + "/" + provide, false, null);
                String[] provideInfo = new String(conns).split(":");

                return (T) Proxy.newProxyInstance(this.getClass().getClassLoader(), new Class<?>[]
                {service}, new ProxyHander(serviceName, provideInfo[0], Integer.valueOf(provideInfo[1])));
            }
            else
            {
                throw new RuntimeException("No service.");
            }
        }
        catch (KeeperException ex)
        {
            // TODO Auto-generated catch block
            ex.printStackTrace();
        }
        catch (InterruptedException ex)
        {
            // TODO Auto-generated catch block
            ex.printStackTrace();
        }

        throw new RuntimeException("No services.");
    }

}
