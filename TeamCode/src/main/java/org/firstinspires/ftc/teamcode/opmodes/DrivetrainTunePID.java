package org.firstinspires.ftc.teamcode.opmodes;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.hardware.VoltageSensor;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.teamcode.hardware.Arm;
import org.firstinspires.ftc.teamcode.hardware.Drivetrain;
import org.firstinspires.ftc.teamcode.hardware.Horizontal;
import org.firstinspires.ftc.teamcode.hardware.Intake;
import org.firstinspires.ftc.teamcode.hardware.Lift;
import org.firstinspires.ftc.teamcode.hardware.Robot;
import org.firstinspires.ftc.teamcode.hardware.navigation.Odometry;
import org.firstinspires.ftc.teamcode.hardware.navigation.PID;
import org.firstinspires.ftc.teamcode.util.Logger;
import org.firstinspires.ftc.teamcode.util.LoopTimer;
import org.firstinspires.ftc.teamcode.vision.AprilTagDetectionPipeline;
import org.openftc.apriltag.AprilTagDetection;
import org.openftc.easyopencv.OpenCvCamera;
import org.openftc.easyopencv.OpenCvCameraFactory;
import org.openftc.easyopencv.OpenCvCameraRotation;

import java.util.ArrayList;

@Config
@Autonomous(name = "Drivetrain Tune PID")
public class DrivetrainTunePID extends LoggingOpMode{

    private Lift lift;
    private Horizontal horizontal;
    private Arm arm;
    private Intake intake;
    private Drivetrain drivetrain;
    private Odometry odometry;

    private String result = "Nothing";

    private int main_id = 0;
    private int arm_id = 0;

    private OpenCvCamera camera;
    private AprilTagDetectionPipeline aprilTagDetectionPipeline;

//    private static final double FEET_PER_METER = 3.28084;

    private double fx = 578.272;
    private double fy = 578.272;
    private double cx = 402.145;
    private double cy = 221.506;

    private double ISUM = 0;

    private double tagsize = 0.166;

    private final PID arm_PID = new PID(0.009, 0, 0, 0.1, 0, 0);
    private final PID horizontal_PID = new PID(0.01, 0, 0, 0, 0, 0);
    private final PID lift_PID = new PID(0.02, 0, 0, 0.015, 0, 0);

    private ElapsedTime timer = new ElapsedTime();

    private ElapsedTime lift_trapezoid = new ElapsedTime();;
    private double lift_accel = 0.39;

    private double lift_target = 0;
    private double horizontal_target = 0;
    private double arm_target = 0;

    private ElapsedTime liftTimer = new ElapsedTime();
    private boolean liftTimerReset = false;

    private final Logger log = new Logger("Cone Auto");

    private double timer_point_1;
    private double timer_point_2;
    private double timer_point_3;
    private double timer_point_4;
    private double timer_point_5;
    private double timer_point_6;

    private double lift_power;
    private double horizontal_power;
    private double arm_power;

    public static double y1 = -49.4;
    public static double x1 = -16.51;
    public static double t1 = 90.0;
    public static double y2 = -49.4;
    public static double x2 = 8.91;
    public static double x3 = 8.85;
    public static double x4 = 8.85;
    public static double x5 = 8.85;
    public static double x6 = 8.85;
    public static double t2 = 90.0;

    public static double armTarget = -69.5;
    public static double armTarget2 = -70.5;
    public static double armTarget3 = -71.5;
    public static double armTarget4 = -72.5;
    public static double armTarget5 = -73.5;

    public double arm_coefficient = 1.056;

    private boolean motion_profile = false;

    private double lift_clip = 1;

//    public static double x = -30.5;
//    public static double y = -62.0;
//    public static double SIDE_LENGTH = 14;
//    public static double SIDE_WIDTH = 12;

//    private FtcDashboard dashboard;
//
//    private static void rotatePoints(double[] xPoints, double[] yPoints, double angle, double x_cor, double y_cor) {
//        for (int i = 0; i < xPoints.length; i++) {
//            double x = xPoints[i];
//            double y = yPoints[i];
//            xPoints[i] = x * Math.cos(angle) - y * Math.sin(angle);
//            yPoints[i] = x * Math.sin(angle) + y * Math.cos(angle);
//            x = xPoints[i];
//            y = yPoints[i];
//            xPoints[i] = x + x_cor;
//            yPoints[i] = y + y_cor;
//        }
//    }

    @Override
    public void init() {
        super.init();
        Robot robot = Robot.initialize(hardwareMap);
        lift = robot.lift;
        horizontal = robot.horizontal;
        arm = robot.arm;
        intake = robot.intake;
        drivetrain = robot.drivetrain;
        odometry = robot.odometry;

        odometry.Down();
        int cameraMonitorViewId = hardwareMap.appContext.getResources().getIdentifier("cameraMonitorViewId", "id", hardwareMap.appContext.getPackageName());
        camera = OpenCvCameraFactory.getInstance().createWebcam(hardwareMap.get(WebcamName.class, "Webcam 1"), cameraMonitorViewId);
        aprilTagDetectionPipeline = new AprilTagDetectionPipeline(tagsize, fx, fy, cx, cy);

        camera.setPipeline(aprilTagDetectionPipeline);
        camera.openCameraDeviceAsync(new OpenCvCamera.AsyncCameraOpenListener()
        {
            @Override
            public void onOpened()
            {
                camera.startStreaming(800,448, OpenCvCameraRotation.UPRIGHT);
            }

            @Override
            public void onError(int errorCode)
            {

            }
        });

//        telemetry.setMsTransmissionInterval(50);

        odometry.resetEncoders();

        telemetry = new MultipleTelemetry(telemetry, FtcDashboard.getInstance().getTelemetry());

//        dashboard = FtcDashboard.getInstance();

        intake.setWristPosition(0.019);
        intake.setClawPosition(0.37);
    }

    @Override
    public void init_loop() {
        super.init_loop();

        ArrayList<AprilTagDetection> currentDetections = aprilTagDetectionPipeline.getLatestDetections();

        if(currentDetections.size() != 0) {

            for (AprilTagDetection tag : currentDetections) {
                if (tag.id == 107) {
                    result = "FTC8813: 1";
                    break;
                }
                else if (tag.id == 350) {
                    result = "FTC8813: 2";
                    break;
                }
                else if (tag.id == 25) {
                    result = "FTC8813: 3";
                    break;
                }
                else {
                    result = "Nothing";
                }

            }
        }


        telemetry.addData("Detected", result);

        telemetry.update();

        if(!arm.getLimit()){
            arm.setPower(0.5);
        }
        if(!lift.getLimit()){
            lift.setPower(-0.2);
        }
        if(!horizontal.getLimit()){
            horizontal.setPower(0.3);
        }

        if(arm.getLimit()){
            arm.resetEncoders();
        }
        if(lift.getLimit()){
            lift.resetEncoders();
        }
        if(horizontal.getLimit()){
            horizontal.resetEncoders();
        }

        lift.setHolderPosition(0.3);

        arm.resetEncoders();
        lift.resetEncoders();
        horizontal.resetEncoders();
        odometry.resetEncoders();
    }


    @Override
    public void start() {
        super.start();
        lift_target = 745;
        lift_trapezoid.reset();
    }

    @Override
    public void loop() {

        arm_coefficient = getBatteryVoltage()/12;



        odometry.updatePose(-drivetrain.getHeading());

//        timer_point_1 = LoopTimer.getLoopTime();

        motion_profile = false;

//        x = -30.5 - odometry.getPose().getY();
//        y = -62 - odometry.getPose().getX();
//
//        double sL = SIDE_LENGTH / 2;
//        double sW = SIDE_WIDTH / 2;
//
//        double[] bxPoints = { sW, -sW, -sW, sW };
//        double[] byPoints = { sL, sL, -sL, -sL };

        switch (main_id) {
            case 0:
                drivetrain.autoMove(-6.0,-13.0,29.0,2,2,3, odometry.getPose(), telemetry);
                if (drivetrain.hasReached()) {
                    main_id += 1;
                    lift.setHolderPosition(0.39);
                    timer.reset();
                }
                break;
            case 1:
                drivetrain.autoMove(-30.24,-24.74,29.0,0.9,0.9,0.9, odometry.getPose(), telemetry);
                if (drivetrain.hasReached() || timer.seconds() > 6) {
                    main_id += 1;
                    arm_target = -28;
                    lift_target = 0;
                }
                break;
            case 2:
                if (lift.getCurrentPosition() < 200) {
                    lift_clip = 0.17;
                    lift.setHolderPosition(0.095);
                    main_id += 1;
                }
                break;
            case 3:
                if (lift.getCurrentPosition() < 150) {
                    main_id += 1;
                }
                break;
            case 4:
                drivetrain.autoMove(y1,x1,t1,2,2,4, odometry.getPose(),telemetry);
                if (drivetrain.hasReached()) {
                    horizontal_target = -800;
                    arm_target = armTarget * arm_coefficient;
                    main_id += 1;
                }
                break;
            case 5:
                motion_profile = true;
                drivetrain.autoMove(y2,x2,t2,1,1,0.5, odometry.getPose(), telemetry);
                if (drivetrain.hasReached()) {
                    arm_target = armTarget * arm_coefficient;
                    intake.setClawPosition(0.1);
                    main_id += 1;
                    timer.reset();
                }
                break;
            case 6:
                if (timer.seconds() > 0.7) {
                    arm_target = -30;
                    main_id += 1;
                    timer.reset();
                }
                break;
            case 7:
                if (arm.getCurrentPosition() > -50) {
                    drivetrain.autoMove((y1-6.5),x1,126.9375,1,1,3, odometry.getPose(),telemetry);
                    intake.setWristPosition(0.678);
                    if(timer.seconds() >  0.5) {
                        horizontal_target = 0;
                        if (horizontal.getCurrentPosition() > -80) {
                            arm_target = 30;
                            lift_trapezoid.reset();
                            main_id += 1;
                        }
                    }
                }
                break;
            case 8:
                lift_target = 745;
                drivetrain.autoMove((y1-6.5),x1,126.9375,4,4,7, odometry.getPose(),telemetry);
                if (drivetrain.hasReached()) {
                    main_id += 1;
                    intake.setClawPosition(0.37);
                    arm_target = -28;
                }
                break;
            case 9:
                drivetrain.autoMove(-43.48,-22.97,126.9375,1.9,1.9,1.6, odometry.getPose(),telemetry);
                if (drivetrain.hasReached()) {
                    lift.setHolderPosition(0.39);
                    lift_trapezoid.reset();
                    main_id += 1;
                }
                break;
            case 10:
                lift_clip = 1;
                if (lift.getCurrentPosition() > 700) {
                    main_id += 1;
                }
                break;
            case 11:
                lift_target = 0;
                if (lift.getCurrentPosition() < 200) {
                    lift_clip = 0.17;
                    lift.setHolderPosition(0.095);
                    main_id += 1;
                }
                break;
            case 12:
                if (lift.getCurrentPosition() < 150) {
                    main_id += 1;
                }
                break;
            case 13:
                drivetrain.autoMove(y1,x1,t1,1,1,4, odometry.getPose(),telemetry);
                if (drivetrain.hasReached()) {
                    intake.setWristPosition(0.019);
                    horizontal_target = -800;
                    arm_target = armTarget2 * arm_coefficient;
                    main_id += 1;
                }
                break;
            case 14:
                motion_profile = true;
                drivetrain.autoMove(y2,x3,t2,1,1,0.5, odometry.getPose(), telemetry);
                if (drivetrain.hasReached()) {
                    intake.setClawPosition(0.1);
                    main_id += 1;
                    timer.reset();
                }
                break;
            case 15:
                if (timer.seconds() > 0.7) {
                    arm_target = -30;
                    main_id += 1;
                    timer.reset();
                }
                break;
            case 16:
                if (arm.getCurrentPosition() > -50) {
                    drivetrain.autoMove((y1-6.5),x1,126.9375,1,1,3, odometry.getPose(),telemetry);
                    intake.setWristPosition(0.678);
                    if(timer.seconds() >  0.5) {
                        horizontal_target = 0;
                        if (horizontal.getCurrentPosition() > -80) {
                            arm_target = 30;
                            lift_trapezoid.reset();
                            main_id += 1;
                        }
                    }
                }
                break;
            case 17:
                lift_target = 745;
                drivetrain.autoMove((y1-6.5),x1,126.9375,4,4,7, odometry.getPose(),telemetry);
                if (drivetrain.hasReached()) {
                    main_id += 1;
                    intake.setClawPosition(0.37);
                    arm_target = -28;
                    timer.reset();
                }
                break;
            case 18:
                drivetrain.autoMove(-43.48,-22.97,126.9375,1.9,1.9,1.6, odometry.getPose(),telemetry);
                if (drivetrain.hasReached() || timer.seconds() > 5) {
                    lift.setHolderPosition(0.39);
                    lift_trapezoid.reset();
                    main_id += 1;
                }
                break;
            case 19:
                lift_clip = 1;
                if (lift.getCurrentPosition() > 700) {
                    main_id += 1;
                }
                break;
            case 20:
                lift_target = 0;
                if (lift.getCurrentPosition() < 200) {
                    lift.setHolderPosition(0.095);
                    lift_clip = 0.17;
                    main_id += 1;
                }
                break;
            case 21:
                if (lift.getCurrentPosition() < 150) {
                    main_id += 1;
                }
                break;
            case 22:
                drivetrain.autoMove(y1,x1,t1,1,1,4, odometry.getPose(),telemetry);
                if (drivetrain.hasReached()) {
                    intake.setWristPosition(0.019);
                    horizontal_target = -800;
                    arm_target = armTarget3 * arm_coefficient;
                    main_id += 1;
                }
                break;
            case 23:
                motion_profile = true;
                drivetrain.autoMove(y2,x4,t2,1,1,0.5, odometry.getPose(), telemetry);
                if (drivetrain.hasReached()) {
                    intake.setClawPosition(0.1);
                    main_id += 1;
                    timer.reset();
                }
                break;
            case 24:
                if (timer.seconds() > 0.7) {
                    arm_target = -30;
                    main_id += 1;
                    timer.reset();
                }
                break;
            case 25:
                if (arm.getCurrentPosition() > -50) {
                    drivetrain.autoMove((y1-6.5),x1,126.9375,1,1,3, odometry.getPose(),telemetry);
                    intake.setWristPosition(0.678);
                    if(timer.seconds() >  0.5) {
                        horizontal_target = 0;
                        if (horizontal.getCurrentPosition() > -80) {
                            arm_target = 30;
                            lift_trapezoid.reset();
                            main_id += 1;
                        }
                    }
                }
                break;
            case 26:
                lift_target = 745;
                drivetrain.autoMove((y1-6.5),x1,126.9375,4,4,7, odometry.getPose(),telemetry);
                if (drivetrain.hasReached()) {
                    main_id += 1;
                    intake.setClawPosition(0.37);
                    arm_target = -28;
                    timer.reset();
                }
                break;
            case 27:
                drivetrain.autoMove(-43.48,-22.97,126.9375,1.9,1.9,1.6, odometry.getPose(),telemetry);
                if (drivetrain.hasReached() || timer.seconds() > 5) {
                    lift.setHolderPosition(0.39);
                    lift_trapezoid.reset();
                    main_id += 1;
                }
                break;
            case 28:
                lift_clip = 1;
                if (lift.getCurrentPosition() > 700) {
                    main_id += 1;
                }
                break;
            case 29:
                lift_target = 0;
                if (lift.getCurrentPosition() < 200) {
                    lift.setHolderPosition(0.095);
                    lift_clip = 0.17;
                    main_id += 1;
                }
                break;
            case 30:
                if (lift.getCurrentPosition() < 150) {
                    main_id += 1;
                }
                break;
            case 31:
                drivetrain.autoMove(y1,x1,t1,1,1,4, odometry.getPose(),telemetry);
                if (drivetrain.hasReached()) {
                    intake.setWristPosition(0.019);
                    horizontal_target = -800;
                    arm_target = armTarget4 * arm_coefficient;
                    main_id += 1;
                }
                break;
            case 32:
                motion_profile = true;
                drivetrain.autoMove(y2,x5,t2,1,1,0.5, odometry.getPose(), telemetry);
                if (drivetrain.hasReached()) {
                    intake.setClawPosition(0.1);
                    main_id += 1;
                    timer.reset();
                }
                break;
            case 33:
                if (timer.seconds() > 0.7) {
                    arm_target = -30;
                    main_id += 1;
                    timer.reset();
                }
                break;
            case 34:
                if (arm.getCurrentPosition() > -50) {
                    drivetrain.autoMove((y1-6.5),x1,126.9375,1,1,3, odometry.getPose(),telemetry);
                    intake.setWristPosition(0.678);
                    if(timer.seconds() >  0.5) {
                        horizontal_target = 0;
                        if (horizontal.getCurrentPosition() > -80) {
                            arm_target = 30;
                            lift_trapezoid.reset();
                            main_id += 1;
                        }
                    }
                }
                break;
            case 35:
                lift_target = 745;
                drivetrain.autoMove((y1-6.5),x1,126.9375,4,4,7, odometry.getPose(),telemetry);
                if (drivetrain.hasReached()) {
                    main_id += 1;
                    intake.setClawPosition(0.37);
                    arm_target = -28;
                    timer.reset();
                }
                break;
            case 36:
                drivetrain.autoMove(-43.48,-22.97,126.9375,1.9,1.9,1.6, odometry.getPose(),telemetry);
                if (drivetrain.hasReached() || timer.seconds() > 5) {
                    lift.setHolderPosition(0.39);
                    lift_trapezoid.reset();
                    main_id += 1;
                }
                break;
            case 37:
                lift_clip = 1;
                if (lift.getCurrentPosition() > 700) {
                    main_id += 1;
                }
                break;
            case 38:
                lift_target = 0;
                if (lift.getCurrentPosition() < 200) {
                    lift.setHolderPosition(0.095);
                    lift_clip = 0.17;
                    main_id += 1;
                }
                break;
            case 39:
                if (lift.getCurrentPosition() < 150) {
                    main_id += 1;
                }
                break;
            case 40:
                drivetrain.autoMove(y1,x1,t1,1,1,4, odometry.getPose(),telemetry);
                if (drivetrain.hasReached()) {
                    intake.setWristPosition(0.019);
                    horizontal_target = -800;
                    arm_target = armTarget5 * arm_coefficient;
                    main_id += 1;
                }
                break;
            case 41:
                motion_profile = true;
                drivetrain.autoMove(y2,x6,t2,1,1,0.5, odometry.getPose(), telemetry);
                if (drivetrain.hasReached()) {
                    intake.setClawPosition(0.1);
                    main_id += 1;
                    timer.reset();
                }
                break;
            case 42:
                if (timer.seconds() > 0.7) {
                    arm_target = -30;
                    main_id += 1;
                    timer.reset();
                }
                break;
            case 43:
                if (arm.getCurrentPosition() > -50) {
                    drivetrain.autoMove((y1-6.5),x1,126.9375,1,1,3, odometry.getPose(),telemetry);
                    intake.setWristPosition(0.678);
                    if(timer.seconds() >  0.5) {
                        horizontal_target = 0;
                        if (horizontal.getCurrentPosition() > -80) {
                            arm_target = 30;
                            lift_trapezoid.reset();
                            main_id += 1;
                        }
                    }
                }
                break;
            case 44:
                lift_target = 745;
                drivetrain.autoMove((y1-6.5),x1,126.9375,4,4,7, odometry.getPose(),telemetry);
                if (drivetrain.hasReached()) {
                    main_id += 1;
                    intake.setClawPosition(0.37);
                    arm_target = -28;
                    timer.reset();
                }
                break;
            case 45:
                drivetrain.autoMove(-43.48,-22.97,126.9375,1.9,1.9,1.6, odometry.getPose(),telemetry);
                if (drivetrain.hasReached() || timer.seconds() > 5) {
                    lift.setHolderPosition(0.39);
                    lift_trapezoid.reset();
                    main_id += 1;
                }
                break;
            case 46:
                lift_clip = 1;
                if (lift.getCurrentPosition() > 700) {
                    main_id += 1;
                }
                break;
            case 47:
                lift_target = 0;
                if (lift.getCurrentPosition() < 200) {
                    lift.setHolderPosition(0.095);
                    lift_clip = 0.17;
                    main_id += 1;
                }
                break;
            case 48:
                if (lift.getCurrentPosition() < 150) {
                    main_id += 1;
                }
                break;
            case 49:
                drivetrain.autoMove(y1,x1,t1,1,1,4, odometry.getPose(),telemetry);
                if (drivetrain.hasReached()) {
                    main_id += 1;
                }
                break;




        }

//        timer_point_2 = LoopTimer.getLoopTime();

        lift_power = Range.clip((lift_PID.getOutPut(lift_target, lift.getCurrentPosition(), 1) * Math.min(lift_trapezoid.seconds() * lift_accel, 1)), -lift_clip, lift_clip); //change
        horizontal_power = horizontal_PID.getOutPut(horizontal_target,horizontal.getCurrentPosition(),0); //change
        arm_power = Range.clip(arm_PID.getOutPut(arm_target, arm.getCurrentPosition(), Math.cos(Math.toRadians(arm.getCurrentPosition() + 0))), -0.6, 1); //change

//        timer_point_3 = LoopTimer.getLoopTime();

        lift.setPower(lift_power);
        horizontal.setPower(horizontal_power);
        arm.setPower(arm_power);

//        timer_point_4 = LoopTimer.getLoopTime();

        drivetrain.update(odometry.getPose(), telemetry,motion_profile);

//        double rot;
//
//        if(Math.signum(-drivetrain.getHeading()) == -1) {
//            rot = ((-drivetrain.getHeading()) + 360);
//        }
//        else {
//            rot = -drivetrain.getHeading();
//        }
//
//        rot %= 360;

//        rotatePoints(bxPoints, byPoints,-Math.toRadians(rot),x,y);
//
//        TelemetryPacket packet = new TelemetryPacket();
//        packet.fieldOverlay()
//                .setStrokeWidth(1)
//                .setFill("black")
//                .fillPolygon(bxPoints, byPoints);
//        dashboard.sendTelemetryPacket(packet);

//        timer_point_5 = LoopTimer.getLoopTime();


        ISUM += (lift_PID.getError() * lift_PID.getTimer());

        telemetry.addData("Main ID", main_id);
        telemetry.addData("Integral Sum", lift_PID.getIntergralSum());
        telemetry.addData("Error", lift_PID.getError());
        telemetry.addData("Timer", lift_PID.getTimer());
        telemetry.addData("Error *  Timer", (lift_PID.getError() * lift_PID.getTimer()));
        telemetry.addData("Actual I Sum", ISUM);
//        telemetry.addData("Distance", intake.getDistance());
//        telemetry.addData("Lift Power", lift_power);
//        telemetry.addData("Horizontal Power", horizontal_power);
//        telemetry.addData("Arm Power", arm_power);
//        telemetry.addData("Lift Target",lift_target);
//        telemetry.addData("Horizontal Target",horizontal_target);
//        telemetry.addData("Arm Target",arm_target);
//        telemetry.addData("Lift Position",lift.getCurrentPosition());
//        telemetry.addData("Horizontal Position",horizontal.getCurrentPosition());
//        telemetry.addData("Arm Position",arm.getCurrentPosition());
//        telemetry.addData("Timer Point 1", timer_point_1);
//        telemetry.addData("Timer Point 2", timer_point_2);
//        telemetry.addData("Timer Point 3", timer_point_3);
//        telemetry.addData("Timer Point 4", timer_point_4);
//        telemetry.addData("Timer Point 5", timer_point_5);
        telemetry.addData("Voltage", getBatteryVoltage());
        telemetry.addData("Loop Time: ", LoopTimer.getLoopTime());
        telemetry.update();

        LoopTimer.resetTimer();
    }

    @Override
    public void stop() {
        super.stop();
    }

    double getBatteryVoltage() {
        double result = Double.POSITIVE_INFINITY;
        for (VoltageSensor sensor : hardwareMap.voltageSensor) {
            double voltage = sensor.getVoltage();
            if (voltage > 0) {
                result = Math.min(result, voltage);
            }
        }
        return result;
    }

}