package org.firstinspires.ftc.teamcode.opmodes;

import android.text.style.IconMarginSpan;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import org.firstinspires.ftc.teamcode.hardware.Robot;
import org.firstinspires.ftc.teamcode.input.ControllerMap;
import org.firstinspires.ftc.teamcode.util.Scheduler;
import org.firstinspires.ftc.teamcode.util.event.EventBus;
import org.firstinspires.ftc.teamcode.util.event.EventFlow;
import org.firstinspires.ftc.teamcode.util.event.TimerEvent;
import org.firstinspires.ftc.teamcode.util.event.TriggerEvent;

import java.sql.Time;
import java.util.Arrays;

@TeleOp(name="CurrentTele")
public class CurrentTele extends OpMode {
    private Robot robot;
    private EventBus eventBus;
    private Scheduler taskScheduler;
    private ControllerMap controllerMap;
    
    private ControllerMap.ButtonEntry btn_ring_find;
    private ControllerMap.AxisEntry ax_forward;
    private ControllerMap.AxisEntry ax_turn;
    
    private int total_rings;
    private boolean ring_deposit;
    private boolean ring_pickup;

    @Override
    public void init() {
        robot = new Robot(hardwareMap);
        eventBus = new EventBus();
        robot.turret.connectEventBus(eventBus);
        taskScheduler = new Scheduler(eventBus);
        controllerMap = new ControllerMap(gamepad1, gamepad2);
        // setup default button map
        controllerMap.setButtonMap("ring_find", "gamepad1", "b");
        controllerMap.setAxisMap("forward", "gamepad1", "left_stick_y");
        controllerMap.setAxisMap("turn", "gamepad1", "right_stick_x");
        // TODO load profile from file
        // assign mappings to variables
        btn_ring_find = controllerMap.buttons.get("ring_find");
        ax_forward = controllerMap.axes.get("forward");
        ax_turn = controllerMap.axes.get("turn");

        /*
        Scheduler.Timer timer = taskScheduler.addRepeatingTrigger(0.5, "Example timer");
        eventBus.subscribe(TimerEvent.class, (ev, bus, sub) -> telemetry.update(), "Telemetry update trigger", timer.eventChannel);
         */
        EventFlow liftFlow = new EventFlow(eventBus);
        Scheduler.Timer grabber_pickup = taskScheduler.addFutureTrigger(1, "Grabber Pickup");
        Scheduler.Timer grabber_dropoff = taskScheduler.addFutureTrigger(0.1, "Grabber Dropoff");
        grabber_pickup.cancelled = true;
        liftFlow.start(
            new EventBus.Subscriber<>(TriggerEvent.class,
                (ev, bus, sub) ->
                {
                    robot.turret.setLift(0);
                }, "Lift Down", 0))
            .then(new EventBus.Subscriber<>(TriggerEvent.class,
                (ev, bus, sub) ->
                {
                    robot.turret.setGrabber(1);
                    grabber_pickup.reset();
                }, "Pick Ring", 1))
            .then(new EventBus.Subscriber<>(TimerEvent.class,
                (ev, bus, sub) ->
                {
                    robot.turret.setGrabber(0);
                    robot.turret.setLift(1);
                }, "Lift Reset", grabber_pickup.eventChannel))
            .then(new EventBus.Subscriber<>(TriggerEvent.class,
                (ev, bus, sub) -> {}, "Lift Pickup Complete", 1));
        liftFlow.start(
                new EventBus.Subscriber<>(TriggerEvent.class,
                        (ev, bus, sub) ->
                        {
                            robot.turret.setLift(2);
                        }, "Lift Up", 5))
                .then(new EventBus.Subscriber<>(TriggerEvent.class,
                        (ev, bus, sub) ->
                        {
                            robot.turret.setFinger(1);
                            robot.turret.setGrabber(2);
                            grabber_dropoff.reset();
                        }, "Finger Out", 6))
                .then(new EventBus.Subscriber<>(TimerEvent.class,
                        (ev, bus, sub) ->
                        {
                            robot.turret.setGrabber(0);
                        }, "Release Ring", grabber_dropoff.eventChannel))
                .then(new EventBus.Subscriber<>(TriggerEvent.class,
                        (ev, bus, sub) ->
                        {
                            robot.turret.setFinger(2);
                        }, "Push Into Shooter", 8))
                .then(new EventBus.Subscriber<>(TriggerEvent.class,
                        (ev, bus, sub) ->
                        {
                            robot.turret.setFinger(0);
                        }, "Push Into Shooter", 9))
                .then(new EventBus.Subscriber<>(TriggerEvent.class,
                        (ev, bus, sub) ->
                        {
                            robot.turret.setLift(1);
                        }, "Push Into Shooter", 9))
                .then(new EventBus.Subscriber<>(TriggerEvent.class,
                        (ev, bus, sub) -> {}, "Lift Dropoff Complete", 10));
        robot.ring_detector.enableLed(false); // turn off the blindness hazard since we don't need it right now
    }

    @Override
    public void loop() {
        // Drivetrain (Normal Drive)
        robot.drivetrain.telemove(0.5*(ax_forward.get()), 0.5*(ax_turn.get()));

        // Update PID
        robot.turret.updateLiftPID();

        // Sets constants
        robot.turret.setShooter(1);

        // boolean ring_found = robot.ring_detector.alpha() < 100;
        // boolean ring_taken = robot.ring_detector.alpha() > 100;
        if (btn_ring_find.edge() > 0) {
            eventBus.pushEvent(new TriggerEvent(0));
            total_rings += 1;
        }

        taskScheduler.loop();
        eventBus.update();
    }
}
