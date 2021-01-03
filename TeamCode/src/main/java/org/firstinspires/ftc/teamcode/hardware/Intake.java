package org.firstinspires.ftc.teamcode.hardware;

import com.qualcomm.robotcore.hardware.DcMotor;

/**
 * Intake -- Control intake movement.
 */
public class Intake {
    public final DcMotor intake;
    public final DcMotor ramp;

    public Intake(DcMotor intake, DcMotor ramp){
        this.intake = intake;
        this.ramp = ramp;
    }
    
    public void intake()
    {
        intake.setPower(1);
        ramp.setPower(0.75);
    }
    
    public void outtake()
    {
        intake.setPower(-1);
        ramp.setPower(-1);
    }
    
    public void stop()
    {
        intake.setPower(0);
        ramp.setPower(0);
    }
}
