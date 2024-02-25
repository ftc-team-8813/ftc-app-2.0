package org.firstinspires.ftc.teamcode.opmodes;

import org.firstinspires.ftc.teamcode.hardware.Robot;
import org.firstinspires.ftc.teamcode.input.ControllerMap;
import org.firstinspires.ftc.teamcode.opmodes.teleop.ControlMgr;
import org.firstinspires.ftc.teamcode.util.LoopTimer;
import org.firstinspires.ftc.teamcode.util.Persistent;
import org.firstinspires.ftc.teamcode.util.Scheduler;
import org.firstinspires.ftc.teamcode.util.event.EventBus;

public class GTele extends LoggingOpMode {
    private ControllerMap controllerMap;
    private Robot robot;
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


        controlMgr.initModules();
    }

    @Override
    public void init_loop() {

    }


    @Override
    public void start() {
        Persistent.clear();
        LoopTimer.resetTimer();
    }


    @Override
    public void loop() {
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
    public void stop() {
        controlMgr.stop();
        super.stop();
    }
}
