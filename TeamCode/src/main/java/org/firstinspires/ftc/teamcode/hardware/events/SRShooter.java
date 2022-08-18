package org.firstinspires.ftc.teamcode.hardware.events;

import com.qualcomm.robotcore.hardware.DcMotorEx;

public class SRShooter {
    DcMotorEx shooter;

    public SRShooter(DcMotorEx shooter){
        this.shooter = shooter;
    }
}