package org.firstinspires.ftc.teamcode.util;

import java.util.HashMap;

public class Persistent
{
    private static HashMap<String, Object> data = new HashMap<>();

    public static void put(String name, Object obj)
    {
        Logger log = new Logger("Persistent");
        log.v("PUT '%s' => '%s'", name, obj);
        data.put(name, obj);
    }

    public static Object get(String name)
    {
        Logger log = new Logger("Persistent");
        Object value = data.get(name);
        log.v("GET '%s' => '%s'", name, value);
        return value;
    }

    public static void logEntries()
    {
        Logger log = new Logger("Persistent");
        log.d("Entries:");
        for (String key : data.keySet())
        {
            log.d("%s: %s", key, data.get(key));
        }
    }

    public static void clear()
    {
        Logger log = new Logger("Persistent");
        log.v("CLEAR");
        data.clear();
    }
}
