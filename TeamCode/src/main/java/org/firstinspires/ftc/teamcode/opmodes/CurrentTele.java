package org.firstinspires.ftc.teamcode.opmodes;

import android.text.style.IconMarginSpan;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import org.firstinspires.ftc.teamcode.hardware.Robot;
import org.firstinspires.ftc.teamcode.input.ControllerMap;
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
    private ControllerMap controllerMap;
    
    private ControllerMap.ButtonEntry btn_ring_find;
    private ControllerMap.AxisEntry ax_forward;
    private ControllerMap.AxisEntry ax_turn;
    private ControllerMap.ButtonEntry btn_wobble_pivot;
    private boolean isWobblePivotToggled;
    private boolean isWobbleClawToggled;
    private ControllerMap.ButtonEntry btn_wobble_claw;
    private int total_rings;
    //0 - down and closed; 1 - up and closed; 2 - down and open; 3 - up and open
    private static final double[] wobbleIn = {0.0, 0.25, 0.5, 1.0};
    private static final double[] wobbleOut = {0.0, 0.25, 0.5, 1.0};
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
        controllerMap.setButtonMap("wobble_pivot", "gamepad1", "left_stick_button");
        controllerMap.setButtonMap("wobble_claw", "gamepad1", "right_stick_button");
        // TODO load profile from file
        // assign mappings to variables
        btn_ring_find = controllerMap.buttons.get("ring_find");
        ax_forward = controllerMap.axes.get("forward");
        ax_turn = controllerMap.axes.get("turn");
        btn_wobble_pivot = controllerMap.buttons.get("wobble_pivot");
        btn_wobble_claw = controllerMap.buttons.get("wobble_claw");

        /*
        Scheduler.Timer timer = taskScheduler.addRepeatingTrigger(0.5, "Example timer");
        eventBus.subscribe(TimerEvent.class, (ev, bus, sub) -> telemetry.update(), "Telemetry update trigger", timer.eventChannel);
         */
        eventBus.subscribe(TriggerEvent.class, (ev, bus, sub) -> {
            robot.turret.setLift(0);
            eventBus.subscribe(TriggerEvent.class, (ev1, bus1, sub1) -> {
                Scheduler.Timer grabber_timer = taskScheduler.addFutureTrigger(1, "Grabber Timer");
                robot.turret.setGrabber(1);
                eventBus.subscribe(TimerEvent.class, (ev2, bus2, sub2) -> {
                    robot.turret.setGrabber(0);
                    robot.turret.setLift(1);
                    eventBus.unsubscribe(sub2);
                }, "Lift Reset", grabber_timer.eventChannel);
                eventBus.unsubscribe(sub1);
            }, "Pick Ring", 1);
        }, "Lift Down", 0);
        /*
        eventBus.subscribe(TriggerEvent.class, (ev, bus, sub) -> {
            robot.turret.setLift(2);
            robot.turret.setFinger(1);
            robot.turret.setGrabber(2);
            robot.turret.setGrabber(0);
            robot.turret.setFinger(2);
            robot.turret.setFinger(0);
            robot.turret.setLift(1);
        }, "Ring Shoot", 1);
         */
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
        int wobblePositionIndex = 0;
        if(btn_wobble_claw.edge() > 0){
            isWobbleClawToggled = !isWobbleClawToggled;
        }
        if(btn_wobble_pivot.edge() > 0){
            isWobblePivotToggled = !isWobblePivotToggled;
        }
        if (isWobbleClawToggled){
            wobblePositionIndex += 2;
        }
        if (isWobblePivotToggled){
            wobblePositionIndex += 1;
        }
        robot.clawIn.setPosition(wobbleIn[wobblePositionIndex]);
        robot.clawIn.setPosition(wobbleOut[wobblePositionIndex]);
        taskScheduler.loop();
        eventBus.update();
    }
}
