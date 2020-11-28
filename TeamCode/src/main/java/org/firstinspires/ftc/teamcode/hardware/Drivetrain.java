package org.firstinspires.ftc.teamcode.hardware;

import com.qualcomm.robotcore.hardware.DcMotor;

public class Drivetrain {
    DcMotor topLeft;
    DcMotor bottomLeft;
    DcMotor topRight;
    DcMotor bottomRight;

    public Drivetrain(DcMotor topLeft, DcMotor bottomLeft, DcMotor topRight, DcMotor bottomRight){
        this.topLeft = topLeft;
        this.bottomLeft = bottomLeft;
        this.topRight = topRight;
        this.bottomRight = bottomRight;
    }

    public void telemove(double left_stick_y, double right_stick_x){
        //Subtracts power from forward based on the amount of rotation in the other stick
        double left_wheel_speed = -left_stick_y+right_stick_x;
        double right_wheel_speed = -left_stick_y-right_stick_x;
        topLeft.setPower(left_wheel_speed);
        bottomLeft.setPower(left_wheel_speed);
        topRight.setPower(right_wheel_speed);
        bottomRight.setPower(right_wheel_speed);
    }
}
