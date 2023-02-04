//package org.firstinspires.ftc.teamcode.opmodes;
//
//import com.arcrobotics.ftclib.controller.wpilibcontroller.SimpleMotorFeedforward;
//import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
//import com.qualcomm.robotcore.util.ElapsedTime;
//import com.qualcomm.robotcore.util.Range;
//
//import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
//import org.firstinspires.ftc.teamcode.hardware.Arm;
//import org.firstinspires.ftc.teamcode.hardware.Drivetrain;
//import org.firstinspires.ftc.teamcode.hardware.Horizontal;
//import org.firstinspires.ftc.teamcode.hardware.Intake;
//import org.firstinspires.ftc.teamcode.hardware.Lift;
//import org.firstinspires.ftc.teamcode.hardware.Robot;
//import org.firstinspires.ftc.teamcode.hardware.navigation.Odometry;
//import org.firstinspires.ftc.teamcode.hardware.navigation.PID;
//import org.firstinspires.ftc.teamcode.opmodes.LoggingOpMode;
//import org.firstinspires.ftc.teamcode.util.Logger;
//import org.firstinspires.ftc.teamcode.util.LoopTimer;
//import org.firstinspires.ftc.teamcode.vision.AprilTagDetectionPipeline;
//import org.openftc.apriltag.AprilTagDetection;
//import org.openftc.easyopencv.OpenCvCamera;
//import org.openftc.easyopencv.OpenCvCameraFactory;
//import org.openftc.easyopencv.OpenCvCameraRotation;
//
//import java.util.ArrayList;
//
//@Autonomous(name = "FTCLibMotionProfiling Auto")
//public class FTCLibMotionProfiling extends LoggingOpMode {
//
//    private Lift lift;
//    private Horizontal horizontal;
//    private Arm arm;
//    private Intake intake;
//    private Drivetrain drivetrain;
//    private Odometry odometry;
//
//    private String result = "Nothing";
//
//    private int main_id = 0;
//    private int arm_id = 0;
//
//    private OpenCvCamera camera;
//    private AprilTagDetectionPipeline aprilTagDetectionPipeline;
//
//    private static final double FEET_PER_METER = 3.28084;
//
//    private double fx = 578.272;
//    private double fy = 578.272;
//    private double cx = 402.145;
//    private double cy = 221.506;
//
//    private double tagsize = 0.166;
//
//    private final PID arm_PID = new PID(0.009, 0, 0, 0.1, 0, 0);
//    private final PID horizontal_PID = new PID(0.01, 0, 0, 0, 0, 0);
//    private final PID lift_PID = new PID(0.02, 0, 0, 0.015, 0, 0);
//
//    private ElapsedTime timer = new ElapsedTime();
//
//    private ElapsedTime lift_trapezoid = new ElapsedTime();;
//    private double lift_accel = 0.27;
//
//    private double lift_target = 0;
//    private double horizontal_target = 0;
//    private double arm_target = 0;
//
//    private ElapsedTime liftTimer = new ElapsedTime();
//    private boolean liftTimerReset = false;
//
//    private final Logger log = new Logger("Square Auto");
//
//    @Override
//    public void init() {
//        super.init();
//        Robot robot = Robot.initialize(hardwareMap);
//        drivetrain = robot.drivetrain;
//        odometry = robot.odometry;
//
//        odometry.Down();
//
//
////        telemetry.setMsTransmissionInterval(50);
//
//        odometry.resetEncoders();
//    }
//
//    @Override
//    public void init_loop() {
//        super.init_loop();
//    }
//
//
//    @Override
//    public void start() {
//        super.start();
//    }
//
//    @Override
//    public void loop() {
//
//        odometry.updatePose(-drivetrain.getHeading());
//        SimpleMotorFeedforward feedforward = new SimpleMotorFeedforward(kS, kV, kA);
////        switch (main_id) {
////            case 0:
////                drivetrain.autoMove(0,-72,0,1,1,2, odometry.getPose(), telemetry);
////                if (drivetrain.hasReached()) {
////                    main_id += 1;
////                }
////                break;
////            case 1:
////                drivetrain.autoMove(-72,-72,0,1,1,2, odometry.getPose(), telemetry);
////                if (drivetrain.hasReached()) {
////                    main_id += 1;
////                }
////                break;
////            case 2:
////                drivetrain.autoMove(-72,0,0,1,1,2, odometry.getPose(), telemetry);
////                if (drivetrain.hasReached()) {
////                    main_id += 1;
////                }
////                break;
////            case 3:
////                drivetrain.autoMove(0,0,0,1,1,2, odometry.getPose(), telemetry);
////                if (drivetrain.hasReached()) {
////                    main_id += 1;
////                }
////                break;
////        }
//
////        drivetrain.update(odometry.getPose(), telemetry,false);
//
//        telemetry.addData("Main ID", main_id);
//        telemetry.addData("Loop Time: ", LoopTimer.getLoopTime());
//        telemetry.update();
//
//        LoopTimer.resetTimer();
//    }
//
//    @Override
//    public void stop() {
//        super.stop();
//    }
//
//}