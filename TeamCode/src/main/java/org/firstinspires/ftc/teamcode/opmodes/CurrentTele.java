package org.firstinspires.ftc.teamcode.opmodes;
//import com.acmerobotics.dashboard.FtcDashboard;
//import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.hardware.Robot;
import org.firstinspires.ftc.teamcode.input.ControllerMap;
//import org.firstinspires.ftc.teamcode.opmodes.teleop.ClawControl;
import org.firstinspires.ftc.teamcode.opmodes.teleop.ControlMgr;
import org.firstinspires.ftc.teamcode.opmodes.teleop.DepoControl;
import org.firstinspires.ftc.teamcode.opmodes.teleop.DriveControl;
import org.firstinspires.ftc.teamcode.opmodes.teleop.DroneControl;
//import org.firstinspires.ftc.teamcode.opmodes.teleop.HorizControl;
//import org.firstinspires.ftc.teamcode.opmodes.teleop.LiftControl;
import org.firstinspires.ftc.teamcode.opmodes.teleop.HorizControl;
import org.firstinspires.ftc.teamcode.opmodes.teleop.IntakeControl;
import org.firstinspires.ftc.teamcode.opmodes.teleop.LiftControl;
import org.firstinspires.ftc.teamcode.opmodes.teleop.OdoTestModule;
import org.firstinspires.ftc.teamcode.opmodes.teleop.RobotControl;
import org.firstinspires.ftc.teamcode.util.LoopTimer;
import org.firstinspires.ftc.teamcode.util.Persistent;
import org.firstinspires.ftc.teamcode.util.Scheduler;
import org.firstinspires.ftc.teamcode.util.event.EventBus;


@TeleOp(name = "!!The TeleOp!!")
public class CurrentTele extends LoggingOpMode {

    private Robot robot;
    private ControllerMap controllerMap;
    private ControlMgr controlMgr;

    private EventBus evBus;
    private Scheduler scheduler;

    @Override
    public void init() {
        super.init();
        robot = Robot.initialize(hardwareMap);
        evBus = robot.eventBus;
        scheduler = robot.scheduler;
//        telemetry = new MultipleTelemetry(telemetry, FtcDashboard.getInstance().getTelemetry());
        controllerMap = new ControllerMap(gamepad1, gamepad2, evBus);
        controlMgr = new ControlMgr(robot, controllerMap);

        controlMgr.addModule(new DriveControl("Drive Control"));
//        controlMgr.addModule(new DroneControl("Drone Control"));
//        controlMgr.addModule(new IntakeControl("Intake Control"));
//        controlMgr.addModule(new HorizControl("Horiz Control"));
//        controlMgr.addModule(new LiftControl("Lift Control"));
//        controlMgr.addModule(new DepoControl("Depo Control"));
//        controlMgr.addModule(new RobotControl("Robot Control"));


//        controlMgr.addModule(new OdoTestModule("Odo Control"));

        controlMgr.initModules();
    }

    @Override
    public void init_loop()
    {
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