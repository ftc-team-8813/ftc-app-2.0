package org.firstinspires.ftc.teamcode.opmodes;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.hardware.Robot;
import org.firstinspires.ftc.teamcode.input.ControllerMap;
import org.firstinspires.ftc.teamcode.opmodes.teleop.AutoAimControl;
import org.firstinspires.ftc.teamcode.opmodes.teleop.BlockerControl;
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
import org.firstinspires.ftc.teamcode.util.Time;
import org.firstinspires.ftc.teamcode.util.Util;
import org.firstinspires.ftc.teamcode.util.event.EventBus;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

@TeleOp(name = "!!THE TeleOp!!")
public class CurrentTele extends LoggingOpMode
{
    private Robot robot;
    private ControllerMap controllerMap;
    
    private ControlMgr controlMgr;
    
    private EventBus evBus;
    private Scheduler scheduler; // just in case
    private double[] loop_time_hist = new double[100];
    private int loop_hist_idx;
    
    @Override
    public void init()
    {
        super.init();
        robot = Robot.initialize(hardwareMap, "Main TeleOp");
        evBus = robot.eventBus;
        scheduler = robot.scheduler;
        
        controllerMap = new ControllerMap(gamepad1, gamepad2, evBus);
        
        robot.imu.initialize(evBus, scheduler);
        
        controlMgr = new ControlMgr(robot, controllerMap);
        // base modules
        controlMgr.addModule(new DriveControl());
        controlMgr.addModule(new IntakeControl());
        controlMgr.addModule(new TurretControl());
        controlMgr.addModule(new PusherControl());
        controlMgr.addModule(new ShooterControl());
        controlMgr.addModule(new WobbleControl());
        controlMgr.addModule(new BlockerControl());
        
        // automation
        controlMgr.addModule(new AutoAimControl());
        // controlMgr.addModule(new AutoPowershotControl());
        
        controlMgr.initModules();
        
        JsonObject defaultsMap = controllerMap.saveMap();
        File outfile = Storage.createFile("teleop-controls.json");
        String data = new GsonBuilder().setPrettyPrinting().create().toJson(defaultsMap);
        try (FileWriter w = new FileWriter(outfile))
        {
            w.write(data);
            w.write("\n");
        }
        catch (IOException e)
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
        double t = Time.now();
        controllerMap.update();
        
        controlMgr.loop(telemetry);
        
        scheduler.loop();
        evBus.update();
        
        double elapsed = Time.since(t);
        loop_time_hist[loop_hist_idx] = elapsed;
        loop_hist_idx = (loop_hist_idx + 1) % loop_time_hist.length;
        telemetry.addData("Loop time", "%.3f", elapsed);
        telemetry.addData("Peak loop time", "%.3f", Util.max(loop_time_hist));
        telemetry.addData("Avg loop time", "%.3f", Util.average(loop_time_hist));
        
    }
    
    @Override
    public void stop()
    {
        controlMgr.stop();
        super.stop();
    }
}
