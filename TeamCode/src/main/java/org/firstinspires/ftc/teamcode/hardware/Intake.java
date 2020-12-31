package org.firstinspires.ftc.teamcode.hardware;

import com.qualcomm.robotcore.hardware.DcMotor;

/**
 * Intake -- Control intake movement.
 * TODO: Do we need this?
 */
public class Intake {
    public final DcMotor intake;
    public final DcMotor ramp;

    public Intake(DcMotor intake, DcMotor ramp){
        this.intake = intake;
        this.ramp = ramp;
    }
}
