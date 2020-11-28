package org.firstinspires.ftc.teamcode.opmodes.util;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.hardware.Robot;

import java.util.Arrays;

@TeleOp(name="Potentiometer Test")
public class PotentiometerTest extends OpMode
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
        telemetry.addData("Potentiometer Pos (Left, Right)", Arrays.toString(robot.turret.getPotenPos()));
        telemetry.update();
    }
}
