package org.firstinspires.ftc.teamcode.opmodes.util;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.hardware.Robot;

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
        telemetry.addData("Left potentiometer V", robot.lift_grabber.left_potentiometer.getVoltage());
        telemetry.addData("Right potentiometer V", robot.lift_grabber.right_potentiometer.getVoltage());
        telemetry.update();
    }
}
