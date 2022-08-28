package org.firstinspires.ftc.teamcode.opmodes.teleop;

import com.qualcomm.robotcore.hardware.DcMotorEx;


public class SRDrivetrain {
    DcMotorEx frontLeft;
    DcMotorEx frontRight;
    DcMotorEx backLeft;
    DcMotorEx backRight;


    public SRDrivetrain(DcMotorEx FL, DcMotorEx FR, DcMotorEx BL, DcMotorEx BR){
        this.frontLeft = FL;
        this.frontRight = FR;
        this.backLeft = BL;
        this.backRight = BR;
    }

    public void move(double forward, double strafe, double turn, double turn_correct) {
        frontLeft.setPower(-(-forward + strafe + (turn+turn_correct)));
        frontRight.setPower((-forward - strafe - (turn+turn_correct)));
        backLeft.setPower(-(-forward - strafe + (turn+turn_correct)));
        backRight.setPower((-forward + strafe - (turn+turn_correct)));
    }
}
