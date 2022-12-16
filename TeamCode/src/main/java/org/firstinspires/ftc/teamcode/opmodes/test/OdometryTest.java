package org.firstinspires.ftc.teamcode.opmodes.test;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.arcrobotics.ftclib.command.OdometrySubsystem;
import com.arcrobotics.ftclib.drivebase.MecanumDrive;
import com.arcrobotics.ftclib.geometry.Pose2d;
import com.arcrobotics.ftclib.geometry.Rotation2d;
import com.arcrobotics.ftclib.hardware.motors.Motor;
import com.arcrobotics.ftclib.hardware.motors.MotorEx;
import com.arcrobotics.ftclib.kinematics.HolonomicOdometry;
import com.arcrobotics.ftclib.hardware.motors.Motor.Encoder;
import com.arcrobotics.ftclib.kinematics.Odometry;
import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.hardware.bosch.JustLoggingAccelerationIntegrator;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorImplEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

import org.firstinspires.ftc.teamcode.hardware.Drivetrain;
import org.firstinspires.ftc.teamcode.hardware.Robot;
import org.firstinspires.ftc.teamcode.hardware.navigation.PID;
import org.firstinspires.ftc.teamcode.opmodes.LoggingOpMode;


@Autonomous(name="Odometry Test")
public class OdometryTest extends LoggingOpMode {

    public static final double TRACKWIDTH = 12.5925;
    public static final double CENTER_WHEEL_OFFSET = 5.89567;
    public static final double WHEEL_DIAMETER = 1.37795;
    public static final double TICKS_PER_REV = 8192;
    public static final double DISTANCE_PER_PULSE = Math.PI * WHEEL_DIAMETER / TICKS_PER_REV;

    private MotorEx frontLeft, frontRight, backLeft, backRight, lift2;
    private DcMotorEx front_left, front_right, back_left, back_right, lift_2;
    private Encoder leftOdometer, rightOdometer, centerOdometer;
    private HolonomicOdometry odometry;

    @Override
    public void init() {
        super.init();

        frontLeft = new MotorEx(hardwareMap, "front left");
        frontRight = new MotorEx(hardwareMap, "front right");
        backLeft = new MotorEx(hardwareMap, "back left");
        backRight = new MotorEx(hardwareMap, "back right");
        lift2 = new MotorEx(hardwareMap,"lift2");

        front_left = frontLeft.motorEx;
        front_right = frontRight.motorEx;
        back_left = backLeft.motorEx;
        back_right = backRight.motorEx;
        lift_2 = lift2.motorEx;

        leftOdometer = backLeft.encoder.setDistancePerPulse(DISTANCE_PER_PULSE);
        rightOdometer = lift2.encoder.setDistancePerPulse(DISTANCE_PER_PULSE);
        centerOdometer = backRight.encoder.setDistancePerPulse(DISTANCE_PER_PULSE);

        leftOdometer.setDirection(MotorEx.Direction.REVERSE);

        odometry = new HolonomicOdometry(
                leftOdometer::getDistance,
                rightOdometer::getDistance,
                centerOdometer::getDistance,
                TRACKWIDTH, CENTER_WHEEL_OFFSET
        );

        leftOdometer.reset();
        rightOdometer.reset();
        centerOdometer.reset();

        Pose2d start_pose = new Pose2d(0,0,new Rotation2d(Math.toRadians(0)));
        odometry.updatePose(start_pose);

        front_right.setDirection(DcMotorSimple.Direction.REVERSE);
        back_right.setDirection(DcMotorSimple.Direction.REVERSE);

        front_left.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        front_right.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        back_left.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        back_right.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);


    }

    @Override
    public void loop() {
        odometry.updatePose();

        double forward_power = -gamepad1.left_stick_y;
        double strafe_power = gamepad1.left_stick_x;
        double turn_power = gamepad1.right_stick_x;

        front_left.setPower(((forward_power + strafe_power + (turn_power + 0))));
        front_right.setPower(((forward_power - strafe_power - (turn_power + 0))));
        back_left.setPower(((forward_power - strafe_power + (turn_power + 0))));
        back_right.setPower(((forward_power + strafe_power - (turn_power + 0))));

        double rot;
        double act_rot = -1;
        double turn = 10;
        
        if(Math.signum(odometry.getPose().getRotation().getDegrees()) == -1) {
            rot = (odometry.getPose().getRotation().getDegrees() + 360);
        }
        else {
            rot = odometry.getPose().getRotation().getDegrees();
        }

        if (Math.abs(turn - rot) > Math.abs(turn - (rot-360))) {
            act_rot = (rot - 360);
        }

        telemetry.addData("Odometry", odometry.getPose());
        telemetry.addData("X",odometry.getPose().getX());
        telemetry.addData("Y",odometry.getPose().getY());
        telemetry.addData("ACT ROT",act_rot);
        telemetry.addData("ROT",rot);
        telemetry.addData("Center",centerOdometer.getPosition());
        telemetry.addData("Left",leftOdometer.getPosition());
        telemetry.addData("Right",rightOdometer.getPosition());

        telemetry.update();
    }
}
