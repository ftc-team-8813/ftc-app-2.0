package org.firstinspires.ftc.teamcode.util;

import android.content.Intent;
import android.net.Uri;
import android.os.Environment;

import org.firstinspires.ftc.robotcore.internal.system.AppUtil;

import java.io.File;
import java.io.IOException;

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
    }
    
    public static File createFile(String path)
    {
        File f = new File(STORAGE_ROOT, path);
        try { f.createNewFile(); }
        catch (IOException e) { e.printStackTrace(); }
        scanFile(f.getPath());
        return f;
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
