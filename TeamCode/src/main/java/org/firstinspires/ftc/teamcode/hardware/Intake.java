package org.firstinspires.ftc.teamcode.hardware;

import com.google.gson.JsonObject;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;

/**
 * Intake -- Control intake movement.
 */
public class Intake {
    public final DcMotor ramp;
    public final CRServo roller;
    public final DcMotor intake;
    public final Servo   pivot;
    private boolean pivot_in;
    
    private double pivot_pos_in;
    private double pivot_pos_out;

    public Intake(DcMotor ramp, DcMotor intake, CRServo roller, Servo pivot,
                  JsonObject config) {
        this.ramp   = ramp;
        this.intake = intake;
        this.roller = roller;
        this.pivot  = pivot;
        
        pivot_pos_in  = config.get("pivot_in").getAsDouble();
        pivot_pos_out = config.get("pivot_out").getAsDouble();
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
        ramp.setPower(speed * 0.85);
        intake.setPower(-speed);
        roller.setPower(speed);
    }
    
    public void pivotIn()
    {
        pivot.setPosition(pivot_pos_in);
        pivot_in = true;
    }
    
    public void pivotOut()
    {
        pivot.setPosition(pivot_pos_out);
        pivot_in = false;
    }
    
    public void pivotToggle()
    {
        if (pivot_in) pivotOut();
        else pivotIn();
    }
    
    public boolean isPivotIn()
    {
        return pivot_in;
    }
    
    public void stop()
    {
        run(0);
    }
}
