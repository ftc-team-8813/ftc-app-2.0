package org.firstinspires.ftc.teamcode.util.python;

import org.firstinspires.ftc.teamcode.util.Logger;
import org.firstinspires.ftc.teamcode.util.Storage;
import org.firstinspires.ftc.teamcode.util.websocket.Server;
import org.firstinspires.ftc.teamcode.util.websocket.UnixSocketServer;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.Random;
import java.util.Scanner;

public class Python
{
    
    public static final String PYTHON_EXE = "/system/bin/python3.9";
    
    private File workDir = Storage.getFile("python");
    private final String scriptFile;
    private boolean started;
    private Process proc;
    
    public Python(String scriptFile)
    {
        this.scriptFile = scriptFile;
    }
    
    public Python setWorkDir(File workDir)
    {
        this.workDir = workDir;
        return this;
    }
    
    public Process start(String... args) throws IOException
    {
        if (started) throw new IllegalStateException("Cannot start() a Python instance multiple times");
        started = true;
        ProcessBuilder builder = new ProcessBuilder();
        builder.command().add(PYTHON_EXE);
        builder.command().add(scriptFile);
        builder.command().addAll(Arrays.asList(args));
        builder.directory(workDir);
        
        proc = builder.start();
    
        return proc;
    }
    
    public Process getProc()
    {
        return proc;
    }
    
    public void stop()
    {
        Thread reaper = new Thread(() -> {
            Logger log = new Logger("Python reaper");
            try
            {
                Thread.sleep(2000);
            } catch (InterruptedException e)
            {
            
            } finally
            {
                log.i("Reaping process");
                proc.destroy();
            }
        }, "Python reaper thread");
        reaper.setDaemon(true);
        reaper.start();
    }
    
    public static File getSocketFile()
    {
        Random rand = new Random();
        return Storage.getFile(String.format("python-ipc-%08x.sock", ((long)rand.nextInt()) & 0xFFFFFFFFL ));
    }
}
