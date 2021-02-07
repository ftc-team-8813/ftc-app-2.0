package org.firstinspires.ftc.teamcode.opmodes.test;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.hardware.Robot;
import org.firstinspires.ftc.teamcode.hardware.tracking.Tracker;
import org.firstinspires.ftc.teamcode.opmodes.LoggingOpMode;
import org.firstinspires.ftc.teamcode.util.Scheduler;
import org.firstinspires.ftc.teamcode.util.event.Event;
import org.firstinspires.ftc.teamcode.util.event.EventBus;

@TeleOp(name="TrackerTest")
public class TrackerTest extends LoggingOpMode {
    private Robot robot;
    private Tracker tracker;
    private EventBus ev;
    private Scheduler scheduler;

    @Override
    public void init() {
        robot = new Robot(hardwareMap);
        tracker = new Tracker(robot.turret, robot.drivetrain, 5, 48);
        ev = new EventBus();
        scheduler = new Scheduler(ev);
        robot.drivetrain.getOdometry().getIMU().initialize(ev, scheduler);
    }

    @Override
    public void loop() {
        telemetry.addData("Turret Current Heading", tracker.getTargetHeading());
        tracker.updateVars();
        telemetry.update();
        scheduler.loop();
        ev.update();
    }
}
