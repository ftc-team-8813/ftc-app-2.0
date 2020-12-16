package org.firstinspires.ftc.teamcode.opmodes.test;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.hardware.Robot;
import org.firstinspires.ftc.teamcode.hardware.events.LiftEvent;
import org.firstinspires.ftc.teamcode.input.ControllerMap;
import org.firstinspires.ftc.teamcode.util.Scheduler;
import org.firstinspires.ftc.teamcode.util.event.Event;
import org.firstinspires.ftc.teamcode.util.event.EventBus;
import org.firstinspires.ftc.teamcode.util.event.EventFlow;
import org.firstinspires.ftc.teamcode.util.event.TimerEvent;
import org.firstinspires.ftc.teamcode.util.event.TriggerEvent;

import java.sql.Time;

@TeleOp(name="Lift Sequence Test")
public class LiftSeqTest extends OpMode
{
    private Robot robot;
    private EventBus evBus;
    private Scheduler scheduler;
    private ControllerMap controllerMap;
    
    private ControllerMap.ButtonEntry btn_trigger;
    private int ringCount = 0;
    
    @Override
    public void init()
    {
        robot = new Robot(hardwareMap);
        evBus = new EventBus();
        scheduler = new Scheduler(evBus);
        controllerMap = new ControllerMap(gamepad1, gamepad2);
        
        controllerMap.setButtonMap("trigger", "gamepad1", "y");
        
        btn_trigger = controllerMap.buttons.get("trigger");
        
        robot.lift.connectEventBus(evBus);
        robot.lift.homeLift(); // start homing
        robot.lift.hold = true; // always hold position
        robot.turret.setFinger("in");
        
        EventFlow flow = new EventFlow(evBus);
        Scheduler.Timer servoTimer = scheduler.addFutureTrigger(1, "Servo Timer");
        Scheduler.Timer pushTimer = scheduler.addFutureTrigger(2, "Push Timer");
        Scheduler.Timer dropTimer = scheduler.addFutureTrigger(1, "Drop Timer");
        servoTimer.cancelled = true;
        pushTimer.cancelled = true;
        dropTimer.cancelled = true;
        flow.start(new EventBus.Subscriber<>(LiftEvent.class, (ev, bus, sub) -> { // 0
                robot.lift.moveLiftPreset("middle");
            }, "Homing Complete", LiftEvent.LIFT_HOMED))
            .then(new EventBus.Subscriber<>(TriggerEvent.class, (ev, bus, sub) -> { // 1
                robot.lift.moveLiftPreset("bottom");
            }, "Lift Down", 0))
            .then(new EventBus.Subscriber<>(LiftEvent.class, (ev, bus, sub) -> { // 2
                robot.lift.moveGrabberPreset(1);
            }, "Grab", LiftEvent.LIFT_MOVED))
            .then(new EventBus.Subscriber<>(LiftEvent.class, (ev, bus, sub) -> { // 3
                ringCount++;
                if (ringCount < 2) // set max rings here TODO make a constant
                {
                    robot.lift.moveLiftPreset("middle");
                    flow.jump(1);
                }
                else
                {
                    robot.turret.setShooter(1);
                    robot.lift.moveLiftPreset("top");
                }
            }, "Lift Up", LiftEvent.LIFT_MOVED))
            .then(new EventBus.Subscriber<>(LiftEvent.class, (ev, bus, sub) -> { // 4
                robot.turret.setFinger("catch");
                servoTimer.reset();
            }, "Finger Catch", LiftEvent.LIFT_MOVED))
            .then(new EventBus.Subscriber<>(TimerEvent.class, (ev, bus, sub) -> { // 5
                robot.lift.moveGrabberPreset(-1);
            }, "Roller Out", servoTimer.eventChannel))
            .then(new EventBus.Subscriber<>(LiftEvent.class, (ev, bus, sub) -> {
                dropTimer.reset();
            }, "Drop Wait", LiftEvent.LIFT_MOVED))
            .then(new EventBus.Subscriber<>(TimerEvent.class, (ev, bus, sub) -> { // 6
                robot.turret.setFinger("out");
                pushTimer.reset();
            }, "Push Ring", dropTimer.eventChannel))
            .then(new EventBus.Subscriber<>(TimerEvent.class, (ev, bus, sub) -> { // 7
                ringCount--;
                if (ringCount > 0)
                {
                    // can't jump to 4, since it needs a LiftEvent to run
                    robot.turret.setFinger("catch");
                    servoTimer.reset();
                    flow.jump(5);
                }
                else
                {
                    robot.turret.setFinger("in");
                    servoTimer.reset();
                }
            }, "Count Rings", pushTimer.eventChannel))
            .then(new EventBus.Subscriber<>(TimerEvent.class, (ev, bus, sub) -> {
                robot.lift.moveGrabber(0);
                robot.lift.moveLiftPreset("middle");
                robot.turret.setShooter(0);
                flow.jump(1); // where it will wait for another trigger event
            }, "Shooter Complete", servoTimer.eventChannel));
    }
    
    @Override
    public void loop()
    {
        if (btn_trigger.edge() > 0)
        {
            evBus.pushEvent(new TriggerEvent(0));
        }
        
        robot.lift.update(telemetry);
        scheduler.loop();
        evBus.update();
    }
}
