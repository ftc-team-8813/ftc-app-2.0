package org.firstinspires.ftc.teamcode.opmodes.test;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.hardware.Robot;
import org.firstinspires.ftc.teamcode.opmodes.LoggingOpMode;

@TeleOp(name="Ramp Test")
@Disabled
public class RampTest extends LoggingOpMode
{
    private Robot robot;
    
    @Override
    public void init()
    {
        robot = new Robot(hardwareMap);
    }
    
    @Override
    public void loop()
    {
        robot.intake.run(gamepad1.left_stick_y);
    }
}
