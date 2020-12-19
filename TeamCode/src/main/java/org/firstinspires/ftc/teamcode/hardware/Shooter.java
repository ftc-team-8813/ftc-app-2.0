package org.firstinspires.ftc.teamcode.hardware;

import com.google.gson.JsonObject;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.teamcode.util.Configuration;

import java.io.File;

public class Shooter
{
    
    private DcMotor motor;
    private double rampTime;
    private double maxPower;
    
    private long startTime;
    private boolean started;
    
    public Shooter(DcMotor motor, File configFile)
    {
        this.motor = motor;
        motor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        loadConfiguration(configFile);
    }
    
    public void start()
    {
        startTime = System.nanoTime();
        started = true;
    }
    
    public void stop()
    {
        started = false;
    }
    
    public void update()
    {
        if (!started)
        {
            motor.setPower(0);
            return;
        }
        
        double time = (double)(System.nanoTime() - startTime) / 1_000_000_000;
        double power;
        if (time >= rampTime) power = 1;
        else                  power = (time / rampTime) * maxPower;
        motor.setPower(power);
    }
    
    private void loadConfiguration(File configFile)
    {
        JsonObject root = Configuration.readJson(configFile);
        rampTime = root.get("rampTime").getAsDouble();
        maxPower = root.get("maxPower").getAsDouble();
    }
}
