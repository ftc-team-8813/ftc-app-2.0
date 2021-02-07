package org.firstinspires.ftc.teamcode.hardware;

import com.google.gson.JsonObject;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DigitalChannel;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.hardware.events.TurretEvent;
import org.firstinspires.ftc.teamcode.util.Logger;
import org.firstinspires.ftc.teamcode.util.event.EventBus;

public class Turret {
    
    public final DcMotor turret;
    public final Shooter shooter;
    public final Servo pusher;
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
    
    private double target = 0;
    private double lastPos;
    
    private EventBus evBus;
    private boolean sendEvent = false;
    
    private DigitalChannel zeroSw;

    private static final int FIND_IDLE      = 0;
    private static final int FIND_RAPID     = 1;
    private static final int FIND_RAPID_REV = 2;
    private static final int FIND_REVERSE   = 3;
    private static final int FIND_REVERSE_2 = 4;
    private static final int FIND_SLOW      = 5;
    private static final int FIND_COMPLETE  = 6;

    private int find_stage = FIND_IDLE;
    private boolean find_fail = false;
    private double revStart = 0;

    public Turret(DcMotor turret, DcMotor shooter, DcMotor shooter2, Servo pusher, Servo aim,
                  DcMotor rotateFeedback, JsonObject shooterConfig, JsonObject turretConfig,
                  DigitalChannel zeroSw)
    {
        this.turret = turret;
        this.shooter = new Shooter(shooter, shooter2, shooterConfig);
        this.pusher = pusher;
        this.aim = aim;
        this.turretFb = rotateFeedback;
        this.zeroSw = zeroSw;

        JsonObject root = turretConfig;
        turretHome = root.get("home").getAsDouble();
        turretHome2= root.get("home180").getAsDouble();
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
        position = Range.clip(position, -1, 0);
        target = position;
        if (sendEvent) this.sendEvent = true;
    }

    public double getHeading(){
        double spin_ratio = turretFb.getCurrentPosition() / ENC_TO_TURRET_RATIO;
        return spin_ratio * 360;
    }

    public void home()
    {
        target = turretHome;
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
        return turretFb.getCurrentPosition() / ENC_TO_TURRET_RATIO;
    }

    public double getTurretHome(){
        return turretHome;
    }

    public double getTurretShootPos()
    {
        return turretHome2;
    }
    
    public void update(Telemetry telemetry)
    {
        shooter.update();
        
        double pos = turretFb.getCurrentPosition() / ENC_TO_TURRET_RATIO;
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
        telemetry.addData("Zero sense", "%s", zeroSw.getState());
    }
    
    public void push()
    {
        pusher.setPosition(pushOut);
    }
    
    public void unpush()
    {
        pusher.setPosition(pushIn);
    }

    // Initialization
    public void startZeroFind()
    {
        turretFb.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        turretFb.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        find_fail = false;
        find_stage = FIND_RAPID;
        revStart = 0;
    }

    public boolean findComplete()
    {
        return find_stage == FIND_COMPLETE;
    }

    public boolean findFailed()
    {
        return find_fail;
    }

    public void updateInit(Telemetry telemetry)
    {
        String state = "[unknown]";
        double position = getPosition();
        switch (find_stage)
        {
            case FIND_RAPID:
            {
                turret.setPower(0.3); // positive power -> increase in position
                state = String.format("Rapid pos=%.3f", position);
                if (position >= 1)
                {
                    find_stage = FIND_RAPID_REV;
                    turret.setPower(0);
                }
                else if (!zeroSw.getState())
                {
                    find_stage = FIND_REVERSE;
                    turret.setPower(0);
                }
                break;
            }
            case FIND_RAPID_REV:
            {
                turret.setPower(-0.2);
                state = String.format("Rapid reverse pos=%.3f", position);
                if (position <= 0)
                {
                    find_stage = FIND_COMPLETE;
                    find_fail = true;
                    turret.setPower(0);
                }
                else if (!zeroSw.getState())
                {
                    find_stage = FIND_REVERSE;
                    turret.setPower(0);
                }
                break;
            }
            case FIND_REVERSE:
            {
                turret.setPower(-0.1);
                state = "Back up";
                if (zeroSw.getState())
                {
                    revStart = position;
                    find_stage = FIND_REVERSE_2;
                }
                break;
            }
            case FIND_REVERSE_2:
            {
                turret.setPower(-0.1);
                state = String.format("Back up 2 pos=%.3f err=%.3f", position, position-revStart);
                if (Math.abs(position - revStart) > 0.05)
                {
                    find_stage = FIND_SLOW;
                    turret.setPower(0);
                }
                break;
            }
            case FIND_SLOW:
            {
                turret.setPower(0.08);
                state = "Slow detect";
                if (!zeroSw.getState())
                {
                    find_stage = FIND_COMPLETE;
                    turret.setPower(0);
                    turretFb.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
                    turretFb.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
                }
                break;
            }
            case FIND_IDLE:
            {
                state = "Idle";
                break;
            }
            case FIND_COMPLETE:
            {
                state = String.format("Complete -- pos=%.3f", position);
                break;
            }
        }
        telemetry.addData("Zeroing Status", state);
    }
}
