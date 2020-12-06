package org.firstinspires.ftc.teamcode.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import org.firstinspires.ftc.teamcode.hardware.Robot;
import org.firstinspires.ftc.teamcode.util.Scheduler;
import org.firstinspires.ftc.teamcode.util.event.EventBus;
import org.firstinspires.ftc.teamcode.util.event.TimerEvent;
import org.firstinspires.ftc.teamcode.util.event.TriggerEvent;

import java.sql.Time;
import java.util.Arrays;

@TeleOp(name="CurrentTele")
public class CurrentTele extends OpMode {
    private Robot robot;
    private EventBus eventBus;
    private Scheduler taskScheduler;
    private int total_rings;

    @Override
    public void init() {
        robot = new Robot(hardwareMap);
        eventBus = new EventBus();
        taskScheduler = new Scheduler(eventBus);
        Scheduler.Timer timer = taskScheduler.addRepeatingTrigger(0.5, "Example timer");

        eventBus.subscribe(TimerEvent.class, (ev, bus, sub) -> telemetry.update(), "Telemetry update trigger", timer.eventChannel);
        eventBus.subscribe(TriggerEvent.class, (ev, bus, sub) -> {
            robot.turret.setLift(0);
            eventBus.subscribe(TriggerEvent.class, (ev1, bus1, sub1) -> {
                Scheduler.Timer grabber_timer = taskScheduler.addFutureTrigger(1, "Grabber Timer");
                robot.turret.setGrabber(1);
                eventBus.subscribe(TimerEvent.class, (ev2, bus2, sub2) -> {
                    robot.turret.setGrabber(0);
                    robot.turret.setLift(1);
                }, "Lift Reset", grabber_timer.eventChannel);
            }, "Pick Ring", 1);
        }, "Lift Down", 0);
        /*
        robot.turret.setLift(2);
        robot.turret.setFinger(1);
        robot.turret.setGrabber(2);
        robot.turret.setGrabber(0);
        robot.turret.setFinger(2);
        robot.turret.setFinger(0);
        robot.turret.setLift(1);
        */
    }

    @Override
    public void loop() {
        // Drivetrain (Normal Drive)
        robot.drivetrain.telemove(0.5*(gamepad1.left_stick_y), 0.5*(gamepad1.right_stick_x));

        // Update PID
        robot.turret.updateLiftPID();

        // Sets constants
        robot.turret.setShooter(1);
        robot.intake.setIntake(1);

        boolean ring_found = robot.ring_detector.alpha() < 100;
        boolean ring_taken = robot.ring_detector.alpha() > 100;
        if (ring_found) {
            eventBus.pushEvent(new TriggerEvent(0));
            total_rings += 1;
        }

        taskScheduler.loop();
        eventBus.update();
    }
}
