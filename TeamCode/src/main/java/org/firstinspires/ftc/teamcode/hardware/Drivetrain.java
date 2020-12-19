package org.firstinspires.ftc.teamcode.hardware;

import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.robotcore.external.Telemetry;

import static com.qualcomm.robotcore.hardware.DcMotorSimple.Direction.REVERSE;

/**
 * Drivetrain -- handles movement of the drive wheels.
 */

public class Drivetrain {
    private DcMotor top_left;
    private DcMotor bottom_left;
    private DcMotor top_right;
    private DcMotor bottom_right;

    public Drivetrain(DcMotor top_left, DcMotor bottom_left, DcMotor top_right, DcMotor bottom_right){
        this.top_left = top_left;
        this.bottom_left = bottom_left;
        this.top_right = top_right;
        this.bottom_right = bottom_right;

        //Reverses left side to match right side rotation and sets mode
        top_right.setDirection(REVERSE);
        bottom_right.setDirection(REVERSE);
        top_left.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        bottom_left.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        top_right.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        bottom_right.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
    }

    public void setModeRun(){
        top_right.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        bottom_right.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        top_left.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        bottom_left.setMode(DcMotor.RunMode.RUN_TO_POSITION);
    }

    public void setModeReset(){
        top_right.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        bottom_right.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        top_left.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        bottom_left.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
    }

    public void setPos(int ticks){
        top_left.setTargetPosition(ticks);
        bottom_left.setTargetPosition(ticks);
        top_right.setTargetPosition(ticks);
        bottom_right.setTargetPosition(ticks);
    }

    public void setPower(double power){
        top_right.setPower(power);
        bottom_right.setPower(power);
        top_left.setPower(power);
        bottom_left.setPower(power);
    }

    /**
     * Move the drivetrain based on gamepad-compatible inputs
     * @param left_stick_y Forward velocity
     * @param right_stick_x Turn velocity
     */
    public void telemove(double left_stick_y, double right_stick_x){
        //Subtracts power from forward based on the amount of rotation in the other stick
        double left_wheel_speed = -left_stick_y+right_stick_x;
        double right_wheel_speed = -left_stick_y-right_stick_x;
        top_left.setPower(left_wheel_speed);
        bottom_left.setPower(left_wheel_speed);
        top_right.setPower(right_wheel_speed);
        bottom_right.setPower(right_wheel_speed);
    }

    public void automove(double distance, double power, Telemetry telemetry){
        setModeReset();
        final double ENCODER_TICKS = 537.6;
        double ratio = (distance/(101.5 * Math.PI / 10));
        int ticks = (int) (ratio * (24 / 22) * ENCODER_TICKS);
        telemetry.addData("Set Encoder Ticks", ticks);
        setPos(ticks);
        setPower(power);
        setModeRun();
    }
}
