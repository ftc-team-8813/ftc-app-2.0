package org.firstinspires.ftc.teamcode.hardware;

import com.google.gson.JsonObject;
import com.qualcomm.robotcore.hardware.AnalogInput;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.hardware.events.TurretEvent;
import org.firstinspires.ftc.teamcode.util.Configuration;
import org.firstinspires.ftc.teamcode.util.Logger;
import org.firstinspires.ftc.teamcode.util.Time;
import org.firstinspires.ftc.teamcode.util.event.Event;
import org.firstinspires.ftc.teamcode.util.event.EventBus;

import java.io.File;

public class Turret {
    
    public final DcMotor turret;
    public final Shooter shooter;
    private Servo pusher;
    private Servo aim;
    public final CalibratedAnalogInput turretFb;
    
    private Logger log = new Logger("Turret");
    
    private double turretHome;
    private double turretKp;
    private double turretMin;
    private double turretMax;
    private double turretSpeed;
    private double pushIn;
    private double pushOut;
    
    private double target;
    private double lastPos;
    
    private EventBus evBus;
    private boolean sendEvent = false;
    
    public Turret(DcMotor turret, DcMotor shooter, Servo pusher, Servo aim, AnalogInput rotateFeedback,
                  JsonObject shooterConfig, JsonObject fbConfig, JsonObject turretConfig)
    {
        this.turret = turret;
        this.shooter = new Shooter(shooter, shooterConfig);
        this.pusher = pusher;
        this.aim = aim;
        this.turretFb = new CalibratedAnalogInput(rotateFeedback, fbConfig);
    
        JsonObject root = turretConfig;
        turretHome = root.get("home").getAsDouble();
        turretKp   = root.get("kp").getAsDouble();
        turretMin  = root.get("min").getAsDouble();
        turretMax  = root.get("max").getAsDouble();
        turretSpeed= root.get("maxSpeed").getAsDouble();
        JsonObject pusherConf = root.getAsJsonObject("pusher");
        pushIn  = pusherConf.get("in").getAsDouble();
        pushOut = pusherConf.get("out").getAsDouble();
        
        target = turretHome;
    }
    
    public void connectEventBus(EventBus evBus)
    {
        this.evBus = evBus;
    }
    
    public void rotate(double position)
    {
        rotate(position, false);
    }
    
    public void rotate(double position, boolean sendEvent)
    {
        position = Range.clip(position, turretMin, turretMax);
        target = position;
        if (sendEvent) this.sendEvent = true;
    }
    
    public void home()
    {
        target = turretHome;
        sendEvent = true;
    }
    
    public double getTarget()
    {
        return target;
    }
    
    public double getPosition()
    {
        return lastPos;
    }
    
    public void update(Telemetry telemetry)
    {
        shooter.update();
        
        double pos = turretFb.get();
        lastPos = pos;
        double error = target - pos;
        
        if (sendEvent && Math.abs(error) < 0.05 && evBus != null)
        {
            sendEvent = false;
            evBus.pushEvent(new TurretEvent(TurretEvent.TURRET_MOVED));
        }
        
        double power = Range.clip(error * turretKp, -turretSpeed, turretSpeed);
        turret.setPower(power);
        telemetry.addData("pos", "%.3f", pos);
        telemetry.addData("target", "%.3f", target);
        telemetry.addData("error", "%.3f", error);
        telemetry.addData("power", "%.3f", power);
    }
    
    public void push()
    {
        pusher.setPosition(pushOut);
    }
    
    public void unpush()
    {
        pusher.setPosition(pushIn);
    }
}
