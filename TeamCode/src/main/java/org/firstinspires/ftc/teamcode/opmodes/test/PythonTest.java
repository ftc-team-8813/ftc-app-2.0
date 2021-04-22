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

@TeleOp(name = "Python Test")
public class PythonTest extends LoggingOpMode
{
    private Python python;
    private Server server;
    private String status = "";
    private int requests = 0;
    private Logger log = new Logger("Python Test");
    
    @Override
    public void init()
    {
        super.init();
        try
        {
            File path = Python.getSocketFile();
            server = new Server(new UnixSocketServer(path.getPath()));
            server.registerProcessor(0x01, (cmd, payload, resp) -> {
                byte[] data = new byte[payload.remaining()];
                payload.get(data);
                status = new String(data, StandardCharsets.UTF_8);
                requests++;
            });
            server.startServer();
            
            python = new Python("test.py");
            python.start(path.getPath());
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
        python.stop();
        server.close();
        super.stop();
    }
}
