package org.firstinspires.ftc.teamcode.opmodes;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
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

@Disabled
@Config
@Autonomous(name = "!!! B Six Cone Auto !!!")
public class BSixConeAuto extends LoggingOpMode{

    private Lift lift;
    private Horizontal horizontal;
    private Arm arm;
    private Intake intake;
    private Drivetrain drivetrain;
    private Odometry odometry;

    private String result = "Nothing";

    private int main_id = 0;
    private int cs_id = 0;

    private OpenCvCamera camera;
    private AprilTagDetectionPipeline aprilTagDetectionPipeline;

//    private static final double FEET_PER_METER = 3.28084;

    private final double fx = 578.272;
    private final double fy = 578.272;
    private final double cx = 402.145;
    private final double cy = 221.506;

    private final double tagsize = 0.166;

    private final PID arm_PID = new PID(0.009, 0, 0, 0.1, 0, 0);
    private final PID horizontal_PID = new PID(0.01, 0, 0, 0, 0, 0);
    private final PID lift_PID = new PID(0.02, 0, 0, 0.015, 0, 0);

    private final ElapsedTime timer = new ElapsedTime();

    private final ElapsedTime lift_trapezoid = new ElapsedTime();;
    private final double lift_accel = 0.39;

    private double lift_target = 0;
    private double horizontal_target = 0;
    private double arm_target = 0;

    private double lift_power;
    private double horizontal_power;
    private double arm_power;

    public static double y1 = -49.4;
    public static double x1 = -16.51;
    public static double t1 = 90.0;
    public static double y2 = -49.4;

    public static double t_cs_1 = 90;
    public static double t_cs_2 = 90;
    public static double t_cs_3 = 90;
    public static double t_cs_4 = 90;
    public static double t_cs_5 = 90;

    private double t_cs = t_cs_1;

    public static double x_cs_1 = -400;
    public static double x_cs_2 = -400;
    public static double x_cs_3 = -400;
    public static double x_cs_4 = -400;
    public static double x_cs_5 = -400;

    private double x_cs = x_cs_1;

    public static double y_cs_1 = -49.4;
    public static double y_cs_2 = -49.4;
    public static double y_cs_3 = -49.4;
    public static double y_cs_4 = -49.4;
    public static double y_cs_5 = -49.4;

    private double y_cs = y_cs_1;

    public static double arm_target_cs_1 = -67.0;
    public static double arm_target_cs_2 = -68.0;
    public static double arm_target_cs_3 = -68.5;
    public static double arm_target_cs_4 = -70.0;
    public static double arm_target_cs_5 = -72.0;

    private double arm_target_cs = arm_target_cs_1;

    public double arm_coefficient = 1.056;

    private boolean motion_profile = false;
    private double lift_clip = 1;

    @Override
    public void init() {

        arm_coefficient = Math.sqrt(getBatteryVoltage()/12);

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

        odometry.updatePose(-drivetrain.getHeading());
        motion_profile = false;

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
                    lift_clip = 1;
                }
                break;

            case 4:
                lift_clip = 1;
                drivetrain.autoMove(-49.4,-16.51,90.0,2,2,4, odometry.getPose(),telemetry);
                if (drivetrain.hasReached()) {
                    intake.setWristPosition(0.019);
                    horizontal_target = x_cs;
                    main_id += 1;
                }
                break;
            case 5:
                motion_profile = true;
                drivetrain.autoMove(y_cs,11,t_cs,1,1,0.5, odometry.getPose(), telemetry);
                if (drivetrain.hasReached()) {
                    arm_target = arm_target_cs * arm_coefficient;
                    main_id += 1;
                    timer.reset();
                }
                break;
            case 6:
                if (timer.seconds() > 0.7) {
                    intake.setClawPosition(0.1);
                    main_id += 1;
                    timer.reset();
                }
                break;
            case 7:
                if (timer.seconds() > 0.7) {
                    arm_target = -30;
                    main_id += 1;
                    timer.reset();
                }
                break;
            case 8:
                if (arm.getCurrentPosition() > -50) {
                    drivetrain.autoMove(-55.9,-16.51,126.9375,1,1,3, odometry.getPose(),telemetry);
                    intake.setWristPosition(0.678);
                    if(timer.seconds() >  0.5) {
                        horizontal_target = 0;
                        if (horizontal.getCurrentPosition() > -80) {
                            arm_target = 30;
                            if(arm.getCurrentPosition() > -15) {
                                intake.setClawPosition(0.37);
                                main_id += 1;
                            }
                        }
                    }
                }
                break;
            case 9:
                arm_target = -28;
                lift_trapezoid.reset();
                drivetrain.autoMove(-55.9,-16.51,126.9375,4,4,7, odometry.getPose(),telemetry);
                if (drivetrain.hasReached()) {
                    lift_target = 745;
                    lift.setHolderPosition(0.39);
                    main_id += 1;
                }
                break;
            case 10:
                drivetrain.autoMove(-44.48,-21.97,126.9375,1.9,1.9,1.6, odometry.getPose(),telemetry);
                if (drivetrain.hasReached()) {
                    main_id += 1;
                }
                break;
            case 11:
                lift_clip = 1;
                if (lift.getCurrentPosition() > 700) {
                    main_id += 1;
                }
                break;
            case 12:
                lift_target = 0;
                if (lift.getCurrentPosition() < 200) {
                    lift_clip = 0.17;
                    lift.setHolderPosition(0.095);
                    main_id += 1;
                }
                break;
            case 13:
                if (lift.getCurrentPosition() < 150) {
                    intake.setWristPosition(0.019);
                    cs_id += 1;
                    if (cs_id > 4) {
                        main_id += 1;
                    }
                    else {
                        main_id = 4;
                    }
                }
                break;
        }


        switch (cs_id) {
            case 0:
                x_cs = x_cs_1;
                y_cs = y_cs_1;
                t_cs = t_cs_1;
                arm_target_cs = arm_target_cs_1;
                break;
            case 1:
                x_cs = x_cs_2;
                y_cs = y_cs_2;
                t_cs = t_cs_2;
                arm_target_cs = arm_target_cs_2;
                break;
            case 2:
                x_cs = x_cs_3;
                y_cs = y_cs_3;
                t_cs = t_cs_3;
                arm_target_cs = arm_target_cs_3;
                break;
            case 3:
                x_cs = x_cs_4;
                y_cs = y_cs_4;
                t_cs = t_cs_4;
                arm_target_cs = arm_target_cs_4;
                break;
            case 4:
                x_cs = x_cs_5;
                y_cs = y_cs_5;
                t_cs = t_cs_5;
                arm_target_cs = arm_target_cs_5;
                break;
        }






        lift_power = Range.clip((lift_PID.getOutPut(lift_target, lift.getCurrentPosition(), 1) * Math.min(lift_trapezoid.seconds() * lift_accel, 1)), -lift_clip, lift_clip); //change
        horizontal_power = horizontal_PID.getOutPut(horizontal_target,horizontal.getCurrentPosition(),0); //change
        arm_power = Range.clip(arm_PID.getOutPut(arm_target, arm.getCurrentPosition(), Math.cos(Math.toRadians(arm.getCurrentPosition() + 0))), -0.6, 1); //change

        lift.setPower(lift_power);
        horizontal.setPower(horizontal_power);
        arm.setPower(arm_power);

        drivetrain.update(odometry.getPose(), telemetry,motion_profile);

        telemetry.addData("Main ID", main_id);
        telemetry.addData("Voltage", getBatteryVoltage());
        telemetry.addData("Coefficient", arm_coefficient);
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