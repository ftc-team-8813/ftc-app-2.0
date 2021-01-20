package org.firstinspires.ftc.teamcode.hardware.navigation;

import com.google.gson.JsonObject;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.hardware.IMU;
import org.firstinspires.ftc.teamcode.hardware.events.AngleHoldEvent;
import org.firstinspires.ftc.teamcode.hardware.events.IMUEvent;
import org.firstinspires.ftc.teamcode.hardware.events.NavMoveEvent;
import org.firstinspires.ftc.teamcode.util.Scheduler;
import org.firstinspires.ftc.teamcode.util.event.EventBus;
import org.firstinspires.ftc.teamcode.util.event.TimerEvent;

/**
 * As the name suggests, this class produces an adjustment value to keep the robot rotated to
 * a certain heading. It also handles setting up the IMU on initialization.
 */
public class AngleHold
{
    private IMU imu;
    private boolean resetFinished;
    
    private double target;
    private boolean sendEvent = false;
    private EventBus evBus;
    
    private double kp;
    
    public AngleHold(IMU imu, EventBus evBus, Scheduler scheduler, JsonObject config)
    {
        this.evBus = evBus;
        kp = config.get("angle_kp").getAsDouble();
        
        this.imu = imu;
        imu.setImmediateStart(true);
        imu.initialize(evBus, scheduler);
    
        Scheduler.Timer resetTimer = scheduler.addPendingTrigger(0.5, "Reset Delay");
        evBus.subscribe(IMUEvent.class, (ev, bus, sub) -> {
            if (ev.new_state == IMU.STARTED)
                resetTimer.reset();
        }, "Reset Heading -- Delay", 0);
        evBus.subscribe(TimerEvent.class, (ev, bus, sub) -> {
            imu.resetHeading();
            resetFinished = true;
            this.evBus.pushEvent(new AngleHoldEvent(AngleHoldEvent.HOLD_INITIALIZED));
        }, "Reset Heading", resetTimer.eventChannel);
    }
    
    public void setTarget(double target)
    {
        this.target = target;
        sendEvent = true;
    }
    
    public double getTurnPower()
    {
        if (imu.getStatus() == IMU.STARTED && resetFinished)
        {
            double heading = imu.getHeading();
            double error = target - heading;
            double power = Range.clip(kp * error, -0.5, 0.5);
            if (Math.abs(power) < 0.08 && sendEvent)
            {
                sendEvent = false;
                evBus.pushEvent(new AngleHoldEvent(AngleHoldEvent.TARGET_REACHED));
                evBus.pushEvent(new NavMoveEvent(NavMoveEvent.TURN_COMPLETE));
            }
            return power;
        }
        else return 0;
    }
    
    public String getStatus()
    {
        if (imu.getStatus() == IMU.STARTED) return String.format("Heading=%.3f, Target=%.3f", imu.getHeading(), target);
        else return imu.getStatusString() + " -- " + imu.getDetailStatus();
    }
    
    public IMU getImu()
    {
        return imu;
    }
    
    public double getHeading()
    {
        return imu.getHeading();
    }
    
    public double getTarget()
    {
        return target;
    }
}
