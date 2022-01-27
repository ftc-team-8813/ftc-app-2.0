package org.firstinspires.ftc.teamcode.hardware;

import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorImpl;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.DistanceSensor;
import com.qualcomm.robotcore.util.Range;

import org.checkerframework.checker.units.qual.A;
import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;
import org.firstinspires.ftc.teamcode.util.Status;

public class Drivetrain {
    public final DcMotorEx front_left;
    public final DcMotorEx front_right;
    public final DcMotorEx back_left;
    public final DcMotorEx back_right;
    private final IMU imu;
    public final DistanceSensor x_dist;

    public Drivetrain(DcMotorEx front_left, DcMotorEx front_right, DcMotorEx back_left, DcMotorEx back_right, IMU imu, DistanceSensor x_dist) {
        this.front_left = front_left;
        this.front_right = front_right;
        this.back_left = back_left;
        this.back_right = back_right;
        this.imu = imu;
        this.x_dist = x_dist;

        front_right.setDirection(DcMotorSimple.Direction.REVERSE);
        back_right.setDirection(DcMotorSimple.Direction.REVERSE);

        front_right.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        back_right.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        front_left.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        back_left.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        front_right.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        back_right.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        front_left.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        back_left.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        front_left.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        front_right.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        back_left.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        back_right.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);


    }

    public void move(double forward, double strafe, double turn) {
        // Slowing right side to keep forward moving straight
        front_left.setPower((forward + strafe + turn));// * 0.87);
        front_right.setPower(forward - strafe - turn);
        back_left.setPower((forward - strafe + turn));// * 0.87);
        back_right.setPower(forward + strafe - turn);
    }

    public void stop() {
        front_left.setPower(0);
        front_right.setPower(0);
        back_left.setPower(0);
        back_right.setPower(0);
    }

    public enum encoderNames {FRONT_RIGHT, BACK_RIGHT, FRONT_LEFT, BACK_LEFT}

    public int getEncoderValue(encoderNames motor) {
        switch (motor) {
            case FRONT_RIGHT:
                return front_right.getCurrentPosition();
            case BACK_RIGHT:
                return back_right.getCurrentPosition();
            case FRONT_LEFT:
                return front_left.getCurrentPosition();
            case BACK_LEFT:
                return back_left.getCurrentPosition();
        }
    return 0;
    }
}