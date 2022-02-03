package org.firstinspires.ftc.teamcode.opmodes.auto;

import android.graphics.Bitmap;
import android.graphics.ImageFormat;

import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.teamcode.drive.SampleMecanumDrive;
import org.firstinspires.ftc.teamcode.hardware.Drivetrain;
import org.firstinspires.ftc.teamcode.hardware.Duck;
import org.firstinspires.ftc.teamcode.hardware.Intake;
import org.firstinspires.ftc.teamcode.hardware.Lift;
import org.firstinspires.ftc.teamcode.hardware.Robot;
import org.firstinspires.ftc.teamcode.input.ControllerMap;
import org.firstinspires.ftc.teamcode.opmodes.teleop.ControlMgr;
import org.firstinspires.ftc.teamcode.util.Logger;
import org.firstinspires.ftc.teamcode.util.Status;
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
    //private final AutoDrive navigation;
    private final Intake intake;
    private final Duck duck;
    private final Lift lift;
    private final SampleMecanumDrive drive;

    private final ControllerMap controller_map;
    private final ControlMgr control_mgr;
    private final Telemetry telemetry;
    public Logger logger;

    private Webcam webcam;
    private Webcam.SimpleFrameHandler frame_handler;
    private final String WEBCAM_SERIAL = "3522DE6F";
    private final Mat detector_frame = new Mat();
    private Mat send_frame = new Mat();

    public int shipping_height = 0;
    public int x_coord = -1;

    private double y_distance = 0;

    private ElapsedTime lift_timer;
    public ElapsedTime lift_dumped_timer;
    private int id = 0;
    public int height = -1;

    public boolean lift_dumped = false;
    public boolean dump_trigger = false;
    private boolean lift_timer_waiting = false;

    static
    {
        OpenCVLoader.initDebug();
    }

    public AutonomousTemplate(String name, Robot robot, HardwareMap hardware_map, ControllerMap controller_map, Telemetry telemetry, SampleMecanumDrive drive){
        this.name = name;
        this.robot = Robot.initialize(hardware_map, "Autonomous", 0);
        this.logger = new Logger(name);
        this.telemetry = telemetry;
        this.controller_map = controller_map;
        this.control_mgr = new ControlMgr(robot, this.controller_map);

        drivetrain = robot.drivetrain;
        this.drive = drive;
        duck = robot.duck;
        lift = robot.lift;
        intake = robot.intake;
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
        lift_dumped_timer = new ElapsedTime();
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
        logger.i(String.format("X Coord of Block: %03f", shipping_height));
    }

    public void liftSequence(){
        switch (id) {
            case 0:
                lift_timer.reset();
                id += 1;
                break;
            case 1:
                intake.deposit(Status.DEPOSITS.get("carry"));

                if (lift_timer.seconds() > Status.BUCKET_WAIT_TIME){
                    id += 1;
                }
                break;
            case 2:
                lift.extend(Status.STAGES.get("pitstop"), true);
                if (lift.ifReached(Status.STAGES.get("pitstop"))){
                    id += 1;
                }

                lift_timer.reset();
                break;
            case 3:
                switch (height){
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

                if (lift_timer.seconds() > Status.PITSTOP_WAIT_TIME_OUT) {
                    lift_timer.reset();
                    id += 1;
                }
                break;
            case 4:
                double target_height = lift.getLiftCurrentPos();
                switch (height){
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
                        target_height = Status.STAGES.get("high");
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
                    id += 1;
                }
                break;
            case 5:
                if (dump_trigger) {
                    if (!lift_timer_waiting) {
                        lift_timer.reset();
                        lift_timer_waiting = true;
                    }
                    intake.deposit(Status.DEPOSITS.get("dump"));

                    if (lift_timer.seconds() > Status.AUTO_DEPOSIT_TIME) {
                        lift_dumped = true;
                        lift_dumped_timer.reset();
                        intake.deposit(Status.DEPOSITS.get("carry"));
                        lift_timer.reset();
                        lift_timer_waiting = false;
                        id += 1;
                    }
                }
                break;
            case 6:
                if (lift_timer.seconds() > 1) {
                    id += 1;
                }
            case 7:
                lift.extend(Status.STAGES.get("pitstop"), true);
                if (lift.ifReached(Status.STAGES.get("pitstop"))) {
                    id += 1;
                }

                lift_timer.reset();
                break;
            case 8:
                lift.rotate(Status.ROTATIONS.get("in"));

                if (lift_timer.seconds() > Status.PITSTOP_WAIT_TIME) {
                    id += 1;
                }
                break;
            case 9:
                lift.extend(0, true);
                if (lift.ifReached(0)) {
                    id += 1;
                }
                break;
            case 10:
                height = -1;
                id = 0;
                break;
        }
    }

    public void update() {
        if (height > -1){
            liftSequence();
        }

        lift.updateLift();
        telemetry.update();
        lift.updateLift();
        robot.lineFinder.update(telemetry);
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
