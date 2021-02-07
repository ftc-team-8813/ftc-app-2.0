package org.firstinspires.ftc.teamcode.util;

import java.util.HashMap;

public class Persistent
{
    private static HashMap<String, Object> data = new HashMap<>();
    
    public static void put(String name, Object obj)
    {
        data.put(name, obj);
    }
    
    public static Object get(String name)
    {
        return data.get(name);
    }
    
    public static void clear()
    {
        data.clear();
    }
}
