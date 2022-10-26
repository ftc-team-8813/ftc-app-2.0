package org.firstinspires.ftc.teamcode.hardware;

import com.arcrobotics.ftclib.command.OdometrySubsystem;
import com.arcrobotics.ftclib.geometry.Pose2d;
import com.arcrobotics.ftclib.geometry.Rotation2d;
import com.arcrobotics.ftclib.hardware.motors.Motor;
import com.arcrobotics.ftclib.hardware.motors.MotorEx;
import com.arcrobotics.ftclib.kinematics.HolonomicOdometry;
import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

import org.firstinspires.ftc.robotcore.external.Telemetry;

public class Drivetrain {

    public MotorEx front_left;
    public MotorEx front_right;
    public MotorEx back_left;
    public MotorEx back_right;

    public DcMotorEx front_left_dc;
    public DcMotorEx front_right_dc;
    public DcMotorEx back_left_dc;
    public DcMotorEx back_right_dc;


    public MotorEx.Encoder leftOdometer;
    public MotorEx.Encoder rightOdometer;
    public MotorEx.Encoder centerOdometer;

    public static final double TRACKWIDTH = 10.07570866;
    public static final double CENTER_WHEEL_OFFSET = 5.027;
    public static final double WHEEL_DIAMETER = 1.37795;
    public static final double TICKS_PER_REV = 8192;
    public static final double DISTANCE_PER_PULSE = Math.PI * WHEEL_DIAMETER / TICKS_PER_REV;

    private HolonomicOdometry odometry;
//    private final BNO055IMU imu;

    public Drivetrain(MotorEx front_left, MotorEx front_right, MotorEx back_left, MotorEx back_right) {
        this.front_left = front_left;
        this.front_right = front_right;
        this.back_left = back_left;
        this.back_right = back_right;

        front_left.motorEx.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        back_left.motorEx.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        back_right.motorEx.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        front_right.motorEx.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        front_left.motorEx.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        back_left.motorEx.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        back_right.motorEx.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        front_right.motorEx.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
    }

//    public Drivetrain(DcMotorEx front_left, DcMotorEx front_right, DcMotorEx back_left, DcMotorEx back_right){
//        this.front_left_dc = front_left;
//        this.front_right_dc = front_right;
//        this.back_left_dc = back_left;
//        this.back_right_dc = back_right;
//
//    }

    public void DrivetrainInit(){
        front_left_dc.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        back_left_dc.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        back_right_dc.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        front_left_dc.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        back_left_dc.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        back_right_dc.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        //front left - left
        //front right - right
        //back left - center
    }

    public void makeOdometry(Telemetry telemetry){
        leftOdometer = front_left.encoder.setDistancePerPulse(DISTANCE_PER_PULSE);
        rightOdometer = back_right.encoder.setDistancePerPulse(DISTANCE_PER_PULSE);
        centerOdometer = back_left.encoder.setDistancePerPulse(DISTANCE_PER_PULSE);

        odometry = new HolonomicOdometry(
            leftOdometer::getDistance,
            rightOdometer::getDistance,
            centerOdometer::getDistance,
            TRACKWIDTH, CENTER_WHEEL_OFFSET
        );
    }

    public void getMotorEncoderPositions(Telemetry telemetry){
        telemetry.addData("Left Odometer: Front Left", front_left_dc.getCurrentPosition());
        telemetry.addData("Right Odometer: Back Right", back_right_dc.getCurrentPosition());
        telemetry.addData("Center Odometer: Back Left", back_left_dc.getCurrentPosition());
    }

    public void updateOdometry(Telemetry telemetry){
        odometry.update(leftOdometer.getPosition(), rightOdometer.getPosition(), centerOdometer.getPosition());
    }

    public Pose2d getOdometry(){
        return odometry.getPose();
    }

    public void setOriginalPoseForOdometry(){
        odometry.updatePose(new Pose2d(0, 0, new Rotation2d(0)));
    }


//    public void move() {
////        front_left.setPower();
////        front_right.setPower();
////        back_left.setPower();
////        back_right.setPower();
//    }

//    public void update() {
//
//    }

//    public void stop() {
//        front_left.setPower(0);
//        front_right.setPower(0);
//        back_left.setPower(0);
//        back_right.setPower(0);
//    }

//    public double getForwardPosition(){
//        return (front_left.getCurrentPosition() + front_right.getCurrentPosition() + back_left.getCurrentPosition() + back_right.getCurrentPosition()) / 4.0;
//    }

//    public double getHeading(){
//        return imu.getAngularOrientation().firstAngle;
//    }

//    public double getAngularVelocity(){
//        return imu.getAngularVelocity().xRotationRate;
//    }

//    public void closeIMU() {
//        imu.close();
//    }
//    public void move(double forward, double strafe, double turn, double turn_correct) {
//        front_left.setPower((forward + strafe + (turn+turn_correct)));
//        front_right.setPower((forward - strafe - (turn+turn_correct)));
//        back_left.setPower((forward - strafe + (turn+turn_correct)));
//        back_right.setPower((forward + strafe - (turn+turn_correct)));
//    }


}
