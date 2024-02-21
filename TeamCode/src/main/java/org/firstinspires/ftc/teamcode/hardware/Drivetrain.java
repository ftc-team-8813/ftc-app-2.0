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
import org.firstinspires.ftc.robotcore.external.navigation.CurrentUnit;

public class Drivetrain{
    public DcMotorEx front_left;
    public DcMotorEx front_right;
    public DcMotorEx back_left;
    public DcMotorEx back_right;
    public Drivetrain(DcMotorEx front_left, DcMotorEx front_right, DcMotorEx back_left, DcMotorEx back_right){
        this.front_left = front_left;
        this.front_right = front_right;
        this.back_left = back_left;
        this.back_right = back_right;

        front_left.setZeroPowerBehavior(DcMotorEx.ZeroPowerBehavior.BRAKE);
        front_right.setZeroPowerBehavior(DcMotorEx.ZeroPowerBehavior.BRAKE);
        back_left.setZeroPowerBehavior(DcMotorEx.ZeroPowerBehavior.BRAKE);
        back_right.setZeroPowerBehavior(DcMotorEx.ZeroPowerBehavior.BRAKE);

        front_right.setDirection(DcMotorSimple.Direction.REVERSE);
        back_right.setDirection(DcMotorSimple.Direction.REVERSE);
    }

    public void move(double forward, double strafe, double turn, double turn_correct) {
        front_left.setPower((forward + strafe + (turn + turn_correct)));
        front_right.setPower((forward - strafe - (turn + turn_correct)));
        back_left.setPower((-forward - strafe + (turn + turn_correct)));
        back_right.setPower((-forward + strafe - (turn + turn_correct)));
    }

    public void getMotorPowers(Telemetry telemetry){
        telemetry.addData("Front Left Power", front_left.getCurrent(CurrentUnit.AMPS));
        telemetry.addData("Front Right Power", front_right.getCurrent(CurrentUnit.AMPS));
        telemetry.addData("Back Left Power", back_left.getCurrent(CurrentUnit.AMPS));
        telemetry.addData("Back Right Power", back_right.getCurrent(CurrentUnit.AMPS));
    }







}
