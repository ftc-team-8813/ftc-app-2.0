package org.firstinspires.ftc.teamcode.opmodes;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.teamcode.hardware.IMU;
import org.firstinspires.ftc.teamcode.hardware.Robot;
import org.firstinspires.ftc.teamcode.hardware.events.TurretEvent;
import org.firstinspires.ftc.teamcode.hardware.navigation.AngleHold;
import org.firstinspires.ftc.teamcode.hardware.navigation.NavPath;
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
public class MainAuto extends LoggingOpMode
{
    private EventBus bus;
    private Scheduler scheduler;
    private EventFlow autoFlow;
    
    private Robot robot;
    
    private NavPath autoPath;
    
    private int forward1;
    private int forward2; // distance from forward1 to park
    private double movePower;
    private double[] turretPos;
    
    private int ringCount = 0;
    
    @Override
    public void init()
    {
        robot = new Robot(hardwareMap);
        // load config
        JsonObject conf = Configuration.readJson(Storage.getFile("autonomous.json"));
        JsonArray pos = conf.getAsJsonArray("turretPos");
        turretPos = new double[pos.size()];
        for (int i = 0; i < pos.size(); i++)
        {
            turretPos[i] = pos.get(i).getAsDouble();
        }
        bus = new EventBus();
        scheduler = new Scheduler(bus);
        
        autoPath = new NavPath(Storage.getFile("nav_paths/test.json"), bus, scheduler, robot, robot.config.getAsJsonObject("nav"));
        autoPath.addActuator("turret", (params) -> {
            String action = params.get("action").getAsString();
            switch (action)
            {
                case "rotate":
                    robot.turret.rotate(turretPos[ringCount], true);
                    break;
                case "push":
                    robot.turret.push();
                    break;
                case "unpush":
                    robot.turret.unpush();
                    break;
            }
        });
        autoPath.addActuator("shooter", (params) -> {
            String action = params.get("action").getAsString();
            switch (action)
            {
                case "start":
                    robot.turret.shooter.start();
                    break;
                case "stop":
                    robot.turret.shooter.stop();
                    break;
            }
        });
        autoPath.addCondition("0", () -> 0);
        autoPath.addCondition("incRingCount", () -> ++ringCount);
        autoPath.load();
    }
    
    @Override
    public void init_loop()
    {
        autoPath.loop(telemetry);
        scheduler.loop();
        bus.update();
    }
    
    @Override
    public void start()
    {
        bus.pushEvent(new LifecycleEvent(START));
        autoPath.start();
    }
    
    @Override
    public void loop()
    {
        autoPath.loop(telemetry);
        robot.turret.update(telemetry);
        scheduler.loop();
        bus.update();
    }
}
