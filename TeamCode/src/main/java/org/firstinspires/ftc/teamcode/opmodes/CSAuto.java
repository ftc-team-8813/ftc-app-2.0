package org.firstinspires.ftc.teamcode.opmodes;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
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
import org.firstinspires.ftc.teamcode.util.LoopTimer;
import org.firstinspires.ftc.teamcode.vision.AprilTagDetectionPipeline;
import org.openftc.apriltag.AprilTagDetection;
import org.openftc.easyopencv.OpenCvCamera;
import org.openftc.easyopencv.OpenCvCameraFactory;
import org.openftc.easyopencv.OpenCvCameraRotation;

import java.util.ArrayList;

@Config
@Autonomous(name = "! CS Auto !")
public class CSAuto extends LoggingOpMode{

    private Lift lift;
    private Horizontal horizontal;
    private Arm arm;
    private Intake intake;
    private Drivetrain drivetrain;
    private Odometry odometry;

    private String result = "Nothing";

    private int main_id = 0;
    private int cs_id = 0;
    private int park_id = 0;

    private OpenCvCamera camera;
    private AprilTagDetectionPipeline aprilTagDetectionPipeline;

//    private static final double FEET_PER_METER = 3.28084;

    private final double fx = 578.272;
    private final double fy = 578.272;
    private final double cx = 402.145;
    private final double cy = 221.506;

    private final double tagsize = 0.166;

    private final PID arm_PID = new PID(0.009, 0, 0, 0.1, 0, 0);
    private final PID horizontal_PID = new PID(0.008, 0, 0, 0, 0, 0);
    private final PID lift_PID = new PID(0.02, 0, 0, 0.015, 0, 0);

    private final ElapsedTime timer = new ElapsedTime();
    private final ElapsedTime auto_timer = new ElapsedTime();

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

    public static double t_cs_1 = 0;
    public static double t_cs_2 = 0;
    public static double t_cs_3 = 0;
    public static double t_cs_4 = 0;
    public static double t_cs_5 = 0;

    private double t_cs = t_cs_1;

    public static double x_cs_1 = 0;
    public static double x_cs_2 = 0;
    public static double x_cs_3 = 0;
    public static double x_cs_4 = 0;
    public static double x_cs_5 = 0;

    private double x_cs = x_cs_1;

    public static double y_cs_1 = 31.4;
    public static double y_cs_2 = 31.3;
    public static double y_cs_3 = 31.1;
    public static double y_cs_4 = 31.25;
    public static double y_cs_5 = 31.45;

    private double y_cs = y_cs_1;

    public static double arm_target_cs_1 = (-83.22793502974713)+((1.0272941805214966 - 1)/2) * 9;
    public static double arm_target_cs_2 = (-85.72793502974713)+((1.0272941805214966 - 1)/2) * 9;
    public static double arm_target_cs_3 = (-89.22793502974713)+((1.0272941805214966 - 1)/2) * 9;
    public static double arm_target_cs_4 = (-93.22793502974713)+((1.0272941805214966 - 1)/2) * 9;
    public static double arm_target_cs_5 = (-95.72793502974713)+((1.0272941805214966 - 1)/2) * 9;

    private double arm_target_cs = arm_target_cs_1;

    public double arm_coefficient = 1.056;

    private boolean motion_profile = false;
    private double lift_clip = 1;

    private boolean rise = false;
    private boolean fall = false;

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
        intake.setClawPosition(0.51);

        arm_target_cs_1 -= ((arm_coefficient - 1)/2) * 9;
        arm_target_cs_2 -= ((arm_coefficient - 1)/2) * 9;
        arm_target_cs_3 -= ((arm_coefficient - 1)/2) * 9;
        arm_target_cs_4 -= ((arm_coefficient - 1)/2) * 9;
        arm_target_cs_5 -= ((arm_coefficient - 1)/2) * 9;
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

        lift.setHolderPosition(0.12);

        arm.resetEncoders();
        lift.resetEncoders();
        horizontal.resetEncoders();
        odometry.resetEncoders();
    }


    @Override
    public void start() {
        super.start();
        lift.setHolderPosition(0.3);
        auto_timer.reset();
//        lift_target = 745;
        lift_trapezoid.reset();
    }

    @Override
    public void loop() {

        rise = false;
        fall = false;

        odometry.updatePose(-drivetrain.getHeading());
        motion_profile = false;

        switch (main_id) {

            case 0:
                lift_clip = 1;
                intake.setWristPosition(0.019);
                horizontal_target = -750;
                arm_target = arm_target_cs * arm_coefficient;
                main_id += 1;
                break;
            case 1:
                motion_profile = true;
                if (odometry.getPose().getY() > 5) {
                    fall = true;
                }
                drivetrain.autoMove(y_cs, x_cs, t_cs, 1, 1, 1, odometry.getPose(), telemetry);
                if (drivetrain.hasReached()) {
                    intake.setClawPosition(0.1);
                    main_id += 1;
                    timer.reset();
                }
                break;
            case 2:
                drivetrain.stop();
                if (timer.seconds() > 0.7) {
                    arm_target = -30;
                    main_id += 1;
                    timer.reset();
                }
                break;
            case 3:
                drivetrain.stop();
                if (arm.getCurrentPosition() > -50) {
                    main_id += 1;
                }
                break;
            case 4:
                drivetrain.autoMove(0, 0, 0, 1, 1, 3, odometry.getPose(), telemetry);
                intake.setWristPosition(0.678);
                if (timer.seconds() > 0.5) {
                    horizontal_target = 0;
                    if (horizontal.getCurrentPosition() > -80) {
                        arm_target = 30;
                        if (arm.getCurrentPosition() > -15) {
                            intake.setClawPosition(0.51);
                            main_id += 1;
                        }
                    }
                }
                break;
            case 5:
                arm_target = -28;
                lift_trapezoid.reset();
                drivetrain.autoMove(0, 0, 0, 4, 4, 7, odometry.getPose(), telemetry);
                if (drivetrain.hasReached()) {
                    lift_target = 745;
                    lift.setHolderPosition(0.39);
                    main_id += 1;
                }
                break;
            case 6:
                drivetrain.autoMove(0, 0, 0, 1.9, 1.9, 1.6, odometry.getPose(), telemetry);
                if (drivetrain.hasReached()) {
                    main_id += 1;
                }
                break;
            case 7:
                lift_clip = 1;
                if (lift.getCurrentPosition() > 700) {
                    main_id += 1;
                }
                break;
            case 8:
                lift_target = 0;
                if (lift.getCurrentPosition() < 200) {
                    lift_clip = 0.17;
                    lift.setHolderPosition(0.095);
                    main_id += 1;
                }
                break;
            case 9:
                if (lift.getCurrentPosition() < 150) {
                    intake.setWristPosition(0.019);
                    cs_id += 1;
                    if (cs_id > 4) {
                        main_id += 1;
                    } else {
                        main_id = 0;
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

        drivetrain.update(odometry.getPose(), telemetry,motion_profile, main_id, rise, fall);

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