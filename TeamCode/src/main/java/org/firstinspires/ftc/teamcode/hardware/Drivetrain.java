package org.firstinspires.ftc.teamcode.hardware;

import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.hardware.bosch.JustLoggingAccelerationIntegrator;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

import org.firstinspires.ftc.teamcode.util.Storage;

public class Drivetrain {
    private final DcMotorEx front_left;
    private final DcMotorEx front_right;
    private final DcMotorEx back_left;
    private final DcMotorEx back_right;
    private final BNO055IMU imu;

    private double FORWARD_KP;
    private double TURN_KP;
    private double target_forward = 0;
    private double target_strafe;
    private double target_heading = 0;
    private double target_power;
    private boolean driving;

    public Drivetrain(DcMotorEx front_left, DcMotorEx front_right, DcMotorEx back_left, DcMotorEx back_right, BNO055IMU imu) {
        this.front_left = front_left;
        this.front_right = front_right;
        this.back_left = back_left;
        this.back_right = back_right;
        this.imu = imu;

        FORWARD_KP = Storage.getJsonValue("forward_kp");
        TURN_KP = Storage.getJsonValue("turn_kp");

        BNO055IMU.Parameters parameters = new BNO055IMU.Parameters();
        parameters.angleUnit = BNO055IMU.AngleUnit.DEGREES;
        parameters.accelUnit = BNO055IMU.AccelUnit.METERS_PERSEC_PERSEC;
        parameters.accelerationIntegrationAlgorithm = new JustLoggingAccelerationIntegrator();
        parameters.gyroRange = BNO055IMU.GyroRange.DPS2000;
        imu.initialize(parameters);

        resetEncoders();

        front_right.setDirection(DcMotorSimple.Direction.REVERSE);
        back_right.setDirection(DcMotorSimple.Direction.REVERSE);

        front_left.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        front_right.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        back_left.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        back_right.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
    }

    public void move(double forward, double strafe, double turn, double turn_correct) {
        front_left.setPower((forward + strafe + turn));
        front_right.setPower((forward - strafe - turn) * turn_correct);
        back_left.setPower((forward - strafe + turn));
        back_right.setPower((forward + strafe - turn) * turn_correct);
    }

    public void autoMove(double distance, double power){
        target_forward = distance;
        target_power = power;
        driving = true;
    }

    public void changeHeading(double heading, double power){
        target_heading = heading;
    }

    public boolean ifReached(){
        double min = target_forward - 100;
        double max = target_forward + 100;
        if (min < getDistance() && getDistance() < max && driving){
            driving = false;
            return true;
        }
        return false;
    }

    public void update(){
        double forward_error = target_forward - getDistance();
        double turn_error = target_heading - getHeading();

        double forward_power = forward_error * FORWARD_KP * target_power;
        double turn_power = -turn_error * TURN_KP;

        move(forward_power, 0, turn_power, 1);
    }

    public void stop() {
        front_left.setPower(0);
        front_right.setPower(0);
        back_left.setPower(0);
        back_right.setPower(0);
    }

    public void resetEncoders(){
        front_right.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        back_right.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        front_left.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        back_left.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        front_right.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        back_right.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        front_left.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        back_left.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
    }

    public double getDistance(){
        return (front_left.getCurrentPosition() + front_right.getCurrentPosition() + back_left.getCurrentPosition() + back_right.getCurrentPosition()) / 4.0;
    }

    public double getTargetDistance(){
        return target_forward;
    }

    public double getHeading(){
        return imu.getAngularOrientation().firstAngle;
    }

    public double getAngularVelocity(){
        return imu.getAngularVelocity().xRotationRate;
    }

    public void closeIMU(){
        imu.close();
    }
}