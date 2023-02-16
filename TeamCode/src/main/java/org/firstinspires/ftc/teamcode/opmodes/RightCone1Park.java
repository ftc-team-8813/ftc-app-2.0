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
@Autonomous(name = "!! Right 1 Cone Park !!")
public class RightCone1Park extends LoggingOpMode{

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

    private boolean rise = false;
    private boolean fall = false;

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

    public static double x_cs_1 = 8.91;
    public static double x_cs_2 = 8.84;
    public static double x_cs_3 = 8.77;
    public static double x_cs_4 = 8.74;
    public static double x_cs_5 = 8.68;

    private double x_cs = x_cs_1;

    public static double y_cs_1 = -49.4;
    public static double y_cs_2 = -49.4;
    public static double y_cs_3 = -49.4;
    public static double y_cs_4 = -49.4;
    public static double y_cs_5 = -49.4;

    private double y_cs = y_cs_1;

    public static double arm_target_cs_1 = -65.0;
    public static double arm_target_cs_2 = -66.0;
    public static double arm_target_cs_3 = -66.5;
    public static double arm_target_cs_4 = -68.0;
    public static double arm_target_cs_5 = -70.0;

    private double arm_target_cs = arm_target_cs_1;

    public double arm_coefficient = 1.056;

    private boolean motion_profile = false;
    private double lift_clip = 1;

    @Override
    public void init() {

//        arm_coefficient = getBatteryVoltage()/12;

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

        lift.setHolderPosition(0.12);

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
        lift.setHolderPosition(0.3);
    }

    @Override
    public void loop() {

        odometry.updatePose(-drivetrain.getHeading());
        motion_profile = false;

        rise = false;
        fall = false;

        switch (main_id) {
            case 0:
                drivetrain.autoMove(-6.0, 13.0, -29.0, 2, 2, 3, odometry.getPose(), telemetry);
                if (drivetrain.hasReached()) {
                    main_id += 1;
                    lift.setHolderPosition(0.39);
                    timer.reset();
                }
                break;
            case 1:
                drivetrain.autoMove(-30.24, 24.74, -29.0, 0.9, 0.9, 0.9, odometry.getPose(), telemetry);
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
                switch (result) {
                    case "FTC8813: 3":
                        drivetrain.autoMove(-27,-28,0,1,1,1, odometry.getPose(), telemetry);
                        if (drivetrain.hasReached()) {
                            main_id += 1;
                        }
                        break;
                    case "FTC8813: 1":
                        drivetrain.autoMove(-27,18,0,1,1,1, odometry.getPose(),telemetry);
                        if (drivetrain.hasReached()) {
                            main_id += 1;
                        }
                        break;
                    default:
                        drivetrain.autoMove(-27,-7,0,1,1,1, odometry.getPose(), telemetry);
                        if (drivetrain.hasReached()) {
                            main_id += 1;
                        }
                        break;
                }
                break;
            case 5:
                drivetrain.stop();
                break;
        }






        lift_power = Range.clip((lift_PID.getOutPut(lift_target, lift.getCurrentPosition(), 1) * Math.min(lift_trapezoid.seconds() * lift_accel, 1)), -lift_clip, lift_clip); //change
        horizontal_power = horizontal_PID.getOutPut(horizontal_target,horizontal.getCurrentPosition(),0); //change
        arm_power = Range.clip(arm_PID.getOutPut(arm_target, arm.getCurrentPosition(), Math.cos(Math.toRadians(arm.getCurrentPosition() + 0))), -0.6, 1); //change

        lift.setPower(lift_power);
        horizontal.setPower(horizontal_power);
        arm.setPower(arm_power);

        drivetrain.update(odometry.getPose(), telemetry,motion_profile, main_id, false, false);

        telemetry.addData("Main ID", main_id);
//        telemetry.addData("Voltage", getBatteryVoltage());
        telemetry.addData("Coefficient", arm_coefficient);
        telemetry.addData("Loop Time: ", LoopTimer.getLoopTime());
        telemetry.update();

        LoopTimer.resetTimer();
    }

    @Override
    public void stop() {
        super.stop();
    }

}