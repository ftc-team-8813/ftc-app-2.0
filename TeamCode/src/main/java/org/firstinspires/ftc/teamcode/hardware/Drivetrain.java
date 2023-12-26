package org.firstinspires.ftc.teamcode.hardware;


import com.arcrobotics.ftclib.controller.PIDController;
import com.arcrobotics.ftclib.drivebase.MecanumDrive;
import com.arcrobotics.ftclib.geometry.Pose2d;
import com.arcrobotics.ftclib.hardware.motors.Motor;
import com.arcrobotics.ftclib.hardware.motors.MotorEx;
import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;

public class Drivetrain{
    private MotorEx front_left;
    private MotorEx front_right;
    private MotorEx back_left;
    private MotorEx back_right;
    private MecanumDrive drive;
    private BNO055IMU imu_sensor;
    public Drivetrain(MotorEx front_left, MotorEx front_right, MotorEx back_left, MotorEx back_right){
        this.front_left = front_left;
        this.front_right = front_right;
        this.back_left = back_left;
        this.back_right = back_right;
//        this.imu_sensor = imu_sensor;
        drive = new MecanumDrive(front_left, front_right, back_left, back_right);
    }

    public void moveRobotCentric(double strafeSpeed, double forwardSpeed, double turnSpeed){
        drive.driveRobotCentric(strafeSpeed, forwardSpeed, turnSpeed);
    }

    public void moveFieldCentric(double strafeSpeed, double forwardSpeed, double turnSpeed){
        drive.driveFieldCentric(strafeSpeed, forwardSpeed, turnSpeed, imu_sensor.getAngularOrientation().firstAngle); //might need to look at imu
    }

    public void autoMove(Pose2d targetPose, Pose2d currentPose, PIDController xCont, PIDController yCont, PIDController headingCont, Telemetry telemetry) {
        double xPower = xCont.calculate(currentPose.getX(), targetPose.getX());
        double yPower = yCont.calculate(currentPose.getY(), targetPose.getY());
        double headingPower = headingCont.calculate(currentPose.getHeading(), targetPose.getHeading());

        drive.driveRobotCentric(-yPower, -xPower, headingPower);

        telemetry.addData("x Power", xPower);
        telemetry.addData("y Power", yPower);
        telemetry.addData("heading Power", headingPower);

    }







}
