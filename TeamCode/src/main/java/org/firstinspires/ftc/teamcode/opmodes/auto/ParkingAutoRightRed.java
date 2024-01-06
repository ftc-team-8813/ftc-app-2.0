package org.firstinspires.ftc.teamcode.opmodes.auto;

import com.arcrobotics.ftclib.geometry.Pose2d;
import com.arcrobotics.ftclib.geometry.Rotation2d;
import com.arcrobotics.ftclib.kinematics.HolonomicOdometry;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import org.firstinspires.ftc.teamcode.hardware.DistanceSensors;
import org.firstinspires.ftc.teamcode.hardware.Drivetrain;
import org.firstinspires.ftc.teamcode.hardware.Robot;
import org.firstinspires.ftc.teamcode.opmodes.LoggingOpMode;
import org.firstinspires.ftc.teamcode.vision.AprilTagDetectionPipeline;
import org.opencv.core.Mat;
import org.openftc.easyopencv.OpenCvCamera;

@Autonomous(name = "!!Parking Auto Right Red!!")
public class ParkingAutoRightRed extends LoggingOpMode {

    private Drivetrain drivetrain;
    private double forwardSpeed;
    private double turnSpeed;
    private double strafeSpeed;

    private DistanceSensors sensors;
    private OpenCvCamera camera;
    private AprilTagDetectionPipeline aprilTagDetectionPipeline;
    private int pocket = 0;
    public HolonomicOdometry odometry;
    private boolean parked;
    private boolean goingForward;


    boolean atRedTape;


    @Override
    public void init() {
        super.init();
        Robot robot = Robot.initialize(hardwareMap);
        drivetrain = robot.drivetrain;
        odometry = robot.odo;
        sensors = robot.sensors;
//        placed = false;
        parked = false;
//        detected = false;
        goingForward = true;

        odometry.updatePose(new Pose2d(0, 0, new Rotation2d(0, 0)));
    }

    @Override
    public void loop() {

        odometry.updatePose();

        if((Math.abs(odometry.getPose().getY() - (-25)) > 55) && (Math.abs(odometry.getPose().getY() - (-25)) < 65)){
            parked = true;
        }

        if(!parked & goingForward){
            drivetrain.moveRobotCentric(-0.4, 0, 0);
        }

        if(parked){
            drivetrain.moveRobotCentric(0,0,0);
        }

        telemetry.addData("Odo Pose", odometry.getPose());
        telemetry.addData("Parked", parked);
        telemetry.addData("Value", Math.abs(odometry.getPose().getY() - (-25)));
    }
}
