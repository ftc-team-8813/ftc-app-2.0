package org.firstinspires.ftc.teamcode.opmodes.test;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.hardware.Robot;
import org.firstinspires.ftc.teamcode.opmodes.LoggingOpMode;

@TeleOp(name="Turret Test")
public class TurretTest extends LoggingOpMode
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
        telemetry.addData("Position", "%.3f", (double)robot.turret.turretFb.getCurrentPosition());
        robot.turret.turret.setPower(-gamepad1.left_stick_y * 0.3);
    }
}
