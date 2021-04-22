package org.firstinspires.ftc.teamcode.hardware;

import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.robotcore.external.Telemetry;

/**
 * Intake -- Control intake movement.
 */
public class Intake
{
    public final DcMotor ramp;
    public final CRServo roller;
    public final DcMotor intake;
    
    private static final double TPR = 145.1;
    
    public Intake(DcMotor ramp, DcMotor intake, CRServo roller)
    {
        this.ramp = ramp;
        this.intake = intake;
        this.roller = roller;
        
        intake.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        intake.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
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
    
    public void zero(Telemetry telemetry)
    {
        int pos = intake.getCurrentPosition();
        
        int off = pos - (int)((int)((pos + TPR/2) / TPR) * TPR);
        double power = -((double)off / TPR) * 2;
        
        telemetry.addData("Intake zeroing", power);
        
        intake.setPower(power);
    }
}
