package org.firstinspires.ftc.teamcode.hardware;

import com.google.gson.JsonObject;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.teamcode.util.Configuration;

import java.io.File;

public class Shooter
{
    
    public final DcMotor motor;
    private double rampTime;
    private double maxPower;
    
    private long startTime;
    private boolean started;
    
    public Shooter(DcMotor motor, JsonObject config)
    {
        this.motor = motor;
        motor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        motor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        loadConfiguration(config);
        
    }
    
    public void start()
    {
        if (!started)
        {
            startTime = System.nanoTime();
            started = true;
        }
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
        if (time >= rampTime) power = maxPower;
        else                  power = (time / rampTime) * maxPower;
        motor.setPower(power);
    }
    
    private void loadConfiguration(JsonObject root)
    {
        rampTime = root.get("rampTime").getAsDouble();
        maxPower = root.get("maxPower").getAsDouble();
    }
}
