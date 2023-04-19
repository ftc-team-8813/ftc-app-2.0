package org.firstinspires.ftc.teamcode.opmodes;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.arcrobotics.ftclib.geometry.Pose2d;
import com.qualcomm.hardware.lynx.LynxModule;
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
import java.util.List;

@Config
@Autonomous(name = "!  !! Medium Pole Auto !!  !")
public class MediumPoleAuto extends LoggingOpMode{

    private Lift lift;
    private Horizontal horizontal;
    private Arm arm;
    private Intake intake;
    private Drivetrain drivetrain;
    private Odometry odometry;

    private String result = "Nothing";

    private int main_id = 0;
    private int transfer_id = -1;
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
    public static double exponent = 0.75;
    public static double multiplier = 2.1;

    private final PID horizontal_PID = new PID(0.008, 0, 0, 0, 0, 0);
    private final PID lift_PID = new PID(0.02, 0, 0, 0.03, 0, 0);

    private final ElapsedTime timer = new ElapsedTime();
    private final ElapsedTime auto_timer = new ElapsedTime();

    private final ElapsedTime lift_trapezoid = new ElapsedTime();;
    private final double lift_accel = 0.39;

    private double lift_target = 0;
    private double horizontal_target = 0;
    private double arm_target = -20;

    public static double lift_retract = 405; // change faster but not to fast

    private double lift_power;
    private double horizontal_power;
    private double arm_power;

    public static double arm_target_cs_1 = -102.8;
    public static double arm_target_cs_2 = -107.9;
    public static double arm_target_cs_3 = -113.0;
    public static double arm_target_cs_4 = -117.1;
    public static double arm_target_cs_5 = -121.0;

    private double arm_target_cs = arm_target_cs_1;


    public static double horizontal_target_cs_1 = 1255; //1285
    public static double horizontal_target_cs_2 = 1320;
    public static double horizontal_target_cs_3 = 1310;
    public static double horizontal_target_cs_4 = 1340;
    public static double horizontal_target_cs_5 = 1350;

    private double horizontal_target_cs = horizontal_target_cs_1;

    private double voltage_cofficient;

    private boolean motion_profile = false;
    private double lift_clip = 1;

    private boolean rise = false;
    private boolean fall = false;

    //TODO Change the parkings stuff

    private boolean park = false;
    private double voltage;

    private int main_park_id = 0;

    @Override
    public void init() {

        List<LynxModule> allHubs = hardwareMap.getAll(LynxModule.class);

        for (LynxModule hub : allHubs) {
            hub.setBulkCachingMode(LynxModule.BulkCachingMode.AUTO);
        }

        voltage = getBatteryVoltage();
        voltage_cofficient = Math.pow(((12.4/voltage) * multiplier),exponent);

        super.init();
        Robot robot = Robot.initialize(hardwareMap);
        lift = robot.lift;
        horizontal = robot.horizontal;
        arm = robot.arm;
        intake = robot.intake;
        drivetrain = robot.drivetrain;
        odometry = robot.odometry;

        odometry.Down();
        lift.setLatchPosition(0.55);
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

        intake.setWristPosition(0.021);
        intake.setClawPosition(0.35);

        arm.setPosition(-30);
        lift.setPower(-0.2);
        horizontal.setPower(-0.3);
    }

    @Override
    public void init_loop() {
        super.init_loop();

        arm.setPosition(-20);

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

        if(lift.getCurrentAmps() > 1.3){
            lift.resetEncoders();
            lift.setPower(0);
        }
        if(horizontal.getCurrentAmps() > 3){
            horizontal.resetEncoders();
            horizontal.setPower(0);
        }

        arm.update();

        lift.setHolderPosition(0.12);

        arm.resetEncoders();
        lift.resetEncoders();
        horizontal.resetEncoders();
        odometry.resetEncoders();
    }


    @Override
    public void start() {
        super.start();
//        drivetrain.resetEncoders();
        lift.setLatchPosition(0);
        auto_timer.reset();
    }

    @Override
    public void loop() {

        /*
        Arm Possitions
        50 = -10
        feed forawrd potentially remove to  stop the move then move back thing
         */

        arm.update();
        lift.update();
        horizontal.updatePosition();
        drivetrain.updateHeading();

        rise = false;
        fall = false;

        odometry.updatePose(-drivetrain.getHeading());
        Pose2d odometryPose = odometry.getPose();

        motion_profile = false;

        // close claw 0.73
        // open claw 0.35
        if (auto_timer.seconds() <= 26.0) {
            switch (main_id) {
                case 0:
                    drivetrain.autoMove(-10, 3, 0, 1.5, 1.5, 2, odometryPose, telemetry);
                    if (drivetrain.hasReached()) {
                        main_id += 1;
                        lift_target = 400;
                        lift_trapezoid.reset();
                    }
                    break;
                case 1:
                    drivetrain.autoMove(-41.23, 4, 0, 1.5, 2, 2, odometryPose, telemetry);
                    if (drivetrain.hasReached()) {
                        main_id += 1;
                    }
                    break;
                case 2:
                    drivetrain.autoMove(-41.23, 4, 106.7625, 1.5, 1.5, 1.5, odometryPose, telemetry);
                    if (drivetrain.hasReached()) {
                        main_id += 1;
                        lift.setHolderPosition(0.40);
                        lift_target = 450;
                        lift_trapezoid.reset();
                    }
                    break;
                case 3:
                    drivetrain.autoMove(-41.43, 2.309, 106.7625, 0.6, 0.6, 1, odometryPose, telemetry);
                    if (drivetrain.hasReached()) {
                        transfer_id += 1;
                        main_id += 1;
                    }
                    break;
                case 4:
                    lift.setHolderPosition(0.40);
//                    if (lift.getCurrentPosition() > 350) { //405
//                        lift.setLatchPosition(0.8);
//                    }
                    if (lift.getCurrentPosition() > 430) {
//                        if (cs_id != 0) {
//                            lift.setHolderPosition(0.25);
//                        }
                        lift_target = 400;
                        main_id += 1;
                    }
                    break;
                case 5:
                    if (lift.getCurrentPosition() < 405) {
                        lift.setLatchPosition(0.8);
                        lift.setHolderPosition(0.121);
                        lift_target = 0;
                        main_id += 1;
                    }
//                    if (lift.getCurrentPosition() < lift_retract && cs_id != 0) {
//                        lift.setHolderPosition(0.121);
//                        main_id += 1;
//                    }
//                    else if (lift.getCurrentPosition() < 370 && cs_id == 0) {
//                        lift.setHolderPosition(0.121);
//                        main_id += 1;
//                    }
                    break;
                case 6:
                    if (transfer_id == 2) {
                        transfer_id += 1;
                        timer.reset();
                    }
                    if (lift.getCurrentPosition() < 150) {
                        if (transfer_id == 6) {
                            lift.setLatchPosition(0.55);
                            transfer_id += 1;
                            main_id += 1;
                            timer.reset();
                        }
                    }
                    break;
                case 7:
                    if (transfer_id == 9 && timer.seconds() > 0.55) {
                        lift_target = 460;
                        lift_trapezoid.reset();
                        cs_id += 1;
                        if (cs_id > 4) {
                            main_id += 1;
                            transfer_id += 1;
                        } else {
                            transfer_id = 0;
                            main_id = 4;
                        }
                    }
                    break;
                case 8:
                    lift.setHolderPosition(0.40);
//                    if (lift.getCurrentPosition() > 350) { //405
//                        lift.setLatchPosition(0.8);
//                    }
                    if (lift.getCurrentPosition() > 430) {
                        lift_target = 400;
                        main_id += 1;
                    }
                    break;
                case 9:
                    if (lift.getCurrentPosition() < 405) {
                        lift.setLatchPosition(0.8);
                        lift.setHolderPosition(0.121);
                        lift_target = 0;
                    }
                    break;
            }

            switch (cs_id) {
                case 0:
                    arm_target_cs = arm_target_cs_1;
                    horizontal_target_cs = horizontal_target_cs_1;
                    break;
                case 1:
                    arm_target_cs = arm_target_cs_2;
                    horizontal_target_cs = horizontal_target_cs_2;
                    break;
                case 2:
                    arm_target_cs = arm_target_cs_3;
                    horizontal_target_cs = horizontal_target_cs_3;
                    break;
                case 3:
                    arm_target_cs = arm_target_cs_4;
                    horizontal_target_cs = horizontal_target_cs_4;
                    break;
                case 4:
                    arm_target_cs = arm_target_cs_5;
                    horizontal_target_cs = horizontal_target_cs_5;
                    break;
            }

            switch (transfer_id) {
                case 0:
                    intake.setWristPosition(0.021);
                    horizontal_target = horizontal_target_cs;
                    arm_target = arm_target_cs;
                    timer.reset();
                    transfer_id += 1;
                    break;
                case 1:
                    if (arm.getCurrentEncoderPosition() < (arm_target_cs + 15) && arm.getCurrentEncoderPosition() > (arm_target_cs - 15) && timer.seconds() > 0.7) {
                        transfer_id += 1;
                    }
                    break;
                case 3:
                    if (horizontal_target <= 1440) {
                        horizontal_target += 4;
                    }
                    if (intake.getDistance() < 35 || timer.seconds() > 1.7) {
                        intake.setClawPosition(0.73);
                        timer.reset();
                        transfer_id += 1;
                    }
                    break;
                case 4:
                    if (timer.seconds() > 0.5) {
                        arm_target = -40;
                        transfer_id += 1;
                    }
                    break;
                case 5:
                    horizontal_target -= 7;
                    if (arm.getCurrentEncoderPosition() > -70) {
                        horizontal_target = 0;
                        intake.setWristPosition(0.692);
                        transfer_id += 1;
                    }
                    break;
                case 7:
                    if (timer.seconds() > 0.5 || horizontal.getCurrentPosition() < 20) {
                        arm_target = -12;
                        transfer_id += 1;
                        timer.reset();
                    }
                    break;
                case 8:
                    if (arm.getCurrentEncoderPosition() > -14 && horizontal.getCurrentPosition() < 30) {
                        lift.setLatchPosition(0);
                        if (timer.seconds() > 0.7) {
                            intake.setClawPosition(0.35);
                            timer.reset();
                            transfer_id += 1;
                        }
                    }
                    break;
            }
        }
        else {
            horizontal_target = 0;
            lift_target = 0;
            arm_target = 0;
            arm.resetEncoders();

            switch (main_park_id) {
                case 0:
                    drivetrain.autoMove(-50, 4.8, 0, 10, 2.5, 3.5, odometryPose, telemetry);
                    if (drivetrain.hasReached()) {
                        main_park_id += 1;
                    }
                    break;
                case 1:
                    switch (result) {
                        case "FTC8813: 1":
                            switch (park_id) {
                                case 0:
                                    drivetrain.autoMove(-51.1, 27.15, 0, 5, 2.5, 3.5, odometryPose, telemetry);
                                    if (drivetrain.hasReached()) {
                                        park_id += 1;
                                    }
                                    break;
                                case 1:
                                    drivetrain.autoMove(-38.0, 28.6, 0, 1, 1, 1, odometryPose, telemetry);
                                    if (drivetrain.hasReached()) {
                                        park_id += 1;
                                    }
                                    break;

                            }
                            break;
                        case "FTC8813: 3":
                            switch (park_id) {
                                case 0:
                                    drivetrain.autoMove(-48.8, -19.2, 0, 5, 2.5, 3.5, odometryPose, telemetry);
                                    if (drivetrain.hasReached()) {
                                        park_id += 1;
                                        arm_target = 0;
                                        horizontal_target = 0;
                                    }
                                    break;
                                case 1:
                                    drivetrain.autoMove(-37.3, -19.18, 0, 1, 1, 1, odometryPose, telemetry);
                                    if (drivetrain.hasReached()) {
                                        park_id += 1;
                                    }
                                    break;

                            }
                            break;
                        default:
                            switch (park_id) {
                                case 0:
                                    drivetrain.autoMove(-49.4, 4.8, 0, 5, 2.5, 3.5, odometryPose, telemetry);
                                    if (drivetrain.hasReached()) {
                                        park_id += 1;
                                        arm_target = 0;
                                        horizontal_target = 0;
                                    }
                                    break;
                                case 1:
                                    drivetrain.autoMove(-36.95, 4.8, 0, 1, 1, 1, odometryPose, telemetry);
                                    if (drivetrain.hasReached()) {
                                        park_id += 1;
                                    }
                                    break;

                            }
                            break;
                    }
                    break;
            }
        }

        lift_power = Range.clip((lift_PID.getOutPut(lift_target, lift.getCurrentPosition(), 1) * Math.min(lift_trapezoid.seconds() * lift_accel, 1)), -lift_clip, lift_clip); //change
        horizontal_power = horizontal_PID.getOutPut(horizontal_target,horizontal.getCurrentPosition(),0); //change

        arm.setPosition(arm_target);
        lift.setPower(lift_power);
        horizontal.setPower(horizontal_power);

        drivetrain.update(odometryPose, telemetry,motion_profile, main_id, rise, fall, voltage);

        telemetry.addData("Main ID", main_id);
        telemetry.addData("Cone Stack ID", cs_id);
        telemetry.addData("Transfer ID", transfer_id);
//        telemetry.addData("Voltage", getBatteryVoltage());
//        telemetry.addData("Coefficient", voltage_cofficient);
        telemetry.addData("Distance", intake.getDistance());
        telemetry.addData("Time", auto_timer.seconds());
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