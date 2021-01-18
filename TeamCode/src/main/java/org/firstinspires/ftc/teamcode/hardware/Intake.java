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
        run(1);
    }
    
    public void outtake()
    {
        run(-1);
    }
    
    public void run(double speed)
    {
        intake.setPower(speed);
        ramp.setPower(speed);
    }
    
    public void stop()
    {
        run(0);
    }
}
