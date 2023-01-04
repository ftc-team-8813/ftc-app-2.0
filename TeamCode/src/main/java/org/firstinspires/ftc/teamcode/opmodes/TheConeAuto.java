package org.firstinspires.ftc.teamcode.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
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

@Autonomous(name = "!!Cone Auto!!")
public class TheConeAuto extends LoggingOpMode{

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

        telemetry.setMsTransmissionInterval(50);

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
        lift_target = 740;
        lift_trapezoid.reset();
    }

    @Override
    public void loop() {

        odometry.updatePose();

        switch (main_id) {
            case 0:
                drivetrain.autoMove(-24,0,0,1,1,1, odometry.getPose(), telemetry);
                if (drivetrain.hasReached()) {
                    main_id += 1;
                }
                break;
            case 1:
                drivetrain.autoMove(-24,0,90,1,1,1, odometry.getPose(), telemetry);
                if (drivetrain.hasReached()) {
                    main_id += 1;
                }
                break;
            case 2:
                drivetrain.autoMove(-24,-25,90,1,1,1, odometry.getPose(), telemetry);
                if (drivetrain.hasReached()) {
                    main_id += 1;
                }
                break;
            case 3:
                drivetrain.autoMove(-38,-25,90,0.7,0.7,1, odometry.getPose(), telemetry);
                if (drivetrain.hasReached()) {
                    main_id += 1;
                    lift_target = 0;
                }
                break;
            case 4:
                if (lift.getCurrentPosition() < 10) {
                    main_id += 1;
                }
                break;
            case 5:
                drivetrain.autoMove(-48,-25,90,1,1,1, odometry.getPose(), telemetry);
                if (drivetrain.hasReached()) {
                    main_id += 1;
                }
                break;
            case 6:
                drivetrain.autoMove(-48,15,90,1,1,1, odometry.getPose(), telemetry);
                if(drivetrain.hasReached()) {
                    main_id += 1;
                    timer.reset();
                }
                break;
            case 7:
                if (timer.seconds() > 3) {
                    main_id += 1;
                }
                break;
            case 8:
                drivetrain.autoMove(-48,-25,90,1,1,1, odometry.getPose(), telemetry);
                if (drivetrain.hasReached()){
                    main_id += 1;
                }
                break;
            case 9:
                drivetrain.autoMove(-38,-25,90,1,1,1, odometry.getPose(), telemetry);
                if(drivetrain.hasReached()) {
                    main_id += 1;
                    timer.reset();
                }
                break;
            case 10:
                if (timer.seconds() > 3) {
                    main_id += 1;
                }
                break;
            case 11:
                drivetrain.stop();
                break;
        }

        double lift_power = lift_PID.getOutPut(lift_target, lift.getCurrentPosition(), 1) * Math.min(lift_trapezoid.seconds() * lift_accel, 1); //change
        double horizontal_power = horizontal_PID.getOutPut(horizontal_target,horizontal.getCurrentPosition(),0); //change
        double arm_power = Range.clip(arm_PID.getOutPut(arm_target, arm.getCurrentPosition(), Math.cos(Math.toRadians(arm.getCurrentPosition() + 0))), -0.6, 0.6); //change


//        lift.setPower(lift_power);
//        horizontal.setPower(horizontal_power);
//        arm.setPower(arm_power);

        drivetrain.update(odometry.getPose(), telemetry);

        telemetry.addData("Main ID", main_id);
        telemetry.addData("Lift Power", lift_power);
        telemetry.addData("Horizontal Power", horizontal_power);
        telemetry.addData("Arm Power", arm_power);
        telemetry.addData("Lift Target",lift_target);
        telemetry.addData("Horizontal Target",horizontal_target);
        telemetry.addData("Arm Target",arm_target);
        telemetry.addData("Lift Position",lift.getCurrentPosition());
        telemetry.addData("Horizontal Position",horizontal.getCurrentPosition());
        telemetry.addData("Arm Position",arm.getCurrentPosition());
        telemetry.addData("Loop Time: ", LoopTimer.getLoopTime());
        telemetry.update();

        LoopTimer.resetTimer();
    }

    @Override
    public void stop() {
        super.stop();
    }

}