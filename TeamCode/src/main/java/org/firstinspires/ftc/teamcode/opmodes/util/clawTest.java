package org.firstinspires.ftc.teamcode.opmodes.util;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.hardware.Robot;
import org.firstinspires.ftc.teamcode.input.ControllerMap;
import org.firstinspires.ftc.teamcode.opmodes.LoggingOpMode;
import org.firstinspires.ftc.teamcode.opmodes.teleop.ClawControl;
import org.firstinspires.ftc.teamcode.opmodes.teleop.ControlMgr;
import org.firstinspires.ftc.teamcode.util.Scheduler;
import org.firstinspires.ftc.teamcode.util.event.EventBus;

@TeleOp(name = "!!ClawTest!!")
public class clawTest extends LoggingOpMode {

    private Robot robot;
    private ControllerMap controllerMap;
    private ControlMgr controlMgr;

    private EventBus evBus;
    private Scheduler scheduler;

    @Override
    public void init() {
        super.init();
        robot = robot.initialize(hardwareMap);
        evBus = robot.eventBus;
        scheduler = robot.scheduler;

        controllerMap = new ControllerMap(gamepad1, gamepad2, evBus);
        controlMgr = new ControlMgr(robot, controllerMap);

        controlMgr.addModule(new ClawControl("Claw"));

        controlMgr.initModules();
    }

    @Override
    public void init_loop()
    {
        controlMgr.init_loop(telemetry);
    }

    @Override
    public void loop() {
        controllerMap.update();
        controlMgr.loop(telemetry);
        scheduler.loop();
        evBus.update();
        telemetry.update();
    }

    @Override
    public void stop()
    {
        controlMgr.stop();
        super.stop();
    }
}