package org.firstinspires.ftc.teamcode.hardware;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;

import java.util.ArrayList;
import java.util.List;

public class Shooter
{
    
    public final DcMotor motor;
    private double rampTime;
    private double maxPower;
    private double[] powershot_power;
    private double maxPowerDef;
    
    private long startTime;
    private boolean started;
    
    private List<ShooterPower> powers;
    private int currPreset = -1;
    
    private static class ShooterPower
    {
        private final double power;
        private final int color;
        
        public ShooterPower(double power, int color)
        {
            this.power = power;
            this.color = color;
        }
        
        public ShooterPower(JsonObject obj)
        {
            this.power = obj.get("power").getAsDouble();
            JsonArray color = obj.getAsJsonArray("color");
            this.color = (color.get(0).getAsInt() << 16 |
                          color.get(1).getAsInt() << 8 |
                          color.get(2).getAsInt());
        }
    }
    
    public Shooter(DcMotor motor, JsonObject config)
    {
        this.motor = motor;
        motor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        motor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        powers = new ArrayList<>();
        loadConfiguration(config);
    }
    
    public void start()
    {
        start(maxPower);
    }
    
    public void start(double power)
    {
        maxPower = power;
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
    
    public boolean running()
    {
        return started;
    }
    
    public void update(Telemetry telemetry)
    {
        if (!started)
        {
            motor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
            double vel = ((DcMotorEx)motor).getVelocity(AngleUnit.RADIANS);
            double power = -vel * 0.05;
            setPower(power);
        }
        else
        {
            motor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            double time = (double) (System.nanoTime() - startTime) / 1_000_000_000;
            double power;
            if (time >= rampTime) power = maxPower;
            else power = (time / rampTime) * maxPower;
            setPower(power);
        }
        telemetry.addData("shooter_power", "%.4f", maxPower);
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
        
        JsonArray shooterPowers = root.get("presets").getAsJsonArray();
        for (int i = 0; i < shooterPowers.size(); i++)
        {
            powers.add(new ShooterPower(shooterPowers.get(i).getAsJsonObject()));
        }
    }
    
    public void setMaxPower(double power)
    {
        this.maxPower = power;
    }
    
    @Deprecated
    public void powershot(int i)
    {
        if (i < 0) maxPower = maxPowerDef;
        maxPower = powershot_power[i];
    }
    
    public void setPreset(int i)
    {
        int x = i % powers.size();
        if (x < 0) x += powers.size();
        currPreset = x;
        maxPower = powers.get(currPreset).power;
    }
    
    public int getPresetColor()
    {
        if (currPreset < 0) return 0;
        return powers.get(currPreset).color;
    }
    
    public int getCurrPreset()
    {
        return currPreset;
    }
}
