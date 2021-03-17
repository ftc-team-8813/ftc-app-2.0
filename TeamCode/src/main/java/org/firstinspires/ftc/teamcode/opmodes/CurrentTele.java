package org.firstinspires.ftc.teamcode.opmodes;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.hardware.Robot;
import org.firstinspires.ftc.teamcode.hardware.autoshoot.Tracker;
import org.firstinspires.ftc.teamcode.input.ControllerMap;
import org.firstinspires.ftc.teamcode.opmodes.teleop.ControlMgr;
import org.firstinspires.ftc.teamcode.opmodes.teleop.DriveControl;
import org.firstinspires.ftc.teamcode.opmodes.teleop.IntakeControl;
import org.firstinspires.ftc.teamcode.opmodes.teleop.PusherControl;
import org.firstinspires.ftc.teamcode.opmodes.teleop.ShooterControl;
import org.firstinspires.ftc.teamcode.opmodes.teleop.TurretControl;
import org.firstinspires.ftc.teamcode.opmodes.teleop.WobbleControl;
import org.firstinspires.ftc.teamcode.util.Persistent;
import org.firstinspires.ftc.teamcode.util.Scheduler;
import org.firstinspires.ftc.teamcode.util.Storage;
import org.firstinspires.ftc.teamcode.util.event.EventBus;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

@TeleOp(name="!!THE TeleOp!!")
public class CurrentTele extends LoggingOpMode
{
    private Robot robot;
    private Tracker tracker;
    private ControllerMap controllerMap;
    
    private ControlMgr controlMgr;
    
    private EventBus evBus;
    private Scheduler scheduler; // just in case

    @Override
    public void init()
    {
        robot = Robot.initialize(hardwareMap, "Main TeleOp");
        // TODO load configuration for tracker
        tracker = new Tracker(robot.turret, robot.drivetrain);
        evBus = robot.eventBus;
        scheduler = robot.scheduler;
    
        controllerMap = new ControllerMap(gamepad1, gamepad2, evBus);
        
        robot.imu.initialize(evBus, scheduler);
        
        controlMgr = new ControlMgr(robot, controllerMap);
        controlMgr.addModule(new DriveControl());
        controlMgr.addModule(new IntakeControl());
        controlMgr.addModule(new TurretControl());
        controlMgr.addModule(new PusherControl());
        controlMgr.addModule(new ShooterControl());
        controlMgr.addModule(new WobbleControl());
        
        controlMgr.initModules();
    
        JsonObject defaultsMap = controllerMap.saveMap();
        File outfile = Storage.createFile("teleop-controls.json");
        String data = new GsonBuilder().setPrettyPrinting().create().toJson(defaultsMap);
        try (FileWriter w = new FileWriter(outfile))
        {
            w.write(data);
            w.write("\n");
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void init_loop()
    {
        controlMgr.init_loop(telemetry);
    }
    
    @Override
    public void start()
    {
        Persistent.clear();
    }
    
    @Override
    public void loop()
    {
        controllerMap.update();

        controlMgr.loop(telemetry);
        
        scheduler.loop();
        evBus.update();
    }
    
    @Override
    public void stop()
    {
        controlMgr.stop();
        super.stop();
    }
}
