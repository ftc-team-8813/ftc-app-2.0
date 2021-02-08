package org.firstinspires.ftc.teamcode.opmodes.test;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.opmodes.LoggingOpMode;
import org.firstinspires.ftc.teamcode.vision.webcam.Webcam;

@TeleOp(name="Webcam Finder")
public class WebcamFinder extends LoggingOpMode
{
    @Override
    public void init()
    {
        Webcam.getConnected();
    }
    
    @Override
    public void loop()
    {
        requestOpModeStop();
    }
}
