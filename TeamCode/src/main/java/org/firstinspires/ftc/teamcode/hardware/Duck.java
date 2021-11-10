package org.firstinspires.ftc.teamcode.hardware;

import com.qualcomm.robotcore.hardware.CRServo;

public class Duck {
    private CRServo spinner;

    public Duck(CRServo spinner){
        this.spinner = spinner;
    }

    public void spin(double power){
        spinner.setPower(power);
    }
}
