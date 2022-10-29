package org.firstinspires.ftc.teamcode.hardware;

import com.arcrobotics.ftclib.geometry.Pose2d;
import com.arcrobotics.ftclib.geometry.Rotation2d;
import com.arcrobotics.ftclib.hardware.motors.MotorEx;
import com.arcrobotics.ftclib.kinematics.HolonomicOdometry;
import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.hardware.bosch.JustLoggingAccelerationIntegrator;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.hardware.navigation.Odometry;

public class Drivetrain {

    private final MotorEx front_left;
    private final MotorEx front_right;
    private final MotorEx back_left;
    private final MotorEx back_right;
    private final BNO055IMU imu;

    private HolonomicOdometry odometry;

    private MotorEx.Encoder left_odometer;
    private MotorEx.Encoder right_odometer;
    private MotorEx.Encoder center_odometer;

    private final double TRACKWIDTH = 10.07570866;
    private final double CENTER_WHEEL_OFFSET = 5.027;
    private final double WHEEL_DIAMETER = 1.37795;
    private final double TICKS_PER_REV = 8192;
    private final double DISTANCE_PER_PULSE = Math.PI * WHEEL_DIAMETER / TICKS_PER_REV;

    public Drivetrain(MotorEx front_left, MotorEx front_right, MotorEx back_left, MotorEx back_right, BNO055IMU imu) {
        this.front_left = front_left;
        this.front_right = front_right;
        this.back_left = back_left;
        this.back_right = back_right;
        this.imu = imu;

        BNO055IMU.Parameters parameters = new BNO055IMU.Parameters();
        parameters.angleUnit = BNO055IMU.AngleUnit.DEGREES;
        parameters.accelUnit = BNO055IMU.AccelUnit.METERS_PERSEC_PERSEC;
        parameters.accelerationIntegrationAlgorithm = new JustLoggingAccelerationIntegrator();
        parameters.gyroRange = BNO055IMU.GyroRange.DPS2000;
        imu.initialize(parameters);

        resetEncoders();

        front_left.motorEx.setDirection(DcMotorSimple.Direction.REVERSE);
        back_left.motorEx.setDirection(DcMotorSimple.Direction.REVERSE);

        front_left.motorEx.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        front_right.motorEx.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        back_left.motorEx.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        back_right.motorEx.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
    }

    public void resetEncoders(){
        front_right.motorEx.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        back_right.motorEx.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        front_left.motorEx.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        back_left.motorEx.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        front_right.motorEx.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        back_right.motorEx.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        front_left.motorEx.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        back_left.motorEx.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
    }

    public void move(double forward, double strafe, double turn, double turn_correct) {
        front_left.motorEx.setPower((forward + strafe + (turn+turn_correct)));
        front_right.motorEx.setPower((forward - strafe - (turn+turn_correct)));
        back_left.motorEx.setPower((forward - strafe + (turn+turn_correct)));
        back_right.motorEx.setPower((forward + strafe - (turn+turn_correct)));
    }

    public void move(double forward, double strafe, double turn, double turn_correct, double denominator) {
        front_left.motorEx.setPower(((forward + strafe + (turn+turn_correct)) / denominator));
        front_right.motorEx.setPower(((forward - strafe - (turn+turn_correct)) / denominator));
        back_left.motorEx.setPower(((forward - strafe + (turn+turn_correct)) / denominator));
        back_right.motorEx.setPower(((forward + strafe - (turn+turn_correct)) / denominator));
    }

    public void stop() {
        front_left.motorEx.setPower(0);
        front_right.motorEx.setPower(0);
        back_left.motorEx.setPower(0);
        back_right.motorEx.setPower(0);
    }

    public double getForwardPosition(){
        return (front_left.getCurrentPosition() + front_right.getCurrentPosition() + back_left.getCurrentPosition() + back_right.getCurrentPosition()) / 4.0;
    }

    public double getStrafePosition(){
        return (front_left.getCurrentPosition() - front_right.getCurrentPosition() - back_left.getCurrentPosition() + back_right.getCurrentPosition()) / 4.0;
    }

    public double getHeading(){
        return imu.getAngularOrientation().firstAngle;
    }

    public double getAngularVelocity(){
        return imu.getAngularVelocity().xRotationRate;
    }

    public void closeIMU() {
        imu.close();
    }

    public void setOdometry(){
        left_odometer = front_left.encoder.setDistancePerPulse(DISTANCE_PER_PULSE);
        right_odometer = back_right.encoder.setDistancePerPulse(DISTANCE_PER_PULSE);
        center_odometer = back_left.encoder.setDistancePerPulse(DISTANCE_PER_PULSE);

        odometry = new HolonomicOdometry(
                left_odometer::getDistance,
                right_odometer::getDistance,
                center_odometer::getDistance,
                TRACKWIDTH, CENTER_WHEEL_OFFSET
        );
    }

    public void updateOdometry(){
        odometry.update(left_odometer.getPosition(), right_odometer.getPosition(), center_odometer.getPosition());
    }

    public void setOriginalPoseForOdometry() {
        odometry.updatePose(new Pose2d(0, 0, new Rotation2d(0)));
    }

    public Pose2d getOdometry(){
        return odometry.getPose();
    }
}
