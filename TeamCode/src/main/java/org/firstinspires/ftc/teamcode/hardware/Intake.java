package org.firstinspires.ftc.teamcode.hardware;

import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

import static com.qualcomm.robotcore.hardware.DcMotorSimple.Direction.REVERSE;

/**
 * Intake -- Control intake movement.
 */
public class Intake {
    public final DcMotor ramp;
    public final CRServo appendage;

    public Intake(DcMotor ramp, CRServo appendage) {
        this.ramp = ramp;
        this.ramp.setDirection(REVERSE);
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
        ramp.setPower(speed);
        appendage.setPower(speed);
    }
    
    public void stop()
    {
        run(0);
    }
}
