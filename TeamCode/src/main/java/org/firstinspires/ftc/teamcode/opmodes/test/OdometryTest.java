package org.firstinspires.ftc.teamcode.opmodes.test;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.arcrobotics.ftclib.command.OdometrySubsystem;
import com.arcrobotics.ftclib.drivebase.MecanumDrive;
import com.arcrobotics.ftclib.geometry.Pose2d;
import com.arcrobotics.ftclib.geometry.Rotation2d;
import com.arcrobotics.ftclib.hardware.motors.Motor;
import com.arcrobotics.ftclib.hardware.motors.MotorEx;
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
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.hardware.Drivetrain;
import org.firstinspires.ftc.teamcode.hardware.Robot;
import org.firstinspires.ftc.teamcode.hardware.navigation.HolonomicIMUOdometry;
import org.firstinspires.ftc.teamcode.hardware.navigation.PID;
import org.firstinspires.ftc.teamcode.opmodes.LoggingOpMode;

//@Disabled
@Config
@Autonomous(name="Odometry Test")
public class OdometryTest extends LoggingOpMode {

    public static double TRACKWIDTH = 9.00;
    public final double CENTER_WHEEL_OFFSET = -6.089;
    public final double WHEEL_DIAMETER = 1.37795;
    public final double TICKS_PER_REV = 8192;
    public final double DISTANCE_PER_PULSE = Math.PI * WHEEL_DIAMETER / TICKS_PER_REV;

    private int main_id = 0;

    private MotorEx frontLeft, frontRight, backLeft, backRight;
    private DcMotorEx front_left, front_right, back_left, back_right;
    private Encoder leftOdometer, rightOdometer, centerOdometer;
    private HolonomicIMUOdometry odometry;
    private BNO055IMU imu;

    private boolean has_reached;

    public static double x = -30.5;
    public static double y = -62.0;
    public static double SIDE_LENGTH = 14;
    public static double SIDE_WIDTH = 12;

    private FtcDashboard dashboard;

    private static void rotatePoints(double[] xPoints, double[] yPoints, double angle, double x_cor, double y_cor) {
        for (int i = 0; i < xPoints.length; i++) {
            double x = xPoints[i];
            double y = yPoints[i];
            xPoints[i] = x * Math.cos(angle) - y * Math.sin(angle);
            yPoints[i] = x * Math.sin(angle) + y * Math.cos(angle);
            x = xPoints[i];
            y = yPoints[i];
            xPoints[i] = x + x_cor;
            yPoints[i] = y + y_cor;
        }
    }

    @Override
    public void init() {
        super.init();
        Robot robot = Robot.initialize(hardwareMap);
        imu = hardwareMap.get(BNO055IMU.class, "imu");

        BNO055IMU.Parameters parameters = new BNO055IMU.Parameters();
        parameters.angleUnit = BNO055IMU.AngleUnit.DEGREES;
        parameters.accelUnit = BNO055IMU.AccelUnit.METERS_PERSEC_PERSEC;
        parameters.accelerationIntegrationAlgorithm = new JustLoggingAccelerationIntegrator();
        parameters.gyroRange = BNO055IMU.GyroRange.DPS2000;
        imu.initialize(parameters);

        frontLeft = new MotorEx(hardwareMap, "front left");
        frontRight = new MotorEx(hardwareMap, "front right");
        backLeft = new MotorEx(hardwareMap, "back left");
        backRight = new MotorEx(hardwareMap, "back right");

        front_left = frontLeft.motorEx;
        front_right = frontRight.motorEx;
        back_left = backLeft.motorEx;
        back_right = backRight.motorEx;

        leftOdometer = backLeft.encoder.setDistancePerPulse(DISTANCE_PER_PULSE);
        rightOdometer = frontLeft.encoder.setDistancePerPulse(DISTANCE_PER_PULSE);
        centerOdometer = backRight.encoder.setDistancePerPulse(DISTANCE_PER_PULSE);

        leftOdometer.setDirection(MotorEx.Direction.REVERSE);

        odometry = new HolonomicIMUOdometry(
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

        telemetry = new MultipleTelemetry(telemetry, FtcDashboard.getInstance().getTelemetry());

        dashboard = FtcDashboard.getInstance();
    }

    @Override
    public void init_loop() {
        super.init_loop();
    }

    @Override
    public void loop() {
        odometry.updatePose(-imu.getAngularOrientation().firstAngle);

        double forward_power = gamepad1.left_stick_y;
        double strafe_power = -gamepad1.left_stick_x;
        double turn_power = gamepad1.right_stick_x;

        front_left.setPower(((forward_power + strafe_power + (turn_power + 0))));
        front_right.setPower(((forward_power - strafe_power - (turn_power + 0))));
        back_left.setPower(((forward_power - strafe_power + (turn_power + 0))));
        back_right.setPower(((forward_power + strafe_power - (turn_power + 0))));

        x = -30.5 - odometry.getPose().getY();
        y = -62 - odometry.getPose().getX();

        double sL = SIDE_LENGTH / 2;
        double sW = SIDE_WIDTH / 2;

        double[] bxPoints = { sW, -sW, -sW, sW };
        double[] byPoints = { sL, sL, -sL, -sL };

        double rot;

        if(Math.signum(-imu.getAngularOrientation().firstAngle) == -1) {
            rot = ((-imu.getAngularOrientation().firstAngle) + 360);
        }
        else {
            rot = -imu.getAngularOrientation().firstAngle;
        }

        rot %= 360;

        double odometry_rotation;

        if(Math.signum(odometry.getPose().getRotation().getDegrees()) == -1) {
            odometry_rotation = ((odometry.getPose().getRotation().getDegrees()) + 360);
        }
        else {
            odometry_rotation = odometry.getPose().getRotation().getDegrees();
        }

        odometry_rotation %= 360;

        rotatePoints(bxPoints, byPoints,-Math.toRadians(rot),x,y);

        TelemetryPacket packet = new TelemetryPacket();
        packet.fieldOverlay()
                .setStrokeWidth(1)
                .setFill("black")
                .fillPolygon(bxPoints, byPoints);
        dashboard.sendTelemetryPacket(packet);

        telemetry.addData("Odometry", odometry.getPose());
        telemetry.addData("X",odometry.getPose().getY());
        telemetry.addData("Y",odometry.getPose().getX());
        telemetry.addData("Rotation",odometry_rotation);
        telemetry.addData("ID",main_id);
        telemetry.addData("Center",centerOdometer.getPosition());
        telemetry.addData("Left",leftOdometer.getPosition());
        telemetry.addData("Right",rightOdometer.getPosition());
        telemetry.addData("imu", rot);
        telemetry.addData("Trackwidth", TRACKWIDTH);

        telemetry.update();
    }
}
