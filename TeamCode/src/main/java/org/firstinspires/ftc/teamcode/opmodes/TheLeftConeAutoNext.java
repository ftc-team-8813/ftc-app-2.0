package org.firstinspires.ftc.teamcode.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
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
import org.firstinspires.ftc.teamcode.opmodes.util.FTCDVS;
import org.firstinspires.ftc.teamcode.util.Logger;
import org.firstinspires.ftc.teamcode.util.LoopTimer;
import org.firstinspires.ftc.teamcode.vision.AprilTagDetectionPipeline;
import org.openftc.easyopencv.OpenCvCamera;
import org.openftc.easyopencv.OpenCvCameraFactory;
import org.openftc.easyopencv.OpenCvCameraRotation;

@Disabled
@Autonomous(name = "Left Cone Auto Next")
public class TheLeftConeAutoNext extends LoggingOpMode{

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

    private static final double FEET_PER_METER = 3.28084;

    private double fx = 578.272;
    private double fy = 578.272;
    private double cx = 402.145;
    private double cy = 221.506;

    private double tagsize = 0.166;

    private final PID arm_PID = new PID(FTCDVS.getKPArm(), 0, 0, FTCDVS.getKFArm(), 0, 0);
    private final PID horizontal_PID = new PID(FTCDVS.getKPHoriz(), 0, 0, 0, 0, 0);
    private final PID lift_PID = new PID(FTCDVS.getKPLift(), 0, 0, FTCDVS.getKFLift(), 0, 0);

    private ElapsedTime timer = new ElapsedTime();

    private ElapsedTime lift_trapezoid = new ElapsedTime();;
    private double lift_accel = 0.4;

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

    private boolean motion_profile = false;

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
    }

    @Override
    public void init_loop() {
        super.init_loop();

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

        timer_point_1 = LoopTimer.getLoopTime();

//        motion_profile = false;

        switch (main_id) {
            case 0:
                drivetrain.autoMove(-6,-19,0,1,1,1, odometry.getPose(), telemetry);
                if (drivetrain.hasReached()) {
                    main_id += 1;
                    lift.setHolderPosition(0.39);
                }
                break;
            case 1:
                drivetrain.autoMove(-28.7,-20.4,46.34,1,1,1, odometry.getPose(), telemetry);
                if (drivetrain.hasReached()) {
                    main_id += 1;
                }
                break;
            case 2:
                drivetrain.autoMove(-32.7,-24.4,46.34,0.5,0.5,1.5, odometry.getPose(), telemetry);
                if (drivetrain.hasReached()) {
                    main_id += 1;
                    arm_target = -35;
                    lift_target = 0;
                }
                break;
            case 3:
                if (lift.getCurrentPosition() < 200) {
                    lift.setHolderPosition(0.14);
                    main_id += 1;
                }
                break;
            case 4:
                if (lift.getCurrentPosition() < 10) {
                    main_id += 1;
                    arm_target = -10;
                }
                break;
            case 5:
                drivetrain.autoMove(-46,-17.36,85,1,1,1, odometry.getPose(), telemetry);
                if (drivetrain.hasReached()) {
                    main_id += 1;
                    horizontal_target = -800;
                    arm_target = -71;
                    intake.setWristPosition(0.019);
                    intake.setClawPosition(0.3);
                }
                break;
            case 6:
                drivetrain.autoMove(-46,9.5,85,1,1,1, odometry.getPose(), telemetry);
                if (drivetrain.hasReached()) {
                    main_id += 1;
                }
                break;
            case 7:
                if (intake.getDistance() < 17) {
                    intake.setClawPosition(0.1);
                    main_id += 1;
                    timer.reset();
                }
                break;
            case 8:
                if (timer.seconds() > 0.7) {
                    arm_target = -35;
                    main_id += 1;
                    timer.reset();
                }
                break;
            case 9:
                if (timer.seconds() > 0.7) {
                    intake.setWristPosition(0.678);
                    horizontal_target = 0;
                    main_id += 1;
                }
                break;
            case 10:
                drivetrain.autoMove(-46,0,85,1,3,1, odometry.getPose(), telemetry);
                if (drivetrain.hasReached()) {
                    arm_target = 0;
                }
                break;
            case 11:
                drivetrain.autoMove(-46,-7,85,1,1,1, odometry.getPose(), telemetry);
                if (drivetrain.hasReached()) {
                    intake.setClawPosition(0.3);
                }
                break;
            case 12:
                drivetrain.autoMove(-46,-14,85,1,3,1, odometry.getPose(), telemetry);
                if (drivetrain.hasReached()) {
                    arm_target = -35;
                    lift.setHolderPosition(0.39);
                }
                break;
            case 13:
                drivetrain.autoMove(-46,-17.36,85,1,1,1, odometry.getPose(), telemetry);
                if (drivetrain.hasReached()) {
                    lift_target = 745;
                    lift_trapezoid.reset();
                }
                break;
            case 14:
                drivetrain.autoMove(-44,-24.4,135,0.5,0.5,1.5, odometry.getPose(), telemetry);
                if (drivetrain.hasReached()) {
                    lift_target = 0;
                }
                break;
            case 15:
                if (lift.getCurrentPosition() < 200) {
                    lift.setHolderPosition(0.14);
                    main_id += 1;
                }
                break;
            case 16:
                if (lift.getCurrentPosition() < 10) {
                    main_id += 1;
                    arm_target = -10;
                }
                break;
            case 17:
                drivetrain.stop();
                break;
        }

        timer_point_2 = LoopTimer.getLoopTime();

        lift_power = lift_PID.getOutPut(lift_target, lift.getCurrentPosition(), 1) * Math.min(lift_trapezoid.seconds() * lift_accel, 1); //change
        horizontal_power = horizontal_PID.getOutPut(horizontal_target,horizontal.getCurrentPosition(),0); //change
        arm_power = Range.clip(arm_PID.getOutPut(arm_target, arm.getCurrentPosition(), Math.cos(Math.toRadians(arm.getCurrentPosition() + 0))), -0.6, 0.6); //change

        timer_point_3 = LoopTimer.getLoopTime();

        lift.setPower(lift_power);
        horizontal.setPower(horizontal_power);
        arm.setPower(arm_power);

        timer_point_4 = LoopTimer.getLoopTime();

        drivetrain.update(odometry.getPose(), telemetry, false);

        timer_point_5 = LoopTimer.getLoopTime();

        telemetry.addData("Main ID", main_id);
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
        telemetry.addData("Timer Point 1", timer_point_1);
        telemetry.addData("Timer Point 2", timer_point_2);
        telemetry.addData("Timer Point 3", timer_point_3);
        telemetry.addData("Timer Point 4", timer_point_4);
        telemetry.addData("Timer Point 5", timer_point_5);
        telemetry.addData("Loop Time: ", LoopTimer.getLoopTime());
        telemetry.update();

        LoopTimer.resetTimer();
    }

    @Override
    public void stop() {
        super.stop();
    }

}