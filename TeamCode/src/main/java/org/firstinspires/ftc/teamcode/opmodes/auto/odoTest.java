package org.firstinspires.ftc.teamcode.opmodes.auto;

import com.acmerobotics.dashboard.config.Config;
import com.arcrobotics.ftclib.controller.PIDController;
import com.arcrobotics.ftclib.geometry.Pose2d;
import com.arcrobotics.ftclib.geometry.Rotation2d;
import com.arcrobotics.ftclib.kinematics.HolonomicOdometry;
import com.arcrobotics.ftclib.kinematics.Odometry;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import org.firstinspires.ftc.teamcode.hardware.Deposit;
import org.firstinspires.ftc.teamcode.hardware.DistanceSensors;
import org.firstinspires.ftc.teamcode.hardware.Drivetrain;
import org.firstinspires.ftc.teamcode.hardware.Robot;
import org.firstinspires.ftc.teamcode.opmodes.LoggingOpMode;


@Config
@Autonomous(name = "!!OdoTest!!")
public class odoTest extends LoggingOpMode {

    private HolonomicOdometry odometry;
    private Drivetrain drivetrain;
    private DistanceSensors sensors;
    private Deposit deposit;
    private Pose2d currentPose;
    private Pose2d targetPose;
    private int ID;
//    private final PIDController xCont = new PIDController(0.42,0.1,0.045);
    private final PIDController yCont = new PIDController(0.07,0,0.09);
//    private final PIDController headingCont = new PIDController(0.35,0,0);
    @Override
    public void init() {
        super.init();
        Robot robot = Robot.initialize(hardwareMap);
        deposit = robot.deposit;
        odometry = robot.odometry;
        drivetrain = robot.drivetrain;
        sensors = robot.sensors;
        currentPose = new Pose2d(0, 0, new Rotation2d(0));
        targetPose = new Pose2d(0, 0, new Rotation2d(0));
        odometry.update(0,0,0);
        odometry.updatePose();
        robot.center_odometer.reset();
        robot.right_odometer.reset();
        robot.left_odometer.reset();
//MOTOR.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
    }
    @Override
    public void init_loop() {
        super.init_loop();
    }

    @Override
    public void loop() {
        odometry.updatePose();
        currentPose = odometry.getPose();
        deposit.setDepoPivot(0.680);
        deposit.setDepoLock(0.85);




        telemetry.addData("Current Pose", currentPose);
        telemetry.addData("Target Pose", targetPose);

    }

    public boolean closeToPosition(Pose2d currentPose, Pose2d targetPose, double xDeadband, double yDeadband, double headingDeadband) {
        if ((Math.abs((targetPose.getX() - currentPose.getX())) < xDeadband) && (Math.abs((targetPose.getY() - currentPose.getY())) < yDeadband) && (Math.abs((targetPose.getHeading() - currentPose.getHeading())) < headingDeadband)) {
            return true;
        } else {
            return false;
        }
    }
}
