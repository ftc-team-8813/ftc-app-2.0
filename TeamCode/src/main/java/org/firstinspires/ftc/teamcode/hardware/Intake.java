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
        run(1, 0.5);
    }
    
    public void outtake()
    {
        run(-1, -0.5);
    }
    
    public void run(double ramp_speed, double appendage_speed)
    {
        ramp.setPower(ramp_speed);
        appendage.setPower(appendage_speed);
    }
    
    public void stop()
    {
        run(0, 0);
    }
}
