//package org.firstinspires.ftc.teamcode.opmodes.auto;
//
//import com.arcrobotics.ftclib.geometry.Pose2d;
//import com.arcrobotics.ftclib.geometry.Rotation2d;
//import com.arcrobotics.ftclib.kinematics.HolonomicOdometry;
//import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
//
//import org.firstinspires.ftc.teamcode.hardware.DistanceSensors;
//import org.firstinspires.ftc.teamcode.hardware.Drivetrain;
//import org.firstinspires.ftc.teamcode.hardware.Robot;
//import org.firstinspires.ftc.teamcode.opmodes.LoggingOpMode;
//import org.firstinspires.ftc.teamcode.vision.AprilTagDetectionPipeline;
//import org.openftc.easyopencv.OpenCvCamera;
//
//@Autonomous(name = "!!Parking Auto Left Blue!!")
//public class ParkingAutoLeftBlue extends LoggingOpMode {
//
//    private Drivetrain drivetrain;
//
//
//    @Override
//    public void init() {
//        super.init();
//        Robot robot = Robot.initialize(hardwareMap);
//        drivetrain = robot.drivetrain;
//
//    }
//    @Override
//
//    public void loop() {
//        drivetrain.move(0, 0.4, 0, 0);
//    }
//}
