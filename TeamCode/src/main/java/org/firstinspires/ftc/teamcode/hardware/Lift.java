package org.firstinspires.ftc.teamcode.hardware;

import com.qualcomm.robotcore.hardware.DcMotorEx;

public class Lift {

    private final DcMotorEx lower;
    private final DcMotorEx upper;
    private final DcMotorEx wrist;

    public Lift(DcMotorEx lower, DcMotorEx upper, DcMotorEx wrist) {
        this.lower = lower;
        this.upper = upper;
        this.wrist = wrist;
    }


}
