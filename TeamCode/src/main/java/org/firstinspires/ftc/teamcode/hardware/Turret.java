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
    public final DcMotor turretFb;
    
    private Logger log = new Logger("Turret");

    private final double TICKS = 128;
    private final double ENC_TO_TURRET_RATIO = 110.0/30.0;

    private double turretRotationSpan = TICKS * ENC_TO_TURRET_RATIO;
    private double turretHome;
    private double turretHome2;
    private double turretKp;
    private double turretMin;
    private double turretMax;
    private double turretSpeed;
    private double pushIn;
    private double pushOut;
    
    private double turretDefSpeed;
    
    private double target;
    private double lastPos;
    
    private EventBus evBus;
    private boolean sendEvent = false;
    
    public Turret(DcMotor turret, DcMotor shooter, Servo pusher, Servo aim, DcMotor rotateFeedback,
                  JsonObject shooterConfig, JsonObject fbConfig, JsonObject turretConfig)
    {
        this.turret = turret;
        this.shooter = new Shooter(shooter, shooterConfig);
        this.pusher = pusher;
        this.aim = aim;
        this.turretFb = rotateFeedback;
    
        JsonObject root = turretConfig;
        //turretHome = root.get("home").getAsDouble();
        turretHome2= root.get("home2").getAsDouble();
        turretKp   = root.get("kp").getAsDouble();
        turretMin  = root.get("min").getAsDouble();
        turretMax  = root.get("max").getAsDouble();
        turretSpeed= root.get("maxSpeed").getAsDouble();
        turretDefSpeed = turretSpeed;
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
        position = Range.clip(position, 0, turretRotationSpan);
        target = position;
        if (sendEvent) this.sendEvent = true;
    }

    public double getHeading(){
        double spin_ratio = turretFb.getCurrentPosition() / turretRotationSpan;
        return spin_ratio * 360;
    }

    public void home()
    {
        target = 0;
        sendEvent = true;
        // HACK: 70% power homing
        turretSpeed = 0.7;
        evBus.subscribe(TurretEvent.class, (ev, bus, sub) -> {
            turretSpeed = turretDefSpeed;
            bus.unsubscribe(sub);
        }, "Turret Speed Reset", TurretEvent.TURRET_MOVED);
    }
    
    public double getTarget()
    {
        return target;
    }
    
    public double getPosition()
    {
        return lastPos;
    }

    public double getTurretHome(){
        return turretHome;
    }

    public double getTurretHome2(){
        return turretHome2;
    }
    
    public void update(Telemetry telemetry)
    {
        shooter.update();
        
        double pos = turretFb.getCurrentPosition();
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
