package org.firstinspires.ftc.teamcode.opmodes;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.teamcode.hardware.Robot;
import org.firstinspires.ftc.teamcode.hardware.events.TurretEvent;
import org.firstinspires.ftc.teamcode.util.Configuration;
import org.firstinspires.ftc.teamcode.util.Scheduler;
import org.firstinspires.ftc.teamcode.util.Scheduler.Timer;
import org.firstinspires.ftc.teamcode.util.Storage;
import org.firstinspires.ftc.teamcode.util.Time;
import org.firstinspires.ftc.teamcode.util.event.Event;
import org.firstinspires.ftc.teamcode.util.event.EventBus;
import org.firstinspires.ftc.teamcode.util.event.EventBus.Subscriber;
import org.firstinspires.ftc.teamcode.util.event.EventFlow;
import org.firstinspires.ftc.teamcode.util.event.LifecycleEvent;
import org.firstinspires.ftc.teamcode.util.event.TimerEvent;


import static org.firstinspires.ftc.teamcode.util.event.LifecycleEvent.START;

// we going to use the event bus system for this so that everything can be done on one thread
@Autonomous(name="Auto")
public class MainAuto extends OpMode
{
    private EventBus bus;
    private Scheduler scheduler;
    private EventFlow autoFlow;
    
    private Robot robot;
    
    private Mover mover;
    
    private static class MoverEvent extends Event
    {
        public MoverEvent()
        {
            super(0);
        }
    }
    
    private class Mover
    {
        DcMotor l, r;   // primary (encoder) motors
        DcMotor ls, rs; // secondary motors
        
        final int deadband = 5;
        final double kp = 0.02;
        
        int fwdTarget;
        double power;
        boolean sendEvent = false;
        
        Mover(DcMotor l, DcMotor ls, DcMotor r, DcMotor rs)
        {
            this.l = l;
            this.ls = ls;
            this.r = r;
            this.rs = rs;
        }
        
        void forward(int dist, double power)
        {
            power = -power; // TODO HACK -- right and left wheels swapped
            fwdTarget += dist;
            this.power = power;
            sendEvent = true;
        }
        
        void update()
        {
            int error = fwdTarget - l.getCurrentPosition();
            double pAdj = kp * error;
            pAdj = Range.clip(pAdj, -1, 1);
            
            l.setPower(power * pAdj);
            r.setPower(power * pAdj);
            ls.setPower(power * pAdj);
            rs.setPower(power * pAdj);
            
            if (Math.abs(error) < deadband && sendEvent)
            {
                bus.pushEvent(new MoverEvent());
            }
        }
    }
    
    private int forward1;
    private int forward2; // distance from forward1 to park
    private double movePower;
    private double[] turretPos;
    
    private int ringCount = 0;
    
    @Override
    public void init()
    {
        // load config
        JsonObject conf = Configuration.readJson(Storage.getFile("autonomous.json"));
        movePower = conf.get("movePower").getAsDouble();
        forward1 = conf.get("forward1").getAsInt();
        forward2 = conf.get("forward2").getAsInt();
        JsonArray pos = conf.getAsJsonArray("turretPos");
        turretPos = new double[pos.size()];
        for (int i = 0; i < pos.size(); i++)
        {
            turretPos[i] = pos.get(i).getAsDouble();
        }
        
        bus = new EventBus();
        scheduler = new Scheduler(bus);
        autoFlow = new EventFlow(bus);
        
        robot = new Robot(hardwareMap);
        robot.turret.connectEventBus(bus);
        
        robot.drivetrain.resetEncoders();
        // TODO NOTE RIGHT AND LEFT SWAPPED -- CONFIG ISSUE
        mover = new Mover(robot.drivetrain.top_right, robot.drivetrain.bottom_right,
                          robot.drivetrain.top_left, robot.drivetrain.bottom_left);
        
        final double ogSpinupDelay = 5;
        // timers here
        Timer shooterTimer = scheduler.addPendingTrigger(ogSpinupDelay, "Shooter Spin-Up");
        Timer pushTimer = scheduler.addPendingTrigger(0.5, "Push Timer");
        Timer shootTimer = scheduler.addFutureTrigger(3, "Shoot Timer");
        
        // flow
        autoFlow.start(new Subscriber<>(LifecycleEvent.class, (ev, bus, sub) -> { // 0
            robot.turret.shooter.start();
            mover.forward(forward1, movePower);
            shooterTimer.reset();
        }, "Start Moving", START))
        .then(new Subscriber<>(MoverEvent.class, (ev, bus, sub) -> { // 1
            if (shooterTimer.cancelled) // triggered early
            {
                shooterTimer.delay = 0.1;
                shooterTimer.reset();
            }
        }, "Finish Moving", 0))
        .then(new Subscriber<>(TimerEvent.class, (ev, bus, sub) -> { // 2
            if (shooterTimer.delay < ogSpinupDelay) shooterTimer.delay = ogSpinupDelay; // reset delay
            // move the turret
            robot.turret.rotate(turretPos[ringCount], true);
        }, "Rotate Turret", shooterTimer.eventChannel))
        .then(new Subscriber<>(TurretEvent.class, (ev, bus, sub) -> { // 3
            pushTimer.reset();
        }, "Pre-Shoot Timer", TurretEvent.TURRET_MOVED))
        .then(new Subscriber<>(TimerEvent.class, (ev, bus, sub) -> { // 4
            robot.turret.push();
            pushTimer.reset();
        }, "Shoot", pushTimer.eventChannel))
        .then(new Subscriber<>(TimerEvent.class, (ev, bus, sub) -> { // 5
            robot.turret.unpush();
            ringCount++;
            if (ringCount < turretPos.length)
            {
                shootTimer.reset();
            }
            else
            {
                mover.forward(forward2, movePower);
                autoFlow.jump(0); // finish -- lifecycle event only triggered once
            }
        }, "Finish Shoot", pushTimer.eventChannel))
        .then(new Subscriber<>(TimerEvent.class, (ev, bus, sub) -> { // 6
            robot.turret.rotate(turretPos[ringCount], true);
            autoFlow.jump(3);
        }, "Re-SpinUp", shootTimer.eventChannel));
    }
    
    @Override
    public void start()
    {
        bus.pushEvent(new LifecycleEvent(START));
    }
    
    @Override
    public void loop()
    {
        robot.turret.update(telemetry);
        scheduler.loop();
        bus.update();
    }
}
