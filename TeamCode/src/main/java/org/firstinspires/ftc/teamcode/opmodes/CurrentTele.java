package org.firstinspires.ftc.teamcode.opmodes;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;

//import com.outoftheboxrobotics.photoncore.PhotonCore;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.hardware.Robot;
import org.firstinspires.ftc.teamcode.input.ControllerMap;
import org.firstinspires.ftc.teamcode.opmodes.teleop.ControlMgr;
import org.firstinspires.ftc.teamcode.opmodes.teleop.DriveControl;
import org.firstinspires.ftc.teamcode.opmodes.teleop.RobotControl;
import org.firstinspires.ftc.teamcode.util.LoopTimer;
import org.firstinspires.ftc.teamcode.util.Persistent;
import org.firstinspires.ftc.teamcode.util.Scheduler;
import org.firstinspires.ftc.teamcode.util.event.EventBus;
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
        //PhotonCore.enable();
        super.init();
        robot = Robot.initialize(hardwareMap);
        evBus = robot.eventBus;
        scheduler = robot.scheduler;

        telemetry = new MultipleTelemetry(telemetry, FtcDashboard.getInstance().getTelemetry());

        controllerMap = new ControllerMap(gamepad1, gamepad2, evBus);
        
        controlMgr = new ControlMgr(robot, controllerMap);

        // Controller Modules
        controlMgr.addModule(new DriveControl("Drive Control"));
        controlMgr.addModule(new RobotControl("Robot Control"));

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
        try {
            controlMgr.loop(telemetry);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
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
