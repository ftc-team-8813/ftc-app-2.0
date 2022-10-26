package org.firstinspires.ftc.teamcode.hardware;

import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.hardware.bosch.JustLoggingAccelerationIntegrator;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.hardware.navigation.Odometry;

public class Drivetrain {

    private final DcMotorEx front_left;
    private final DcMotorEx front_right;
    private final DcMotorEx back_left;
    private final DcMotorEx back_right;
    private final BNO055IMU imu;
    private final Odometry odometry; //temp

    private double target_x; //temp
    private double target_y; //temp
    private double target_heading; //temp
    private double target_speed; //temp

    public Drivetrain(DcMotorEx front_left, DcMotorEx front_right, DcMotorEx back_left, DcMotorEx back_right, BNO055IMU imu, Odometry odometry/*temp*/) {
        this.front_left = front_left;
        this.front_right = front_right;
        this.back_left = back_left;
        this.back_right = back_right;
        this.imu = imu;
        this.odometry = odometry;//temp

        BNO055IMU.Parameters parameters = new BNO055IMU.Parameters();
        parameters.angleUnit = BNO055IMU.AngleUnit.DEGREES; // radian maybe?
        parameters.accelUnit = BNO055IMU.AccelUnit.METERS_PERSEC_PERSEC;
        parameters.accelerationIntegrationAlgorithm = new JustLoggingAccelerationIntegrator();
        parameters.gyroRange = BNO055IMU.GyroRange.DPS2000;
        imu.initialize(parameters);

        resetEncoders();

        front_left.setDirection(DcMotorSimple.Direction.REVERSE);
        back_left.setDirection(DcMotorSimple.Direction.REVERSE);

        front_left.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        front_right.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        back_left.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        back_right.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
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

    public void move(double forward, double strafe, double turn, double turn_correct) {
        front_left.setPower((forward + strafe + (turn+turn_correct)));
        front_right.setPower((forward - strafe - (turn+turn_correct)));
        back_left.setPower((forward - strafe + (turn+turn_correct)));
        back_right.setPower((forward + strafe - (turn+turn_correct)));
    }

    public void move(double forward, double strafe, double turn, double turn_correct, double denominator) {


        front_left.setPower(((forward + strafe + (turn+turn_correct)) / denominator));
        front_right.setPower(((forward - strafe - (turn+turn_correct)) / denominator));
        back_left.setPower(((forward - strafe + (turn+turn_correct)) / denominator));
        back_right.setPower(((forward + strafe - (turn+turn_correct)) / denominator));
    }

    public void stop() {
        front_left.setPower(0);
        front_right.setPower(0);
        back_left.setPower(0);
        back_right.setPower(0);
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

    public void goToPosition(double y, double x, double heading, double speed){ //temp
        target_y = y;
        target_x = x;
        target_heading = heading;
        target_speed = speed;
    }

    public void goToHeading(double heading){ //temp
        target_heading = heading;
    } //temp

    public boolean updatePosition(){ //temp
        double[] odoData = odometry.getOdoData();
        double delta_y = target_y + odoData[0];
        double delta_x = target_x - odoData[1];
        double delta_heading = target_heading + odoData[2];

        double forward_power = delta_x * 0.17 * target_speed;
        double strafe_power = delta_y * 0.25 * target_speed;
        double turn_power = delta_heading * 0.01 * target_speed;

        move(forward_power, strafe_power, turn_power,0);
        return delta_y < 1.5 && delta_x < 1.5 && delta_heading < 2;
    }
}
