package org.firstinspires.ftc.teamcode.opmodes.auto;

import android.graphics.ImageFormat;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.hardware.Drivetrain;
import org.firstinspires.ftc.teamcode.hardware.Duck;
import org.firstinspires.ftc.teamcode.hardware.Lift;
import org.firstinspires.ftc.teamcode.hardware.Robot;
import org.firstinspires.ftc.teamcode.hardware.navigation.Odometry;
import org.firstinspires.ftc.teamcode.input.ControllerMap;
import org.firstinspires.ftc.teamcode.opmodes.teleop.ControlMgr;
import org.firstinspires.ftc.teamcode.opmodes.teleop.ServerControl;
import org.firstinspires.ftc.teamcode.util.Logger;
import org.firstinspires.ftc.teamcode.vision.CapstoneDetector;
import org.firstinspires.ftc.teamcode.vision.webcam.Webcam;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Mat;

public class AutonomousTemplate {
    String name;
    private Robot robot;
    private Drivetrain drivetrain;
    private Odometry odometry;
    private Duck duck;
    private Lift lift;

    private ControllerMap controller_map;
    private ControlMgr control_mgr;
    private Telemetry telemetry;
    private Logger logger;

    private Webcam webcam;
    private Webcam.SimpleFrameHandler frame_handler;
    private final String WEBCAM_SERIAL = "3522DE6F";
    private Mat detector_frame = new Mat();

    private ElapsedTime timer;
    private int id = 0;
    private double timer_delay = 1000; // Set high to not trigger next move
    private boolean waiting = false;
    private int shipping_height;

    static
    {
        OpenCVLoader.initDebug();
    }

    public AutonomousTemplate(String name, Robot robot, HardwareMap hardware_map, ControllerMap controller_map, Telemetry telemetry){
        this.name = name;
        this.robot = Robot.initialize(hardware_map, "Autonomous");
        this.logger = new Logger(name);
        this.telemetry = telemetry;
        this.controller_map = controller_map;
        this.control_mgr = new ControlMgr(robot, this.controller_map);
        this.timer = new ElapsedTime();

        drivetrain = robot.drivetrain;
        odometry = robot.odometry;
        duck = robot.duck;
        lift = robot.lift;
    }

    public void init_server(){
        control_mgr.addModule(new ServerControl("Server Control"));
        control_mgr.initModules();
    }

    public void init_odometry(double x, double y, double heading){
        odometry.setStartPosition(66, -10, 0);
        drivetrain.setStart(66, -10, 0); // Must match Odo start position
        odometry.podsUp();
    }

    public void init_camera(){
        webcam = Webcam.forSerial(WEBCAM_SERIAL);
        if (webcam == null) throw new IllegalArgumentException("Could not find a webcam with serial number " + WEBCAM_SERIAL);
        frame_handler = new Webcam.SimpleFrameHandler();
        webcam.open(ImageFormat.YUY2, 800, 448, 30, frame_handler);
    }

    public void check_image(){
        webcam.requestNewFrame();
        if (!frame_handler.newFrameAvailable){
            throw new IllegalArgumentException("New frame not available");
        }
        Utils.bitmapToMat(frame_handler.currFramebuffer, detector_frame);
        CapstoneDetector capstone_detector = new CapstoneDetector(detector_frame);
        int x_coord = capstone_detector.detect();
        if (100 < x_coord && x_coord < 250){
            shipping_height = 1;
        } else if (250 < x_coord && x_coord < 450){
            shipping_height = 2;
        } else if (450 < x_coord && x_coord < 600){
            shipping_height = 3;
        }
    }

    public void set_timer(double delay){
        timer_delay = delay;
        waiting = true;
    }

    public int update() {
        if (!waiting) {
            timer.reset();
        }

        telemetry.addData("Timer: ", timer.seconds());
        telemetry.addData("Id: ", id);
        telemetry.addData("Reached: ", waiting);

        lift.updateLift();
        telemetry.update();

        if (drivetrain.ifReachedPosition()){
            logger.i("Reached Position: %d", id);
            id += 1;
        } else if (drivetrain.ifReachedHeading()){
            logger.i("Reached Heading: %d", id);
            id += 1;
        } else if (timer.seconds() > timer_delay){
            logger.i("Reached Timer: %d", id);
            id += 1;
            waiting = false;
        } else if (lift.ifLifted()){
            logger.i("Reached Lift: %d", id);
            id += 1;
        }

        return id;
    }

    public void stop(){
        control_mgr.stop();
        webcam.close();
    }
}
