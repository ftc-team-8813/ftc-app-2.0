package org.firstinspires.ftc.teamcode.hardware;

import com.qualcomm.robotcore.hardware.DcMotor;

/**
 * Drivetrain -- handles movement of the drive wheels.
 */
public class Drivetrain {
    public final DcMotor top_left;
    public final DcMotor bottom_left;
    public final DcMotor top_right;
    public final DcMotor bottom_right;

    public Drivetrain(DcMotor top_left, DcMotor bottom_left, DcMotor top_right, DcMotor bottom_right){
        this.top_left = top_left;
        this.bottom_left = bottom_left;
        this.top_right = top_right;
        this.bottom_right = bottom_right;
    }
    
    /**
     * Move the drivetrain based on gamepad-compatible inputs
     * @param left_stick_y Forward velocity
     * @param right_stick_y Turn velocity
     */
    public void telemove(double left_stick_y, double right_stick_y){
        //Subtracts power from forward based on the amount of rotation in the other stick
        double left_wheel_speed = -left_stick_y;
        double right_wheel_speed = -right_stick_y;
        top_left.setPower(left_wheel_speed);
        bottom_left.setPower(left_wheel_speed);
        top_right.setPower(right_wheel_speed);
        bottom_right.setPower(right_wheel_speed);
    }
}
