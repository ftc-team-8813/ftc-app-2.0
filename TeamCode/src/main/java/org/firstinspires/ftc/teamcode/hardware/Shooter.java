package org.firstinspires.ftc.teamcode.hardware;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.teamcode.util.Configuration;

import java.io.File;

public class Shooter
{
    
    public final DcMotor motor;
    private double rampTime;
    private double maxPower;
    private double[] powershot_power;
    private double maxPowerDef;
    
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

    public void setPower(double power){
        motor.setPower(power);
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
        maxPowerDef = maxPower;
        JsonArray powershots = root.get("powershots").getAsJsonArray();
        powershot_power = new double[powershots.size()];
        for (int i = 0; i < powershot_power.length; i++)
        {
            powershot_power[i] = powershots.get(i).getAsDouble();
        }
    }
    
    public void powershot(int i)
    {
        if (i < 0) maxPower = maxPowerDef;
        maxPower = powershot_power[i];
    }
}
