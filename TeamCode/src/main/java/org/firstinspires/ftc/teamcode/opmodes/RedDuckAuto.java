package org.firstinspires.ftc.teamcode.opmodes;

import android.graphics.Bitmap;
import android.graphics.ImageFormat;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.hardware.Drivetrain;
import org.firstinspires.ftc.teamcode.hardware.Duck;
import org.firstinspires.ftc.teamcode.hardware.Lift;
import org.firstinspires.ftc.teamcode.hardware.Robot;
import org.firstinspires.ftc.teamcode.hardware.navigation.Odometry;
import org.firstinspires.ftc.teamcode.input.ControllerMap;
import org.firstinspires.ftc.teamcode.opmodes.teleop.ControlMgr;
import org.firstinspires.ftc.teamcode.opmodes.teleop.ServerControl;
import org.firstinspires.ftc.teamcode.util.Logger;
import org.firstinspires.ftc.teamcode.util.Status;
import org.firstinspires.ftc.teamcode.util.event.EventBus;
import org.firstinspires.ftc.teamcode.vision.CapstoneDetector;
import org.firstinspires.ftc.teamcode.vision.ImageDraw;
import org.firstinspires.ftc.teamcode.vision.webcam.Webcam;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Mat;

import java.sql.Statement;

// we going to use the event bus system for this so that everything can be done on one thread
@Autonomous(name="Red Duck Auto")
public class RedDuckAuto extends LoggingOpMode
{
    private Robot robot;
    private Drivetrain drivetrain;
    private Odometry odometry;
    private Duck duck;
    private Lift lift;
    private Webcam webcam;
    private Webcam.SimpleFrameHandler frame_handler;
    private final String WEBCAM_SERIAL = "3522DE6F";
    private Mat detector_frame = new Mat();

    private ControllerMap controllerMap;
    private ControlMgr controlMgr;
    private Logger logger;

    private ElapsedTime timer;
    private int id = 0;
    private double timer_delay = 1000; // Set high to not trigger next move
    private boolean waiting = false;
    private int shipping_height;


    static
    {
        OpenCVLoader.initDebug();
    }

    @Override
    public void init()
    {
        super.init();
        robot = Robot.initialize(hardwareMap, "Autonomous");
        logger = new Logger("Autonomous");
        controllerMap = new ControllerMap(gamepad1, gamepad2, new EventBus());
        controlMgr = new ControlMgr(robot, controllerMap);
        timer = new ElapsedTime();

//        controlMgr.addModule(new ServerControl("Server Control"));
//        controlMgr.initModules();

        drivetrain = robot.drivetrain;
        odometry = robot.odometry;
        duck = robot.duck;
        lift = robot.lift;

//        odometry.setStartPosition(66, -10, 0);
//        drivetrain.setStart(66, -10, 0); // Must match Odo start position
        odometry.podsUp();

        webcam = Webcam.forSerial(WEBCAM_SERIAL);
        if (webcam == null) throw new IllegalArgumentException("Could not find a webcam with serial number " + WEBCAM_SERIAL);
        frame_handler = new Webcam.SimpleFrameHandler();
        webcam.open(ImageFormat.YUY2, 800, 448, 30, frame_handler);
    }

    @Override
    public void start() {
        timer.reset();
        odometry.resetEncoders();
//        webcam.requestNewFrame();
//        if (!frame_handler.newFrameAvailable){
//            throw new IllegalArgumentException("New frame not available");
//        }
//        Utils.bitmapToMat(frame_handler.currFramebuffer, detector_frame);
//        CapstoneDetector capstone_detector = new CapstoneDetector(detector_frame);
//        int x_coord = capstone_detector.detect();
//        if (100 < x_coord && x_coord < 250){
//            shipping_height = 1;
//        } else if (250 < x_coord && x_coord < 450){
//            shipping_height = 2;
//        } else if (450 < x_coord && x_coord < 600){
//            shipping_height = 3;
//        }
    }

    @Override
    public void loop() {
        if (!waiting){
            timer.reset();
        }

        // DON'T FORGET BREAKS
        // NEXT CASE SHOULD BE +1
        switch (id){
            case 0:
                drivetrain.teleMove(0.215, -0.3, 0);
                timer_delay = 2;
                waiting = true;
                break;
            case 1:
                drivetrain.teleMove(0, 0, 0);
                break;
        }

        telemetry.addData("Timer: ", timer.seconds());
        telemetry.addData("Id: ", id);
        telemetry.addData("Reached: ", waiting);

        lift.updateLift();
        telemetry.update();

        if (timer.seconds() > timer_delay){
            logger.i("Reached Timer: %d", id);
            id += 1;
            waiting = false;
        } else if (lift.ifLifted()){
            logger.i("Reached Lift: %d", id);
            id += 1;
        }
    }

    @Override
    public void stop()
    {
        controlMgr.stop();
        webcam.close();
        super.stop();
    }
}
