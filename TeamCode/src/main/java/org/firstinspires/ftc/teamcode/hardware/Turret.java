package org.firstinspires.ftc.teamcode.hardware;

import com.google.gson.JsonObject;
import com.qualcomm.robotcore.hardware.AnalogInput;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.teamcode.util.Configuration;
import org.firstinspires.ftc.teamcode.util.Time;

import java.io.File;

public class Turret {
    
    private DcMotor turret;
    public final Shooter shooter;
    private Servo pusher;
    private Servo aim;
    private CalibratedAnalogInput turretFb;
    
    private double turretHome;
    private double turretKp;
    
    private double target;
    private double lastUpdate;
    private double lastPos;
    
    public Turret(DcMotor turret, DcMotor shooter, Servo pusher, Servo aim, AnalogInput rotateFeedback,
                  File shooterConfig, File fbConfig, File turretConfig)
    {
        this.turret = turret;
        this.shooter = new Shooter(shooter, shooterConfig);
        this.pusher = pusher;
        this.aim = aim;
        this.turretFb = new CalibratedAnalogInput(rotateFeedback, fbConfig);
    
        JsonObject root = Configuration.readJson(turretConfig);
        turretHome = root.get("home").getAsDouble();
        turretKp   = root.get("kp").getAsDouble();
        
        target = turretHome;
    }
    
    public void rotate(double position)
    {
        if (position < 0.1) position = 0.1;
        else if (position > 0.9) position = 0.9;
        target = position;
    }
    
    public void home()
    {
        target = turretHome;
    }
    
    public double getTarget()
    {
        return target;
    }
    
    public double getPosition()
    {
        return lastPos;
    }
    
    public void update()
    {
        shooter.update();
        if (lastUpdate == 0) lastUpdate = Time.now();
        double dt = Time.since(lastUpdate);
        double pos = turretFb.get();
        double error = target - pos;
        
        double power = error * dt * turretKp;
        turret.setPower(power);
        lastUpdate = Time.now();
    }
    
    public void push()
    {
        pusher.setPosition(0.85);
    }
    
    public void unpush()
    {
        pusher.setPosition(1);
    }
}
