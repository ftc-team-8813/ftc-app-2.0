package org.firstinspires.ftc.teamcode.hardware;

import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.teamcode.hardware.navigation.Odometry;

import static com.qualcomm.robotcore.hardware.DcMotorSimple.Direction.REVERSE;

/**
 * Drivetrain -- handles movement of the drive wheels.
 */

public class Drivetrain {
    public final DcMotor top_left;
    public final DcMotor bottom_left;
    public final DcMotor top_right;
    public final DcMotor bottom_right;
    public final Odometry odometry;
    public IMU imu;
    public int auto_id = -1;

    public Drivetrain(DcMotor top_left, DcMotor bottom_left, DcMotor top_right, DcMotor bottom_right){
        this.top_left = top_left;
        this.bottom_left = bottom_left;
        this.top_right = top_right;
        this.bottom_right = bottom_right;
        this.odometry = new Odometry(top_left, top_right, this.imu);

        //Reverses left side to match right side rotation and sets mode
        top_right.setDirection(REVERSE);
        bottom_right.setDirection(REVERSE);
        // TODO drive motor encoders seem to be faulty right now
        top_left.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        bottom_left.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        top_right.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        bottom_right.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
    }

    public void resetEncoders(){
        top_right.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        bottom_right.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        top_left.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        bottom_left.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        // reset to driving mode
        top_left.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        bottom_left.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        top_right.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        bottom_right.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
    }
    
    /**
     * Move the drivetrain based on gamepad-compatible inputs
     * @param left_stick_y Left Wheel Velocity
     * @param right_stick_y Right Wheel Velocity
     */
    public void telemove(double left_stick_y, double right_stick_y){
        //Subtracts power from forward based on the amount of rotation in the other stick
        double left_wheel_speed = -left_stick_y + right_stick_y;
        double right_wheel_speed = -left_stick_y - right_stick_y;
        top_left.setPower(left_wheel_speed);
        bottom_left.setPower(left_wheel_speed);
        top_right.setPower(right_wheel_speed);
        bottom_right.setPower(right_wheel_speed);
    }

    public void automove(double distance){
        odometry.setTargetPos(distance);
    }

    public int odoPIDUpdate(){
        final double kP = 1;
        double error = odometry.getX();
        double left_wheel_speed = -error * kP;
        double right_wheel_speed = error * kP;
        // TODO Increase deadband for error to make it possible to reach
        if (error != 0){
            top_left.setPower(left_wheel_speed);
            bottom_left.setPower(left_wheel_speed);
            top_right.setPower(right_wheel_speed);
            bottom_right.setPower(right_wheel_speed);
            return this.auto_id;
        } else {
            return this.auto_id + 1;
        }
    }
}
