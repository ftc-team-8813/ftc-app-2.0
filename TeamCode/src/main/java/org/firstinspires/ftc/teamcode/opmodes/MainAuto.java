package org.firstinspires.ftc.teamcode.opmodes;

import android.graphics.ImageFormat;
import android.net.wifi.aware.IdentityChangedListener;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.teamcode.hardware.IMU;
import org.firstinspires.ftc.teamcode.hardware.Robot;
import org.firstinspires.ftc.teamcode.hardware.events.TurretEvent;
import org.firstinspires.ftc.teamcode.hardware.navigation.AngleHold;
import org.firstinspires.ftc.teamcode.hardware.navigation.NavPath;
import org.firstinspires.ftc.teamcode.util.Configuration;
import org.firstinspires.ftc.teamcode.util.Logger;
import org.firstinspires.ftc.teamcode.util.Scheduler;
import org.firstinspires.ftc.teamcode.util.Scheduler.Timer;
import org.firstinspires.ftc.teamcode.util.Storage;
import org.firstinspires.ftc.teamcode.util.Time;
import org.firstinspires.ftc.teamcode.util.event.Event;
import org.firstinspires.ftc.teamcode.util.event.EventBus;
import org.firstinspires.ftc.teamcode.util.event.EventBus.Subscriber;
import org.firstinspires.ftc.teamcode.util.event.EventFlow;
import org.firstinspires.ftc.teamcode.util.event.LifecycleEvent;
import org.firstinspires.ftc.teamcode.util.event.TimerEvent;
import org.firstinspires.ftc.teamcode.vision.RingDetector;
import org.firstinspires.ftc.teamcode.vision.webcam.Webcam;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Mat;


import static org.firstinspires.ftc.teamcode.util.event.LifecycleEvent.START;
import static org.opencv.core.CvType.CV_8UC4;

// we going to use the event bus system for this so that everything can be done on one thread
@Autonomous(name="Auto")
public class MainAuto extends LoggingOpMode
{
    private EventBus bus;
    private Scheduler scheduler;
    private EventFlow autoFlow;
    
    private Robot robot;
    
    private NavPath autoPath;
    
    private int forward1;
    private int forward2; // distance from forward1 to park
    private double movePower;
    private double[] turretPos;
    
    private int ringCount = 0;
    
    private Mat detectorFrame;
    private RingDetector detector;
    private boolean frameUsed = true;
    private int ringsDetected = -1;
    
    private Webcam.SimpleFrameHandler frameHandler;
    private Webcam webcam;
    
    private Logger log = new Logger("Autonomous");
    
    private static final String serial = "3522DE6F";
    
    static
    {
        OpenCVLoader.initDebug();
    }
    
    @Override
    public void init()
    {
        robot = new Robot(hardwareMap);
        // load config
        JsonObject conf = Configuration.readJson(Storage.getFile("autonomous.json"));
        JsonArray pos = conf.getAsJsonArray("turretPos");
        turretPos = new double[pos.size()];
        for (int i = 0; i < pos.size(); i++)
        {
            turretPos[i] = pos.get(i).getAsDouble();
        }
        bus = new EventBus();
        scheduler = new Scheduler(bus);
        robot.turret.connectEventBus(bus);
        
        autoPath = new NavPath(Storage.getFile("nav_paths/auto_v1.json"), bus, scheduler, robot, robot.config.getAsJsonObject("nav"));
        autoPath.addActuator("turret", (params) -> {
            String action = params.get("action").getAsString();
            switch (action)
            {
                case "rotatePs":
                    robot.turret.rotate(turretPos[ringCount], true);
                    break;
                case "push":
                    robot.turret.push();
                    break;
                case "unpush":
                    robot.turret.unpush();
                    break;
            }
        });
        autoPath.addActuator("shooter", (params) -> {
            String action = params.get("action").getAsString();
            switch (action)
            {
                case "start":
                    robot.turret.shooter.start();
                    break;
                case "stop":
                    robot.turret.shooter.stop();
                    break;
            }
        });
        autoPath.addActuator("wobble", (params) -> {
            String action = params.get("action").getAsString();
            switch (action)
            {
                case "down":
                    robot.wobble.down();
                    break;
                case "up":
                    robot.wobble.up();
                    break;
                case "close":
                    robot.wobble.close();
                    break;
                case "open":
                    robot.wobble.open();
                    break;
            }
        });
        autoPath.addCondition("0", () -> 0);
        autoPath.addCondition("incRingCount", () -> ++ringCount);
        autoPath.addCondition("webcamState", () -> webcam.getState());
        autoPath.addCondition("ringsSeen", () -> ringsDetected);
        autoPath.addActuator("webcamDetect", (params) -> {
            // assume a frame is available
            double area = detector.detect(detectorFrame, null);
            if      (area < 700)   ringsDetected = 0;
            else if (area < 2500)  ringsDetected = 1;
            else if (area < 10000) ringsDetected = 4;
            else                   ringsDetected = -1;
            log.d("Detected: %d rings (area=%.3f)", ringsDetected, area);
            frameUsed = true;
        });
        
        webcam = Webcam.forSerial(serial);
        if (webcam == null) throw new IllegalArgumentException("Could not find a webcam with serial number " + serial);
        frameHandler = new Webcam.SimpleFrameHandler();
        webcam.open(ImageFormat.YUY2, 800, 448, 30, frameHandler);
    
        detectorFrame = new Mat(800, 448, CV_8UC4);
    
        detector = new RingDetector(800, 448);
    
        autoPath.load();
    }
    
    @Override
    public void init_loop()
    {
        autoPath.loop(telemetry);
        scheduler.loop();
        bus.update();
    }
    
    @Override
    public void start()
    {
        // bus.pushEvent(new LifecycleEvent(START));
        autoPath.start();
    }
    
    @Override
    public void loop()
    {
        if (frameHandler.newFrameAvailable && frameUsed)
        {
            frameHandler.newFrameAvailable = false;
            frameUsed = false;
            Utils.bitmapToMat(frameHandler.currFramebuffer, detectorFrame);
            webcam.requestNewFrame();
        }
        
        webcam.loop(bus);
        autoPath.loop(telemetry);
        robot.turret.update(telemetry);
        scheduler.loop();
        bus.update();
        
        telemetry.addData("Rings Detected", ringsDetected);
    }
}
