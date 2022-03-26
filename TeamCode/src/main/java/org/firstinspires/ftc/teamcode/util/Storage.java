package org.firstinspires.ftc.teamcode.util;

import android.content.Intent;
import android.net.Uri;
import android.os.Environment;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

import org.firstinspires.ftc.robotcore.internal.system.AppUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

public class Storage
{
    public static final String STORAGE_ROOT = new File(Environment.getExternalStorageDirectory(), "Team8813").getPath();
    
    private static void scanFile(String path)
    {
        Uri uri = Uri.fromFile(new File(path));
        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri);
        AppUtil.getInstance().getActivity().sendBroadcast(intent);
    }
    
    public static void initialize()
    {
        File rootFile = new File(STORAGE_ROOT);
        if (!rootFile.exists())
        {
            if (!rootFile.mkdir())
                throw new IllegalStateException("Unable to create root directory. Maybe check the SD card?");
        }
        else if (!rootFile.isDirectory())
        {
            File newFile = new File(rootFile.getParentFile(), "_" + rootFile.getName());
            if (!rootFile.renameTo(newFile))
                throw new IllegalStateException("Failed to move existing file in order to create root directory.");
            if (!rootFile.mkdir())
                throw new IllegalStateException("Unable to create root directory. Maybe check the SD card?");
            scanFile(newFile.getPath());
        }
        
        scanDir(rootFile, "logs");
    }
    
    private static void scanDir(File dir, String... ignore)
    {
        Logger log = new Logger("Tree Scanner");
        String[] names = dir.list();
        if (names == null) return;
        
        log.v("Scan %s", dir.getPath());
        for (String name : names)
        {
            File sub = new File(dir, name);
            if (sub.isDirectory())
            {
                boolean noScan = false;
                for (String ex : ignore)
                {
                    if (name.equals(ex))
                    {
                        noScan = true;
                        break;
                    }
                }
                if (!noScan) scanDir(sub, ignore);
                
            }
            else
            {
                log.v(" -> Scan file %s", sub.getPath());
                scanFile(sub.getPath());
            }
        }
    }
    
    public static File createFile(String path)
    {
        File f = new File(STORAGE_ROOT, path);
        try { f.createNewFile(); }
        catch (IOException e) { e.printStackTrace(); }
        scanFile(f.getPath());
        return f;
    }

    /**
     * Only works with non-nested json
     */
    public static double getJsonValue(String key){
        File config = Storage.getFile("config.json");
        try {
            Gson gson = new Gson();
            JsonReader reader = new JsonReader(new FileReader(config));
            HashMap<String, Double> map = gson.fromJson(reader, HashMap.class);
            return map.get(key);
        } catch (FileNotFoundException e) {e.printStackTrace();}
        return -1;
    }
    
    public static File getFile(String path)
    {
        return new File(STORAGE_ROOT, path);
    }
    
    public static void deleteFile(String path)
    {
        File f = new File(STORAGE_ROOT, path);
        f.delete();
        AppUtil.getInstance().getActivity().getContentResolver().delete(Uri.fromFile(f), null, null);
    }
    
    public static void createDirs(String path)
    {
        File f = new File(STORAGE_ROOT, path);
        f.mkdirs();
    }
}
