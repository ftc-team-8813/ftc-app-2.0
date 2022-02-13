package org.firstinspires.ftc.teamcode.opmodes.auto;

import android.graphics.Bitmap;
import android.graphics.ImageFormat;

import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.teamcode.drive.SampleMecanumDrive;
import org.firstinspires.ftc.teamcode.hardware.AutoDrive;
import org.firstinspires.ftc.teamcode.hardware.Drivetrain;
import org.firstinspires.ftc.teamcode.hardware.Duck;
import org.firstinspires.ftc.teamcode.hardware.Intake;
import org.firstinspires.ftc.teamcode.hardware.Lift;
import org.firstinspires.ftc.teamcode.hardware.Robot;
import org.firstinspires.ftc.teamcode.hardware.events.DriveEvent;
import org.firstinspires.ftc.teamcode.hardware.events.IntakeEvent;
import org.firstinspires.ftc.teamcode.hardware.events.LiftEvent;
import org.firstinspires.ftc.teamcode.input.ControllerMap;
import org.firstinspires.ftc.teamcode.opmodes.teleop.ControlMgr;
import org.firstinspires.ftc.teamcode.util.Logger;
import org.firstinspires.ftc.teamcode.util.Scheduler;
import org.firstinspires.ftc.teamcode.util.Status;
import org.firstinspires.ftc.teamcode.util.event.EventBus;
import org.firstinspires.ftc.teamcode.util.event.EventFlow;
import org.firstinspires.ftc.teamcode.util.websocket.InetSocketServer;
import org.firstinspires.ftc.teamcode.util.websocket.Server;
import org.firstinspires.ftc.teamcode.vision.CapstoneDetector;
import org.firstinspires.ftc.teamcode.vision.webcam.Webcam;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Mat;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

public class AutonomousTemplate {
    String name;
    private final Robot robot;
    private Server server;
    private final Drivetrain drivetrain;
    private final AutoDrive navigation;
    private final Intake intake;
    private final Duck duck;
    private final Lift lift;

    private final EventBus ev_bus;
    private final Scheduler scheduler;
    private final ControllerMap controller_map;
    private final ControlMgr control_mgr;
    private final Telemetry telemetry;
    public Logger logger;

    private Webcam webcam;
    private Webcam.SimpleFrameHandler frame_handler;
    private final String WEBCAM_SERIAL = "3522DE6F";
    private final Mat detector_frame = new Mat();
    private Mat send_frame = new Mat();

    private int id = 0;
    private int lift_id = 0;
    private int intake_id = 0;
    private boolean lifting = false;
    private boolean driving = false;
    private boolean detecting_freight = false;

    private int drive_trigger;
    private double inches;
    private double forward;
    private double strafe;

    public int shipping_height = 0;
    public int x_coord = -1;

    private ElapsedTime lift_timer;
    public int height = -1;

    static
    {
        OpenCVLoader.initDebug();
    }

    public AutonomousTemplate(String name, Robot robot, HardwareMap hardware_map, ControllerMap controller_map, Telemetry telemetry){
        this.name = name;
        this.robot = Robot.initialize(hardware_map, "Autonomous", 0);
        this.logger = new Logger(name);
        this.telemetry = telemetry;
        this.controller_map = controller_map;
        this.ev_bus = robot.eventBus;
        this.scheduler = robot.scheduler;
        this.control_mgr = new ControlMgr(robot, this.controller_map);

        drivetrain = robot.drivetrain;
        navigation = robot.navigation;
        duck = robot.duck;
        lift = robot.lift;
        intake = robot.intake;

        robot.lineFinder.initialize();
        logger.i("Line Finder Init Value: %f", robot.lineFinder.alpha_init * Status.LIGHT_MULTIPLIER);
    }

    public void init_server(){
        try
        {
            server = new Server(new InetSocketServer(20000));
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
        server.registerProcessor(0x01, (cmd, payload, resp) -> { // Get frame
            if (detector_frame == null) return;

            Bitmap bmp = Bitmap.createBitmap(send_frame.cols(), send_frame.rows(), Bitmap.Config.ARGB_8888);
            Utils.matToBitmap(send_frame, bmp);

            ByteArrayOutputStream os = new ByteArrayOutputStream(16384);
            bmp.compress(Bitmap.CompressFormat.JPEG, 80, os); // probably quite slow
            byte[] data = os.toByteArray();
            resp.respond(ByteBuffer.wrap(data));
        });
        server.startServer();
    }

    public void init_lift(){
        lift.extend(0, false);
        lift.rotate(Status.ROTATIONS.get("in"));
        robot.intake.deposit(Status.DEPOSITS.get("carry"));
        lift_timer = new ElapsedTime();
    }

    public void init_camera(){
        webcam = Webcam.forSerial(WEBCAM_SERIAL);
        if (webcam == null) throw new IllegalArgumentException("Could not find a webcam with serial number " + WEBCAM_SERIAL);
        frame_handler = new Webcam.SimpleFrameHandler();
        webcam.open(ImageFormat.YUY2, 800, 448, 30, frame_handler);
    }

    public void check_image(){
        if (shipping_height != 0){
            return;
        }

        webcam.requestNewFrame();
        if (!frame_handler.newFrameAvailable) {
            //throw new IllegalArgumentException("New frame not available");
            shipping_height = 3;
            return;
        }
        Utils.bitmapToMat(frame_handler.currFramebuffer, detector_frame);
        CapstoneDetector capstone_detector = new CapstoneDetector(detector_frame, logger);
        capstone_detector.setName(name);
        int[] capstone_data = capstone_detector.detect();
        shipping_height = capstone_data[0];
        x_coord = capstone_data[1];
        send_frame = capstone_detector.getStoredFrame();

        logger.i(String.format("Shipping Height: %01d", x_coord));
        logger.i(String.format("X Coord of Block: %01d", shipping_height));
    }

    public void raiseLift(int height){
        raiseLift(height, -1, 0, 0);
    }

    public void raiseLift(int height, int drive_trigger, double inches, double strafe){
        this.height = height;
        this.drive_trigger = drive_trigger;
        this.inches = inches;
        this.strafe = strafe;
        lifting = true;
    }

    public void moveForward(double inches, double strafe){
        this.inches = inches;
        this.strafe = strafe;
        driving = true;
    }

    public void moveTillFreight(double forward, double strafe){
        this.forward = forward;
        this.strafe = strafe;
    }

    public void lineFind(){
        if (name.startsWith("Blue")){
            logger.i("Tried to Move");
            drivetrain.move(-0.4, -0.2, 0);
        } else {
            logger.i("Tried to Move");
            drivetrain.move(0.4, 0.2, 0);
        }
        if (robot.lineFinder.lineFound()){
            logger.i("Line Finder Current: %d", robot.lineFinder.line_finder.alpha());
            drivetrain.stop();
            id += 1;
        }
    }

    public void updateLift(){
        if (lift_id == drive_trigger){
            logger.i("Moving within Lift");
            this.moveForward(inches, strafe);
        }
        switch (lift_id) {
            case 0:
                switch (this.height){
                    case 1:
                        lift.rotate(Status.ROTATIONS.get("low_out"));
                        break;
                    case 2:
                        lift.rotate(Status.ROTATIONS.get("mid_out"));
                        break;
                    case 3:
                        lift.rotate(Status.ROTATIONS.get("high_out"));
                        break;
                    case 4:
                        lift.rotate(Status.ROTATIONS.get("neutral_out"));
                        break;
                    case 5:
                        lift.rotate(Status.ROTATIONS.get("high_out2"));
                        break;
                    case 6:
                        lift.rotate(Status.ROTATIONS.get("high_out"));
                        lift.moveOutrigger(Status.OUTRIGGERS.get("down"));
                        break;
                }

                double target_height = lift.getLiftCurrentPos();
                switch (this.height){
                    case 0:
                        target_height = 0;
                        break;
                    case 1:
                        target_height = Status.STAGES.get("low");
                        break;
                    case 2:
                        target_height = Status.STAGES.get("mid");
                        break;
                    case 3:
                        target_height = Status.STAGES.get("high") - 100;
                        break;
                    case 4:
                        target_height = Status.STAGES.get("neutral");
                        break;
                    case 5:
                        target_height = Status.STAGES.get("high2");
                        break;
                    case 6:
                        target_height = Status.STAGES.get("really high");
                        break;
                }
                lift.extend(target_height, true);
                if (lift.ifReached(target_height)){
                    lift_id += 1;
                    lift_timer.reset();
                }
                break;
            case 1:
                intake.deposit(Status.DEPOSITS.get("dump"));
                if (lift_timer.seconds() > Status.AUTO_DEPOSIT_TIME) {
                    intake.deposit(Status.DEPOSITS.get("carry"));
                    lift_timer.reset();
                    lift_id += 1;
                }
                break;
            case 2:
                if (lift_timer.seconds() > Status.AUTO_DEPOSIT_TIME) {
                    lift_id += 1;
                    lift_timer.reset();
                }
                break;
            case 3:
                lift.rotate(Status.ROTATIONS.get("in"));
                lift.extend(0, true);
                if (lift.ifReached(0)) {
                    lift_id += 1;
                }
                break;
            case 4:
                this.height = -1;
                lifting = false;
                lift_id = 0;
                id += 1;
                break;
        }
    }

    public void updateDrivetrain(){
        final double TICKS_PER_INCHES = 1 / ((1 / Status.TICKS_PER_ROTATION) * (96 * Math.PI / 25.4) * (1 / 15.2));
        double diff_ticks = inches * TICKS_PER_INCHES - (drivetrain.front_right.getCurrentPosition() +
                drivetrain.back_right.getCurrentPosition() +
                drivetrain.front_left.getCurrentPosition() +
                drivetrain.back_left.getCurrentPosition()) / 4;

        double forward_power = diff_ticks * 0.0004;
        logger.i("Diff Ticks: %f", diff_ticks);
        drivetrain.move(forward_power, strafe, 0);
        if (Math.abs(diff_ticks) < 200) {
            drivetrain.stop();
            drivetrain.resetEncoders();
            driving = false;
            id += 1;
        }
    }

    public void updateFreightDetection(){
        switch (intake_id){
            case 0:
                if (name.startsWith("Blue")){
                    intake.deposit(Status.DEPOSITS.get("front"));
                } else {
                    intake.deposit(Status.DEPOSITS.get("back"));
                }
                intake_id += 1; // TODO May need timer
                break;
            case 1:
                if (name.startsWith("Blue")){
                    intake.setIntakeFront(0.7);
                } else {
                    intake.setIntakeBack(0.7);
                }
                drivetrain.move(forward, strafe, 0);
                intake_id += 1;
                break;
            case 2:
                if (intake.freightDetected()){
                    if (name.startsWith("Blue")) {
                        intake.deposit(Status.DEPOSITS.get("front_tilt"));
                    } else {
                        intake.deposit(Status.DEPOSITS.get("back_tilt"));
                    }
                    drivetrain.stop();
                    lift_timer.reset();
                    intake_id += 1;
                }
                break;
            case 3:
                // Gives time to keep block in bucket when tilting
                if (lift_timer.seconds() > 0.5){
                    intake_id += 1;
                }
                break;
            case 4:
                intake.deposit(Status.DEPOSITS.get("carry"));
                detecting_freight = false;
                intake_id = 0;
                id += 1;
                break;
        }
    }

    public int update() {
        if (lifting) {
            updateLift();
        }
        if (driving){
            updateDrivetrain();
        }
        if (detecting_freight){
            updateFreightDetection();
        }

        ev_bus.update();
        scheduler.loop();
        lift.updateLift();
        telemetry.update();
        lift.updateLift();
        return id;
    }

    public void stop(){
        control_mgr.stop();
        if (webcam != null){
            webcam.close();
        }
        if (server != null) {
            server.close();
        }
    }
}
