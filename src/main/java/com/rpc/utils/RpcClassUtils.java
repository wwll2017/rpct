package com.rpc.utils;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import com.alibaba.fastjson.JSON;
import com.rpc.anno.Rmi;

public class RpcClassUtils
{
    //包下所的class文件
    private static ArrayList<File> class_pkg = new ArrayList<File>();
    private static Set<Class<?>> class_pkg_set = new HashSet<Class<?>>();
    private static Set<Class<?>> class_rmi_set = new HashSet<Class<?>>();
    private static Map<Class<?>, Object> class_inst_rmi_map = new HashMap<Class<?>, Object>();
    private static Map<String, Object> service_inst_rmi_map = new HashMap<String, Object>();

    //包路径
    static String pkgPath;
    static String proper_file = "rpc.properties";

    static
    {
        getClasses();
        loadPkgCls();
        instRmiClass();
    }

    /*
    * 获取包下所的class
    */
    private static void getClasses()
    {
        ClassLoader loader = Thread.currentThread().getContextClassLoader();

        Properties props = new Properties();

        try
        {
            props.load(loader.getResourceAsStream(proper_file));
            pkgPath = (String) props.get("scanbasepackage");

            URL urls = loader.getResource(pkgPath);

            // 
            // Enumeration<URL> urlsEnum = loader.getResources("com/nsyh/demo");
            // System.out.println("urlsEnum:" + JSON.toJSONString(urlsEnum));
            //
            URL url = urls;
            if (url != null)
            {
                System.out.println("url is:" + JSON.toJSONString(url));
                String potocol = url.getProtocol();
                String filePath = url.getPath();

                System.out.println("filePath is:" + filePath);

                if ("file".equals(potocol))
                {
                    String fileRealPath = filePath.replaceFirst("/", "").trim();
                    System.out.println("fileRealPath is:" + fileRealPath);
                    pkgPath = fileRealPath.replace(pkgPath, "").trim().replace("/", "\\");

                    getAllFiles(fileRealPath, class_pkg);
                }
            }
        }
        catch (IOException ex)
        {
            // TODO Auto-generated catch block
            ex.printStackTrace();
        }
    }

    /*
    * 获取指定路径下的class文件
    */
    private static void getAllFiles(String paths, List<File> fileLists)
    {
        File[] result = new File(paths).listFiles();
        List<File> fileList = null;

        fileList = new ArrayList<File>(Arrays.asList(result));

        Iterator<File> iters = fileList.iterator();

        while (iters.hasNext())
        {
            File fil = iters.next();
            if (fil.getName().endsWith(".class"))
            {
                fileLists.add(fil);
                continue;
            }

            if (fil.isDirectory())
            {
                getAllFiles(fil.getPath(), fileLists);
}

        }

    }

    /*
    * 载入包下所的类
    */
    private static void loadPkgCls()
    {
        Iterator<File> iter = class_pkg.iterator();
        while (iter.hasNext())
        {
            String clsPath = iter.next().getPath();
            String classes = clsPath.replace(pkgPath, "").replace('\\', '.');
            int idx = classes.lastIndexOf(".class");
            classes = classes.substring(0, idx);

            System.out.println(classes);

            try
            {
                Class<?> classs = Class.forName(classes);
                class_pkg_set.add(classs);
            }
            catch (ClassNotFoundException ex)
            {
                // TODO Auto-generated catch block
                ex.printStackTrace();
            }
        }
    }

    /*
    * 实例化所的rmi类
    */
    private static void instRmiClass()
    {
        for (Class<?> cls : class_pkg_set)
        {
            if (cls.isAnnotationPresent(Rmi.class))
            {
                String ser_name = cls.getAnnotation(Rmi.class).servicename();
                ser_name = (ser_name == null || ser_name.trim().length() == 0) ? cls.getInterfaces()[0].getClass()
                        .getName() : ser_name;
                try
                {
                    Object inst = cls.newInstance();

                    class_rmi_set.add(cls);
                    class_inst_rmi_map.put(cls, inst);
                    service_inst_rmi_map.put(ser_name, inst);
                }
                catch (InstantiationException ex)
                {
                    // TODO Auto-generated catch block
                    ex.printStackTrace();
                }
                catch (IllegalAccessException ex)
                {
                    // TODO Auto-generated catch block
                    ex.printStackTrace();
                }
            }
        }

        System.out.println(JSON.toJSONString(class_rmi_set));
        System.out.println(class_inst_rmi_map.toString());
        System.out.println(service_inst_rmi_map.toString());
    }

    public static Set<Class<?>> getClass_pkg_set()
    {
        return class_pkg_set;
    }

    public static Set<Class<?>> getClass_rmi_set()
    {
        return class_rmi_set;
    }

    public static Map<Class<?>, Object> getClass_inst_rmi_map()
    {
        return class_inst_rmi_map;
    }

    public static Map<String, Object> getService_inst_rmi_map()
    {
        return service_inst_rmi_map;
    }

}