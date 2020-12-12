package org.firstinspires.ftc.teamcode.hardware;

import com.qualcomm.robotcore.hardware.DcMotor;

public class Intake {
    DcMotor intake;

    public Intake(DcMotor intake){
        this.intake = intake;
    }

    public void setIntake(int mode){
        // TODO Find actual power values
        if (mode == 0){
            intake.setPower(0);
        } else if (mode == 1){
            intake.setPower(1);
        }
    }
}
