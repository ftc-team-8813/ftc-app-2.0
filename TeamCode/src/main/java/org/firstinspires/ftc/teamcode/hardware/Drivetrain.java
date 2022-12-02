package org.firstinspires.ftc.teamcode.hardware;

import com.arcrobotics.ftclib.geometry.Pose2d;
import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.hardware.bosch.JustLoggingAccelerationIntegrator;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.hardware.navigation.PID;
import org.firstinspires.ftc.teamcode.opmodes.util.FTCDashboardValues;

public class Drivetrain {

    private final DcMotorEx front_left;
    private final DcMotorEx front_right;
    private final DcMotorEx back_left;
    private final DcMotorEx back_right;
    private final BNO055IMU imu;
    private final FTCDashboardValues ftcdbvals = new FTCDashboardValues();
    private boolean has_reached;

    public Drivetrain(DcMotorEx front_left, DcMotorEx front_right, DcMotorEx back_left, DcMotorEx back_right, BNO055IMU imu) {
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

        front_left.setDirection(DcMotorSimple.Direction.REVERSE);
        back_left.setDirection(DcMotorSimple.Direction.REVERSE);

        front_left.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        front_right.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        back_left.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        back_right.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
    }

    public void resetEncoders() {
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
        front_left.setPower((forward + strafe + (turn + turn_correct)));
        front_right.setPower((forward - strafe - (turn + turn_correct)));
        back_left.setPower((forward - strafe + (turn + turn_correct)));
        back_right.setPower((forward + strafe - (turn + turn_correct)));
    }

    public void move(double forward, double strafe, double turn, double turn_correct, double denominator) {
        front_left.setPower(((forward + strafe + (turn + turn_correct)) / denominator));
        front_right.setPower(((forward - strafe - (turn + turn_correct)) / denominator));
        back_left.setPower(((forward - strafe + (turn + turn_correct)) / denominator));
        back_right.setPower(((forward + strafe - (turn + turn_correct)) / denominator));
    }

    public void stop() {
        front_left.setPower(0);
        front_right.setPower(0);
        back_left.setPower(0);
        back_right.setPower(0);
    }

    public boolean hasReached() {
        return has_reached;
    }

    public void autoMove(double forward, double strafe, double turn, double turnErrorBand, double forwardErrorBand, double strafeErrorBand, Pose2d odo, Telemetry telemetry, double lateralSpeed, double turnSpeed) {
        has_reached = false;

        PID forwardPID = new PID(0, 0, 0, 0, 0, 0); //add it on ftclib
        PID strafePID = new PID(0, 0, 0, 0, 0, 0);
        PID turnPID = new PID(ftcdbvals.getKp(), 0,0, 0, 0, 0);

        double y = odo.getY();
        double x = odo.getY();
        double rot = odo.getRotation().getDegrees();

        double forward_error = Math.abs(forward - y);
        double strafe_error = Math.abs(strafe - x);
        double turn_error = Math.abs(turn - rot);

        double forwardPower = Range.clip(forwardPID.getOutPut(forward, y, 0), -lateralSpeed, lateralSpeed);
        double strafePower = Range.clip(strafePID.getOutPut(strafe, x, 0), -lateralSpeed, lateralSpeed);
        double turnPower = Range.clip(turnPID.getOutPut(turn, rot, 0), -turnSpeed, turnSpeed);

        move(forwardPower, strafePower, turnPower, 0);

        if ((forward_error <= forwardErrorBand) && (strafe_error <= strafeErrorBand) && (turn_error <= turnErrorBand)) {
            has_reached = true;
        }

        telemetry.addData("F Power",forwardPower);
        telemetry.addData("S Power",strafePower);
        telemetry.addData("T Power",turnPower);
        telemetry.addData("F Error",forward_error);
        telemetry.addData("S Error",strafe_error);
        telemetry.addData("T Error",turn_error);
        telemetry.addData("F Current",y);
        telemetry.addData("S Current",x);
        telemetry.addData("T Current",rot);
        telemetry.addData("Has Reached",has_reached);
    }

    public double getForwardPosition() {
        return (front_left.getCurrentPosition() + front_right.getCurrentPosition() + back_left.getCurrentPosition() + back_right.getCurrentPosition()) / 4.0;
    }

    public double getStrafePosition() {
        return (front_left.getCurrentPosition() - front_right.getCurrentPosition() - back_left.getCurrentPosition() + back_right.getCurrentPosition()) / 4.0;
    }

    public double getHeading() {
        return imu.getAngularOrientation().firstAngle;
    }
}
