//package org.firstinspires.ftc.teamcode.opmodes.auto;
//
//import com.acmerobotics.dashboard.FtcDashboard;
//import com.acmerobotics.dashboard.config.Config;
//import com.arcrobotics.ftclib.controller.PIDController;
//import com.arcrobotics.ftclib.controller.PIDFController;
//import com.arcrobotics.ftclib.geometry.Pose2d;
//import com.arcrobotics.ftclib.geometry.Rotation2d;
//import com.arcrobotics.ftclib.kinematics.HolonomicOdometry;
//import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
//import com.qualcomm.robotcore.hardware.Servo;
//import com.qualcomm.robotcore.util.ElapsedTime;
//
//import org.firstinspires.ftc.robotcore.external.Telemetry;
//import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
//import org.firstinspires.ftc.teamcode.R;
//import org.firstinspires.ftc.teamcode.hardware.Camera;
//import org.firstinspires.ftc.teamcode.hardware.Deposit;
//import org.firstinspires.ftc.teamcode.hardware.DistanceSensors;
//import org.firstinspires.ftc.teamcode.hardware.Drivetrain;
//import org.firstinspires.ftc.teamcode.hardware.Horizontal;
//import org.firstinspires.ftc.teamcode.hardware.Intake;
//import org.firstinspires.ftc.teamcode.hardware.Lift;
//import org.firstinspires.ftc.teamcode.hardware.Robot;
//import org.firstinspires.ftc.teamcode.hardware.navigation.ColorPipeline;
//import org.firstinspires.ftc.teamcode.hardware.navigation.PID;
//import org.firstinspires.ftc.teamcode.opmodes.LoggingOpMode;
//import org.firstinspires.ftc.teamcode.vision.AprilTagDetectionPipeline;
//import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;
//import org.openftc.easyopencv.OpenCvCamera;
//import org.openftc.easyopencv.OpenCvCameraFactory;
//import org.openftc.easyopencv.OpenCvCameraRotation;
//
//import java.util.ArrayList;
//
//
//@Config
//@Autonomous(name = "!!Camera Test!!")
//
//public class CameraTest extends LoggingOpMode {
//
//    private Robot robot;
//    private Drivetrain drivetrain;
//    private double kpX = 0.06;
//    private double kiX = 0;
//    private double kdX = 0.02;
//    private double kpY = 0.07;
//    private double kiY = 0;
//    private double kdY = 0.02;
//    private double kpH = 0.8;
//    private double kiH = 0.007;
//    private double kdH = 0.12;
//    private int ID;
//    Camera camera;
//    AprilTagDetection detection;
//    private double[] posFromBack;
//
//    @Override
//    public void init() {
//        super.init();
//        robot = Robot.initialize(hardwareMap);
//        drivetrain = robot.drivetrain;
//        ID = 5;
//        camera = new Camera(hardwareMap.get(WebcamName.class, "Webcam 1"));
//    }
//
//
//
//    @Override
//    public void loop() {
//        detection = camera.getDetection(ID);
//
//        if(!(detection == null)){
//            moveTowardsID(detection, drivetrain, telemetry);
//        }else{
//            drivetrain.move(0,0,0,0);
//        }
//
//        telemetry.addData("Detection", detection);
//
////        if(posFromBack.length == 3){
////            drivetrain.move(posFromBack[0], posFromBack[1], posFromBack[2], 0);
////        }
//    }
//
//    public double[] moveTowardsID(AprilTagDetection detection, Drivetrain drivetrain, Telemetry telemetry) {
//        if (detection != null) {
//            PIDController xCont = new PIDController(kpX, kiX, kdX);
//            PIDController yCont = new PIDController(kpY, kpY, kdY);
//            PIDController zCont = new PIDController(kpH, kiH, kdH);
//
//            double xPower = clipValue(-0.33, 0.33, xCont.calculate(0, detection.ftcPose.x));
//            double yPower = clipValue(-0.33, 0.33, yCont.calculate(0, (detection.ftcPose.y + 2)));
//            double zPower = clipValue(-0.033, 0.033, zCont.calculate(0, (detection.ftcPose.z)));
//
//            telemetry.addData("x Power", xPower);
//            telemetry.addData("y Power", yPower);
//            telemetry.addData("z Power", zPower);
//            telemetry.addData("x Distance", detection.ftcPose.x);
//            telemetry.addData("y Distance", detection.ftcPose.y);
//            telemetry.addData("z Distance", detection.ftcPose.z);
//
//            drivetrain.move(-yPower, -xPower, -zPower, 0);
//
//            return new double[]{xPower, yPower, -zPower};
//        }else{
//            return new double[]{0};
//        }
//    }
//    public double clipValue(double min, double max, double value) {
//        return Math.min(Math.max(value, min), max);
//    }
//}