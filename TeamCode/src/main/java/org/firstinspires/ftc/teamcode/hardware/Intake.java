package org.firstinspires.ftc.teamcode.hardware;

import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;

/**
 * Intake -- Control intake movement.
 */
public class Intake {
    public final DcMotor intake;
    public final DcMotor ramp;
    public final CRServo appendage;

    public Intake(DcMotor intake, DcMotor ramp, CRServo appendage){
        this.intake = intake;
        this.ramp = ramp;
        this.appendage = appendage;
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
        appendage.setPower(speed);
    }
    
    public void stop()
    {
        run(0);
    }
}
