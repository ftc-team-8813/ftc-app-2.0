package org.firstinspires.ftc.teamcode.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.hardware.LineFinder;
import org.firstinspires.ftc.teamcode.hardware.Robot;
import org.firstinspires.ftc.teamcode.input.ControllerMap;
import org.firstinspires.ftc.teamcode.opmodes.teleop.CapperControl;
import org.firstinspires.ftc.teamcode.opmodes.teleop.ControlMgr;
import org.firstinspires.ftc.teamcode.opmodes.teleop.DriveControl;
import org.firstinspires.ftc.teamcode.opmodes.teleop.DuckControl;
import org.firstinspires.ftc.teamcode.opmodes.teleop.IntakeControl;
import org.firstinspires.ftc.teamcode.opmodes.teleop.LiftControl;
import org.firstinspires.ftc.teamcode.opmodes.teleop.ServerControl;
import org.firstinspires.ftc.teamcode.util.Logger;
import org.firstinspires.ftc.teamcode.util.LoopTimer;
import org.firstinspires.ftc.teamcode.util.Persistent;
import org.firstinspires.ftc.teamcode.util.Scheduler;
import org.firstinspires.ftc.teamcode.util.event.EventBus;
import org.firstinspires.ftc.teamcode.util.webserver.WebHost;
import org.opencv.android.OpenCVLoader;

@TeleOp(name = "!!THE TeleOp!!")
public class CurrentTele extends LoggingOpMode
{
    // Robot and Controller Vars
    private Robot robot;
    private ControllerMap controllerMap;
    private ControlMgr controlMgr;

    private EventBus evBus;
    private Scheduler scheduler;
    static
    {
        OpenCVLoader.initDebug();
    }
    
    @Override
    public void init()
    {
        super.init();
        robot = Robot.initialize(hardwareMap);
        evBus = robot.eventBus;
        scheduler = robot.scheduler;

        controllerMap = new ControllerMap(gamepad1, gamepad2, evBus);
        
        controlMgr = new ControlMgr(robot, controllerMap);

        // Controller Modules
        controlMgr.addModule(new ServerControl("Server Control"));
        controlMgr.addModule(new DriveControl("Drive Control"));
        controlMgr.addModule(new LiftControl("Lift Control"));
        controlMgr.addModule(new IntakeControl("Intake Control"));
        controlMgr.addModule(new DuckControl("Duck Control"));
        controlMgr.addModule(new CapperControl("Capper Control"));

        controlMgr.initModules();
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
        LoopTimer.resetTimer();
    }
    
    @Override
    public void loop()
    {
        // Loop Updaters
        controllerMap.update();
        controlMgr.loop(telemetry);
        scheduler.loop();
        evBus.update();
        telemetry.update();
        LoopTimer.resetTimer();
    }
    
    @Override
    public void stop()
    {
        controlMgr.stop();
        super.stop();
    }
}
