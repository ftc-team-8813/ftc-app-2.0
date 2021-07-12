package org.firstinspires.ftc.teamcode.opmodes;

import android.graphics.Bitmap;
import android.graphics.ImageFormat;

import com.qualcomm.hardware.lynx.LynxVoltageSensor;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.hardware.DcMotorEx;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.teamcode.hardware.Robot;
import org.firstinspires.ftc.teamcode.hardware.navigation.PythonNavPath;
import org.firstinspires.ftc.teamcode.util.Logger;
import org.firstinspires.ftc.teamcode.util.Persistent;
import org.firstinspires.ftc.teamcode.util.Scheduler;
import org.firstinspires.ftc.teamcode.util.Time;
import org.firstinspires.ftc.teamcode.util.event.EventBus;
import org.firstinspires.ftc.teamcode.util.event.EventFlow;
import org.firstinspires.ftc.teamcode.util.event.LifecycleEvent;
import org.firstinspires.ftc.teamcode.util.websocket.InetSocketServer;
import org.firstinspires.ftc.teamcode.util.websocket.Server;
import org.firstinspires.ftc.teamcode.vision.ImageDraw;
import org.firstinspires.ftc.teamcode.vision.RingDetector;
import org.firstinspires.ftc.teamcode.vision.webcam.Webcam;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Mat;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

import static org.firstinspires.ftc.teamcode.util.event.LifecycleEvent.START;
import static org.opencv.core.CvType.CV_8UC4;

// we going to use the event bus system for this so that everything can be done on one thread
@Autonomous(name="Auto")
public class MainAuto extends LoggingOpMode
{
    private Robot robot;
    private Logger log = new Logger("Autonomous");

    private EventBus bus;
    private Scheduler scheduler;

    private PythonNavPath autoPath;
    
    
    static
    {
        OpenCVLoader.initDebug();
    }
    
    @Override
    public void init()
    {
        super.init();
        robot = Robot.initialize(hardwareMap, "Autonomous");
        bus = robot.eventBus;
        scheduler = robot.scheduler;
        
        autoPath = new PythonNavPath("autonomous.py", bus, robot);
    }
    
    @Override
    public void init_loop()
    {
        scheduler.loop();
        bus.update();
    }
    
    @Override
    public void start()
    {
        bus.pushEvent(new LifecycleEvent(START));
    }
    
    @Override
    public void loop()
    {

    }
    
    @Override
    public void stop()
    {
        autoPath.stop();
        super.stop();
    }
}
