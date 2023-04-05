package org.firstinspires.ftc.teamcode.opmodes.util;

import android.text.TextUtils;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.opmodes.LoggingOpMode;
import org.firstinspires.ftc.teamcode.vision.webcam.Webcam;

@TeleOp(name = "Webcam Finder")
public class WebcamFinder extends LoggingOpMode
{
    private String telemString;
    
    @Override
    public void init()
    {
        super.init();
        Webcam[] webcams = Webcam.getConnected();
        
        String[] serials = new String[webcams.length];
        for (int i = 0; i < webcams.length; i++)
        {
            serials[i] = webcams[i].getSerialNumber();
        }
        
        telemString = TextUtils.join(", ", serials);
    }
    
    @Override
    public void init_loop()
    {
        telemetry.addData("Webcams connected", telemString);
    }
    
    @Override
    public void loop()
    {
        requestOpModeStop();
    }
}
