package org.firstinspires.ftc.teamcode.hardware;

import com.google.gson.JsonObject;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;

/**
 * Intake -- Control intake movement.
 */
public class Intake
{
    public final DcMotor ramp;
    public final CRServo roller;
    public final DcMotor intake;
    
    public Intake(DcMotor ramp, DcMotor intake, CRServo roller, Servo pivot,
                  JsonObject config)
    {
        this.ramp = ramp;
        this.intake = intake;
        this.roller = roller;
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
        runRoller(speed);
        runRamp(speed);
        runIntake(speed);
    }
    
    public void runRoller(double speed)
    {
        roller.setPower(speed);
    }
    
    public void runRamp(double speed)
    {
        ramp.setPower(speed);
    }
    
    public void runIntake(double speed)
    {
        intake.setPower(speed);
    }
    
    public void stop()
    {
        run(0);
    }
}
