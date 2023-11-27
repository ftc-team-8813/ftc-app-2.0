package org.firstinspires.ftc.teamcode.opmodes.auto;

import com.arcrobotics.ftclib.geometry.Pose2d;
import com.arcrobotics.ftclib.geometry.Rotation2d;
import com.arcrobotics.ftclib.kinematics.HolonomicOdometry;

import org.firstinspires.ftc.teamcode.hardware.DistanceSensors;
import org.firstinspires.ftc.teamcode.hardware.Drivetrain;
import org.firstinspires.ftc.teamcode.hardware.Robot;
import org.firstinspires.ftc.teamcode.opmodes.LoggingOpMode;
import org.firstinspires.ftc.teamcode.vision.AprilTagDetectionPipeline;
import org.openftc.easyopencv.OpenCvCamera;


public class ParkingAutoRed extends LoggingOpMode {

    private Drivetrain drivetrain;
    private double forwardSpeed;
    private double turnSpeed;
    private double strafeSpeed;

    private DistanceSensors sensors;
    private OpenCvCamera camera;
    private AprilTagDetectionPipeline aprilTagDetectionPipeline;
    private int pocket = 0;
    HolonomicOdometry odometry;

    boolean atRedTape;


    @Override
    public void init() {
        super.init();
        Robot robot = Robot.initialize(hardwareMap);
        drivetrain = robot.drivetrain;
        odometry = robot.odo;

        odometry.updatePose(new Pose2d(0, 0, new Rotation2d(0)));
    }

    @Override
    public void init_loop() {

        super.init_loop();
        if ((sensors.getLeftDistance()[0] > 600) && (sensors.getLeftDistance()[0] < 800)) {
            pocket = 2;
        } else if ((sensors.getLeftDistance()[1] > 600) && (sensors.getLeftDistance()[1] < 800)) {
            pocket = 3;
        } else {
            pocket = 1;
        }

        telemetry.addData("Detected", pocket);
        telemetry.update();
    }

    @Override
    public void loop() {
        odometry.updatePose();

        if (pocket == 1 && !atRedTape) {
            drivetrain.autoMove(new Pose2d(0, 0, new Rotation2d(0)), odometry.getPose(), strafeSpeed, forwardSpeed, turnSpeed, true);
        }
        if (pocket == 2 && !atRedTape) {
            drivetrain.autoMove(new Pose2d(0, 0, new Rotation2d(0)), odometry.getPose(), strafeSpeed, forwardSpeed, turnSpeed, true);
        }
        if (pocket == 3 && !atRedTape){
            drivetrain.autoMove(new Pose2d(0, 0, new Rotation2d(0)), odometry.getPose(), strafeSpeed, forwardSpeed, turnSpeed, true);
        }

        if(sensors.getRed() < 0 && sensors.getRed() > 0){ //change Values
            atRedTape = true;
        }
    }
}
