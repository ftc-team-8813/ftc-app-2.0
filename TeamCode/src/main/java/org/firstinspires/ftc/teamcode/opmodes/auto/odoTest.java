package org.firstinspires.ftc.teamcode.opmodes.auto;

import com.acmerobotics.dashboard.config.Config;
import com.arcrobotics.ftclib.controller.PIDController;
import com.arcrobotics.ftclib.controller.PIDFController;
import com.arcrobotics.ftclib.geometry.Pose2d;
import com.arcrobotics.ftclib.geometry.Rotation2d;
import com.arcrobotics.ftclib.kinematics.HolonomicOdometry;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.hardware.DistanceSensors;
import org.firstinspires.ftc.teamcode.hardware.Drivetrain;
import org.firstinspires.ftc.teamcode.hardware.Robot;
import org.firstinspires.ftc.teamcode.opmodes.LoggingOpMode;
import org.firstinspires.ftc.teamcode.vision.AprilTagDetectionPipeline;
import org.openftc.easyopencv.OpenCvCamera;

@Config
@Autonomous(name = "!!OdoTest!!")

public class odoTest extends LoggingOpMode {

    private Drivetrain drivetrain;
    private DistanceSensors sensors;
    private AprilTagDetectionPipeline aprilTagDetectionPipeline;
    private int pocket = 0;
    public HolonomicOdometry odometry;
    private ElapsedTime timer;
    private Pose2d currentPose;
    public static PIDController xCont = new PIDController(0.5, 0, 0);
    public static PIDController yCont = new PIDController(0.06, 0, 0);
//    public static PIDController headingCont = new PIDController(0.007, 0.12, 0.0028);
    public static PIDController headingCont = new PIDController(0, 0, 0);


    private boolean reachedPos1;
    @Override
    public void init() {
        super.init();
        Robot robot = Robot.initialize(hardwareMap);
        drivetrain = robot.drivetrain;
        odometry = robot.odo;
        sensors = robot.sensors;
        odometry.updatePose(new Pose2d(0, 0, new Rotation2d(0, 0)));
        timer = new ElapsedTime(ElapsedTime.Resolution.MILLISECONDS);
        currentPose = odometry.getPose();
        reachedPos1 = false;
    }
    @Override
    public void init_loop() {
        super.init_loop();
        if(sensors.getRightDistance() > 700 && sensors.getRightDistance() < 500) {
            pocket = 2;
        }
//        }else{
//            pocket = 1;
//        }

        telemetry.addData("Pocket", pocket);

    }

    @Override

    public void loop() {
        timer.reset();
        odometry.updatePose();
        currentPose = odometry.getPose();

        if(pocket == 3){
            drivetrain.autoMove(new Pose2d(18, 4, new Rotation2d(0)), currentPose, xCont, yCont, headingCont, telemetry);
        }
        if (pocket == 2) {
            drivetrain.autoMove(new Pose2d(28, 0, new Rotation2d(0)), currentPose, xCont, yCont, headingCont, telemetry);
        }

//        if(pocket == 3){
//            if(currentPose.getX() < 27 && currentPose.getX() > 25){
//                reachedPos1 = true;
//            }
//            if(reachedPos1){
//                drivetrain.autoMove(new Pose2d(26, -17, new Rotation2d(0)), currentPose, xCont, yCont, headingCont, telemetry); //Left Pos
//            }else{
//                drivetrain.autoMove(new Pose2d(26, 0, new Rotation2d(0)), currentPose, xCont, yCont, headingCont, telemetry); //Left Pos
//            }
//        }


        telemetry.addData("Odo Pose", currentPose);
        telemetry.addData("Loop Time", timer.time());

    }
}
