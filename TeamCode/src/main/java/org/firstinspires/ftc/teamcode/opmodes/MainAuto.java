package org.firstinspires.ftc.teamcode.opmodes;

import android.graphics.Bitmap;
import android.graphics.ImageFormat;

import com.qualcomm.hardware.lynx.LynxVoltageSensor;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.hardware.DcMotorEx;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.teamcode.hardware.Robot;
import org.firstinspires.ftc.teamcode.hardware.navigation.NavPath;
import org.firstinspires.ftc.teamcode.util.Logger;
import org.firstinspires.ftc.teamcode.util.Persistent;
import org.firstinspires.ftc.teamcode.util.Scheduler;
import org.firstinspires.ftc.teamcode.util.Storage;
import org.firstinspires.ftc.teamcode.util.event.EventBus;
import org.firstinspires.ftc.teamcode.util.event.EventFlow;
import org.firstinspires.ftc.teamcode.util.websocket.Server;
import org.firstinspires.ftc.teamcode.vision.ImageDraw;
import org.firstinspires.ftc.teamcode.vision.RingDetector;
import org.firstinspires.ftc.teamcode.vision.webcam.Webcam;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Mat;


import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;

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
    private Bitmap serverFrame;
    private ImageDraw serverDraw;
    
    private static final int DETECT_REQUEST_FRAME = 1;
    private static final int DETECT_PROCESS_FRAME = 2;
    private RingDetector detector;
    private int detectStage = 0;
    private int ringsDetected = -1;
    
    private Webcam.SimpleFrameHandler frameHandler;
    private Webcam webcam;
    
    private Logger log = new Logger("Autonomous");
    
    private static final String WEBCAM_SERIAL = "3522DE6F";
    
    private ByteBuffer telemBuf;
    private boolean telemUsed = true;
    
    private boolean homeComplete = false;
    
    static
    {
        OpenCVLoader.initDebug();
    }
    
    @Override
    public void init()
    {
        robot = Robot.initialize(hardwareMap, "Autonomous");
        bus = new EventBus();
        scheduler = new Scheduler(bus);
        robot.turret.connectEventBus(bus);
        telemBuf = ByteBuffer.allocate(65535);
        robot.imu.initialize(bus, scheduler);
        
        robot.wobble.up();
        robot.wobble.close();
        
        autoPath = new NavPath(Storage.getFile("nav_paths/test_auto_v3.json"),
                bus, scheduler, robot, robot.config.getAsJsonObject("nav"));
        autoPath.addActuator("turret", (params) -> {
            String action = params.get("action").getAsString();
            switch (action)
            {
                case "rotate":
                    double angle = params.get("angle").getAsDouble();
                    robot.turret.rotate(angle, true);
                    break;
                case "rotatePs":
                    robot.turret.rotate(turretPos[ringCount], true);
                    robot.turret.shooter.powershot(ringCount);
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
                    log.v("Wobble DOWN");
                    robot.wobble.down();
                    break;
                case "up":
                    log.v("Wobble UP");
                    robot.wobble.up();
                    break;
                case "close":
                    log.v("Wobble CLOSE");
                    robot.wobble.close();
                    break;
                case "open":
                    log.v("Wobble OPEN");
                    robot.wobble.open();
                    break;
            }
        });
        autoPath.addCondition("0", () -> 0);
        autoPath.addCondition("incRingCount", () -> ++ringCount);
        autoPath.addCondition("webcamState", () -> webcam.getState());
        autoPath.addCondition("ringsSeen", () -> ringsDetected);
        autoPath.addActuator("webcamDetect", (params) -> {
            detectStage = DETECT_REQUEST_FRAME;
        });
        
        webcam = Webcam.forSerial(WEBCAM_SERIAL);
        if (webcam == null) throw new IllegalArgumentException("Could not find a webcam with serial number " + WEBCAM_SERIAL);
        frameHandler = new Webcam.SimpleFrameHandler();
        webcam.open(ImageFormat.YUY2, 800, 448, 30, frameHandler);
    
        detectorFrame = new Mat(800, 448, CV_8UC4);
    
        detector = new RingDetector(800, 448);
    
        autoPath.load();
        
        // load config
        /*
        turretPos = new double[] {
                autoPath.getConstant("powershot0"),
                autoPath.getConstant("powershot1"),
                autoPath.getConstant("powershot2")
        };
         */
    
        Persistent.clear();
        homeComplete = false;
    
        initServer();
        LynxVoltageSensor voltageSensor = hardwareMap.get(LynxVoltageSensor.class, "Control Hub");
        double voltage = voltageSensor.getVoltage();
        log.d("Battery voltage: %.3f", voltage);
        autoPath.getNavigator().adjForVoltage(voltage);
        
        robot.turret.startZeroFind();
    }
    
    @Override
    public void init_loop()
    {
        
        robot.turret.updateInit(telemetry);
        if (robot.turret.findComplete() && !homeComplete)
        {
            homeComplete = true;
            Persistent.put("turret_zero_found", true);
        }
        
        telemetry.addData("IMU status", robot.imu.getStatus() + " -- " + robot.imu.getDetailStatus());
        telemetry.addData("IMU heading", robot.imu.getHeading());
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
        if (detectStage == DETECT_REQUEST_FRAME)
        {
            frameHandler.newFrameAvailable = false;
            webcam.requestNewFrame();
            detectStage = DETECT_PROCESS_FRAME;
        }
        else if (detectStage == DETECT_PROCESS_FRAME && frameHandler.newFrameAvailable)
        {
            frameHandler.newFrameAvailable = false;
            detectStage = 0;
            serverFrame = frameHandler.currFramebuffer.copy(Bitmap.Config.ARGB_8888, false);
            serverDraw = new ImageDraw();
            Utils.bitmapToMat(frameHandler.currFramebuffer, detectorFrame);
            double area = detector.detect(detectorFrame, serverDraw);
            if      (area < 700)   ringsDetected = 0;
            else if (area < 2500)  ringsDetected = 1;
            else if (area < 10000) ringsDetected = 4;
            else                   ringsDetected = -1;
            log.d("Detected: %d rings (area=%.3f)", ringsDetected, area);
        }
        
        if (telemUsed)
        {
            telemBuf.clear();
            // TODO add navigation data
            telemBuf.putDouble(robot.turret.getPosition());
            telemBuf.putDouble(robot.turret.getTarget());
            telemBuf.putDouble(robot.turret.shooter.motor.getPower());
            telemBuf.putDouble(((DcMotorEx)robot.turret.shooter.motor).getVelocity(AngleUnit.RADIANS));
            telemBuf.putDouble(ringsDetected);
            telemUsed = false;
        }
        
        robot.drivetrain.getOdometry().updateDeltas();
        webcam.loop(bus);
        autoPath.loop(telemetry);
        robot.turret.update(telemetry);
        scheduler.loop();
        bus.update();
        
        telemetry.addData("Rings Detected", ringsDetected);
    }
    
    @Override
    public void stop()
    {
        webcam.close();
        if (server != null) server.close();
        super.stop();
    }
    
    
    /////////////////////////////
    // DEBUG SERVER THINGS
    
    private Server server;
    
    private void initServer()
    {
        server = new Server(8814);
        Logger.serveLogs(server, 0x01);
        autoPath.getNavigator().serve(server, 0x05);
        
        server.registerProcessor(0x02, (cmd, payload, resp) -> {
            if (serverFrame != null)
            {
                ByteArrayOutputStream os = new ByteArrayOutputStream(32768);
                serverFrame.compress(Bitmap.CompressFormat.JPEG, 80, os); // probably quite slow
                serverFrame.recycle();
                serverFrame = null;
                byte[] data = os.toByteArray();
                resp.respond(ByteBuffer.wrap(data));
            }
        });
        server.registerProcessor(0x03, (cmd, payload, resp) -> {
            if (serverDraw != null)
            {
                ByteBuffer drawBuf = ByteBuffer.allocate(65535);
                serverDraw.write(drawBuf);
                drawBuf.flip();
                resp.respond(drawBuf);
                serverDraw = null;
            }
        });
        server.registerProcessor(0x04, (cmd, payload, resp) -> {
            if (!telemUsed)
            {
                telemBuf.flip();
                resp.respond(telemBuf);
                telemUsed = true;
            }
        });
        
        server.startServer();
    }
}
