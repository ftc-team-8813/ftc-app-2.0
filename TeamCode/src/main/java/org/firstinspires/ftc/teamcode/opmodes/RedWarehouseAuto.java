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
import org.firstinspires.ftc.teamcode.util.event.EventBus;
import org.firstinspires.ftc.teamcode.vision.CapstoneDetector;
import org.firstinspires.ftc.teamcode.vision.ImageDraw;
import org.firstinspires.ftc.teamcode.vision.webcam.Webcam;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Mat;

// we going to use the event bus system for this so that everything can be done on one thread
@Autonomous(name="Red Warehouse Auto")
public class RedWarehouseAuto extends LoggingOpMode
{
    private Robot robot;
    private Drivetrain drivetrain;
    private Odometry odometry;
    private Duck duck;
    private Lift lift;
    private Webcam webcam;
    private Webcam.SimpleFrameHandler frame_handler;
    private final String WEBCAM_SERIAL = "3522DE6F";
    private Mat detector_frame;

    private ControllerMap controllerMap;
    private ControlMgr controlMgr;
    private Logger logger;

    private ElapsedTime timer;
    private int id = 0;
    private int timer_delay = 1000; // Set high to not trigger next move
    private boolean waiting = false;


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

        controlMgr.addModule(new ServerControl("Server Control"));
        controlMgr.initModules();

        drivetrain = robot.drivetrain;
        odometry = robot.odometry;
        duck = robot.duck;
        lift = robot.lift;

//        odometry.setStartPosition(66, -10, 0);
//        drivetrain.setStart(66, -10, 0); // Must match Odo start position
        odometry.podsDown();

        webcam = Webcam.forSerial(WEBCAM_SERIAL);
        if (webcam == null) throw new IllegalArgumentException("Could not find a webcam with serial number " + WEBCAM_SERIAL);
        frame_handler = new Webcam.SimpleFrameHandler();
        webcam.open(ImageFormat.YUY2, 800, 448, 30, frame_handler);
    }

    @Override
    public void start() {
        odometry.resetEncoders();
//        webcam.requestNewFrame();
//        if (!frame_handler.newFrameAvailable){
//            throw new IllegalArgumentException("New frame not available");
//        }
//        Utils.bitmapToMat(frame_handler.currFramebuffer, detector_frame);
//        CapstoneDetector capstone_detector = new CapstoneDetector(detector_frame);
        // TODO Add detection command
    }

    @Override
    public void loop() {
        // DON'T FORGET BREAKS
        // NEXT CASE SHOULD BE +1
        switch (id){
            case 0:
                drivetrain.goToPosition(22, -21, 0.03);
                break;
            case 1:
                timer_delay = 1;
                waiting = true;
            case 2:
                drivetrain.goToPosition(0, 0, 0.025);
                break;
            case 3:
                drivetrain.goToPosition(2, 40, 0.04);
                break;
            case 4:
                drivetrain.goToPosition(20, 40, 0.04);
                break;

        }

        // Y: -20 X: 36

        double[] odo_data = odometry.getOdoData();
        telemetry.addData("Y: ", odo_data[0]);
        telemetry.addData("X: ", odo_data[1]);
        telemetry.addData("Heading: ", odo_data[2]);

        double[] target_positions = drivetrain.getTargets();
        telemetry.addData("Target Y: ", target_positions[0]);
        telemetry.addData("Target X: ", target_positions[1]);
        telemetry.addData("Target Heading: ", target_positions[2]);

        double[] delta_positions = drivetrain.getPositionDeltas();
        telemetry.addData("Delta Y: ", delta_positions[0]);
        telemetry.addData("Delta X: ", delta_positions[1]);
        telemetry.addData("Delta Heading: ", delta_positions[2]);

        telemetry.addData("Timer: ", timer.seconds());
        telemetry.addData("Id: ", id);
        logger.i(String.format("%d", id));
        telemetry.addData("Reached: ", drivetrain.reached);


        if (!drivetrain.turned){
            logger.i("Update Heading");
            drivetrain.updateHeading();
        } else {
            logger.i("Update Position");
            drivetrain.updatePosition();
        }
        odometry.update();
        telemetry.update();

        if (!waiting){
            timer.reset();
        }
        if (drivetrain.ifReachedPosition()){ // Checks after updates to get values for deltas
            logger.i("Reached Position");
            id += 1;
        } else if (drivetrain.ifReachedHeading()) {
            logger.i("Reached Heading");
            id += 1;
        } else if (timer.seconds() > timer_delay){
            id += 1;
            waiting = false;
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
