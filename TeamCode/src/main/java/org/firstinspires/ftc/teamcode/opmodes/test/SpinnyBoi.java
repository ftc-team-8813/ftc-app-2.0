package org.firstinspires.ftc.teamcode.opmodes.test;

import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.teamcode.hardware.Drivetrain;
import org.firstinspires.ftc.teamcode.hardware.IMU;
import org.firstinspires.ftc.teamcode.hardware.Robot;
import org.firstinspires.ftc.teamcode.hardware.events.IMUEvent;
import org.firstinspires.ftc.teamcode.opmodes.LoggingOpMode;
import org.firstinspires.ftc.teamcode.util.Scheduler;
import org.firstinspires.ftc.teamcode.util.Time;
import org.firstinspires.ftc.teamcode.util.event.EventBus;
import org.firstinspires.ftc.teamcode.util.event.TimerEvent;

@TeleOp(name="Spinny Boi")
public class SpinnyBoi extends LoggingOpMode
{
    private IMU imu;
    private EventBus eventBus;
    private Scheduler scheduler;
    private Drivetrain drivetrain;
    private boolean resetFinished = false;
    
    private double target = 0;
    private double kp = 0.02;
    
    @Override
    public void init()
    {
        eventBus = new EventBus();
        scheduler = new Scheduler(eventBus);
        drivetrain = new Robot(hardwareMap).drivetrain;
        imu = new IMU(hardwareMap.get(BNO055IMU.class, "imu"));
        imu.setImmediateStart(true);
        imu.initialize(eventBus, scheduler);
    
        Scheduler.Timer resetTimer = scheduler.addPendingTrigger(0.5, "Reset Delay");
        eventBus.subscribe(IMUEvent.class, (ev, bus, sub) -> {
            if (ev.new_state == IMU.STARTED)
                resetTimer.reset();
        }, "Reset Heading -- Delay", 0);
        eventBus.subscribe(TimerEvent.class, (ev, bus, sub) -> {
            imu.resetHeading();
            resetFinished = true;
        }, "Reset Heading", resetTimer.eventChannel);
    }
    
    @Override
    public void loop()
    {
        if (imu.getStatus() == IMU.STARTED && resetFinished)
        {
            double heading = imu.getHeading();
            telemetry.addData("Heading", heading);
            
            double error = target - heading;
            double power = Range.clip(kp * error, -1, 1);
            drivetrain.telemove(power * 0.5, power * -0.5);
        }
        else
        {
            telemetry.addData("IMU status", imu.getDetailStatus());
        }
        
        scheduler.loop();
        eventBus.update();
    }
}
