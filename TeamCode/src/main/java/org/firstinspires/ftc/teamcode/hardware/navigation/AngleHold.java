package org.firstinspires.ftc.teamcode.hardware.navigation;

import com.google.gson.JsonObject;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.hardware.IMU;
import org.firstinspires.ftc.teamcode.hardware.events.IMUEvent;
import org.firstinspires.ftc.teamcode.util.Scheduler;
import org.firstinspires.ftc.teamcode.util.event.EventBus;
import org.firstinspires.ftc.teamcode.util.event.TimerEvent;

public class AngleHold
{
    private IMU imu;
    private boolean resetFinished;
    
    public double target;
    
    private double kp;
    
    public AngleHold(IMU imu, EventBus evBus, Scheduler scheduler, JsonObject config)
    {
        kp = config.get("kp").getAsDouble();
        
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
        }, "Reset Heading", resetTimer.eventChannel);
    }
    
    public void getAdj(Telemetry telemetry, double[] adj)
    {
        if (imu.getStatus() == IMU.STARTED && resetFinished)
        {
            double heading = imu.getHeading();
            telemetry.addData("Heading", heading);
        
            double error = target - heading;
            double power = Range.clip(kp * error, -1, 1);
            adj[0] = power * 0.5;
            adj[1] = power * -0.5;
        }
        else
        {
            telemetry.addData("IMU status", imu.getDetailStatus());
            adj[0] = 0;
            adj[1] = 0;
        }
    }
}
