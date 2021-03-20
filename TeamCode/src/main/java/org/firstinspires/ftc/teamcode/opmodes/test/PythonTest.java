package org.firstinspires.ftc.teamcode.opmodes.test;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.opmodes.LoggingOpMode;
import org.firstinspires.ftc.teamcode.util.Logger;
import org.firstinspires.ftc.teamcode.util.python.Python;
import org.firstinspires.ftc.teamcode.util.websocket.Server;
import org.firstinspires.ftc.teamcode.util.websocket.UnixSocketServer;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

@TeleOp(name="Python Test")
public class PythonTest extends LoggingOpMode
{
    private Process pyProc;
    private Server server;
    private String status = "";
    private int requests = 0;
    private Logger log = new Logger("Python Test");
    
    @Override
    public void init()
    {
        try
        {
            File path = Python.getSocketFile();
            server = new Server(new UnixSocketServer(path.getPath()));
            server.registerProcessor(0x01, (cmd, payload, resp) -> {
                byte[] data = new byte[payload.limit()];
                payload.get(data);
                status = new String(data, StandardCharsets.UTF_8);
                requests++;
            });
            server.startServer();
            
            Python py = new Python("test.py");
            pyProc = py.start(path.getPath());
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }
    
    @Override
    public void loop()
    {
        telemetry.addData("Info", status);
    }
    
    @Override
    public void stop()
    {
        Thread reaper = new Thread(() -> {
            try
            {
                Thread.sleep(2000);
            } catch (InterruptedException e)
            {
            
            } finally
            {
                log.i("Reaping process");
                pyProc.destroy();
            }
        }, "Python reaper thread");
        reaper.setDaemon(true);
        reaper.start();
        
        server.close();
        super.stop();
    }
}
