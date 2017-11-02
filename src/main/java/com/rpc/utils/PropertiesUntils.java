package com.rpc.utils;

import java.io.IOException;
import java.util.Properties;

public class PropertiesUntils
{
    private static Properties props = new Properties();

    static
    {
        ClassLoader loader = Thread.currentThread().getContextClassLoader();

        try
        {
            props.load(loader.getResourceAsStream("rpc.properties"));
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
        }
    }

    public static Properties getProps()
    {
        return props;
    }

}
