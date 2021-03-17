package org.firstinspires.ftc.teamcode.hardware;

import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;

/**
 * Intake -- Control intake movement.
 */
public class Intake {
    public final DcMotor ramp;
    public final CRServo roller;

    public Intake(DcMotor ramp, CRServo roller) {
        this.ramp = ramp;
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
        ramp.setPower(-speed);
        roller.setPower(speed);
    }
    
    public void stop()
    {
        run(0);
    }
}
