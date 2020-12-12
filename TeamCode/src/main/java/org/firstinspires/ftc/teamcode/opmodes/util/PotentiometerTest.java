package org.firstinspires.ftc.teamcode.opmodes.util;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.teamcode.hardware.Robot;
import org.firstinspires.ftc.teamcode.util.Storage;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;

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
        double[] pos = robot.lift.getPositions();
        // double[] xy = robot.turret.getXYPos();
        telemetry.addData("values", "%.3f, %.3f", pos[0], pos[1]);
        // telemetry.addData("xy", "%.3f, %.3f", xy[0], xy[1]);
        telemetry.update();
    }
}
