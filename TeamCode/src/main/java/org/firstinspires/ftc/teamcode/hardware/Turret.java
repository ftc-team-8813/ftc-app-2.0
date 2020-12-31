package org.firstinspires.ftc.teamcode.hardware;

import com.qualcomm.robotcore.hardware.AnalogInput;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.teamcode.util.Configuration;
import org.firstinspires.ftc.teamcode.util.Logger;
import org.firstinspires.ftc.teamcode.util.Storage;
import org.firstinspires.ftc.teamcode.util.event.EventBus;
import org.firstinspires.ftc.teamcode.util.event.TriggerEvent;

import java.io.File;
import java.util.HashMap;

public class Turret {
    
    private DcMotor turret;
    public final Shooter shooter;
    private Servo pusher;
    private Servo aim;
    private CalibratedAnalogInput turretFb;
    
    public Turret(DcMotor turret, DcMotor shooter, Servo pusher, Servo aim, AnalogInput rotateFeedback,
                  File shooterConfig, File rotateConfig)
    {
        this.turret = turret;
        this.shooter = new Shooter(shooter, shooterConfig);
        this.pusher = pusher;
        this.aim = aim;
        this.turretFb = new CalibratedAnalogInput(rotateFeedback, rotateConfig);
    }
    
    public void rotate(double position)
    {
        // TODO implement this: set target position (between 0 and 1)
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
