package org.firstinspires.ftc.teamcode.hardware;

import com.qualcomm.robotcore.hardware.DcMotor;

public class Duck {
    private DcMotor spinner;

    public Duck(DcMotor spinner){
        this.spinner = spinner;
    }

    public void spin(double power){
        spinner.setPower(power);
    }
}
