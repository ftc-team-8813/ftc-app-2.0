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

    @Deprecated
    public void setIntake(int mode){
        // TODO Find actual power values
        if (mode == 0){
            intake.setPower(0);
        } else if (mode == 1){
            intake.setPower(-1);
        }
    }
}
