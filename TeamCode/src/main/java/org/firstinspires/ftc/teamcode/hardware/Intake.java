package org.firstinspires.ftc.teamcode.hardware;

import com.qualcomm.robotcore.hardware.DcMotor;

public class Intake {
    DcMotor intake;


    public Intake(DcMotor intake){
        this.intake = intake;
    }


    public void intake(){
        intake.setPower(-1);
    }

    public void outtake(){
        intake.setPower(1);
    }

    public void stop(){
        intake.setPower(0);
    }
}
