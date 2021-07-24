package org.firstinspires.ftc.teamcode.hardware;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

public class Drivetrain {
    private DcMotor front_left;
    private DcMotor front_right;
    private DcMotor back_left;
    private DcMotor back_right;

    public Drivetrain(DcMotor front_left, DcMotor front_right, DcMotor back_left, DcMotor back_right){
        this.front_left = front_left;
        this.front_right = front_right;
        this.back_left = back_left;
        this.back_right = back_right;

        front_right.setDirection(DcMotorSimple.Direction.REVERSE);
        back_right.setDirection(DcMotorSimple.Direction.REVERSE);
    }

    public void telemove(double forward, double strafe, double turn){
        front_left.setPower(forward + strafe + turn);
        front_right.setPower(forward - strafe - turn);
        back_left.setPower(forward - strafe + turn);
        back_right.setPower(forward + strafe - turn);
    }
}
