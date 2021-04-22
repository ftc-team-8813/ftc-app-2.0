package org.firstinspires.ftc.teamcode.util.python;

import android.text.TextUtils;

import org.firstinspires.ftc.teamcode.util.Logger;
import org.firstinspires.ftc.teamcode.util.Storage;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Random;

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
    
    private Logger log = new Logger("Python");
    
    public Python setWorkDir(File workDir)
    {
        this.workDir = workDir;
        return this;
    }
    
    public Process start(String... args) throws IOException
    {
        if (started)
            throw new IllegalStateException("Cannot start() a Python instance multiple times");
        started = true;
        ProcessBuilder builder = new ProcessBuilder();
        builder.command().add(PYTHON_EXE);
        builder.command().add(scriptFile);
        builder.command().addAll(Arrays.asList(args));
        builder.directory(workDir);
        log.d("Starting Python (in %s):", workDir);
        String cmdString = TextUtils.join(" ", builder.command());
        log.d(cmdString);
        
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
            }
            catch (InterruptedException e)
            {
                log.w("[interrupted]");
            }
            finally
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
        return Storage.getFile(String.format("python-ipc-%08x.sock", ((long) rand.nextInt()) & 0xFFFFFFFFL));
    }
}
