package org.firstinspires.ftc.teamcode.opmodes.test;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.hardware.Robot;
import org.firstinspires.ftc.teamcode.hardware.events.LiftEvent;
import org.firstinspires.ftc.teamcode.input.ControllerMap;
import org.firstinspires.ftc.teamcode.util.Scheduler;
import org.firstinspires.ftc.teamcode.util.event.EventBus;
import org.firstinspires.ftc.teamcode.util.event.EventFlow;
import org.firstinspires.ftc.teamcode.util.event.TimerEvent;
import org.firstinspires.ftc.teamcode.util.event.TriggerEvent;

@TeleOp(name="Low Effort TeleOp")
public class LiftSeqTest extends OpMode
{
    private Robot robot;
    private EventBus evBus;
    private EventFlow flow;
    private Scheduler scheduler;
    private ControllerMap controllerMap;
    
    private ControllerMap.ButtonEntry btn_trigger;
    private ControllerMap.ButtonEntry btn_intake;
    private ControllerMap.AxisEntry ax_forward_left;
    private ControllerMap.AxisEntry ax_forward_right;
    private ControllerMap.AxisEntry ax_turret;
    private ControllerMap.ButtonEntry btn_cancel;
    
    private int ringCount = 0;
    
    @Override
    public void init()
    {
        robot = new Robot(hardwareMap);
        evBus = new EventBus();
        scheduler = new Scheduler(evBus);
        controllerMap = new ControllerMap(gamepad1, gamepad2);
        
        controllerMap.setButtonMap("trigger", "gamepad1", "y");
        controllerMap.setButtonMap("intake", "gamepad2", "x");
        controllerMap.setAxisMap("forward_l", "gamepad1", "left_stick_y");
        controllerMap.setAxisMap("forward_r", "gamepad1", "right_stick_y");
        controllerMap.setAxisMap("turret", "gamepad2", "left_stick_x");
        controllerMap.setButtonMap("cancel", "gamepad1", "x");
        
        btn_trigger = controllerMap.buttons.get("trigger");
        btn_intake = controllerMap.buttons.get("intake");
        ax_forward_left = controllerMap.axes.get("forward_l");
        ax_forward_right = controllerMap.axes.get("forward_r");
        ax_turret = controllerMap.axes.get("turret");
        btn_cancel = controllerMap.buttons.get("cancel");
        
        robot.lift.connectEventBus(evBus);
        robot.lift.homeLift(); // start homing
        robot.lift.hold = true; // always hold position
        robot.turret.setTransfer("in");
        
        flow = new EventFlow(evBus);
        Scheduler.Timer servoTimer = scheduler.addFutureTrigger(1, "Servo Timer");
        Scheduler.Timer pushTimer = scheduler.addFutureTrigger(0.25, "Push Timer");
        Scheduler.Timer dropTimer = scheduler.addFutureTrigger(0.5, "Drop Timer");
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
                    robot.lift.moveGrabberPreset(1);
                    robot.lift.moveLiftPreset("top");
                }
            }, "Lift Up", LiftEvent.LIFT_MOVED))
            .then(new EventBus.Subscriber<>(LiftEvent.class, (ev, bus, sub) -> { // 4
                robot.turret.setTransfer("catch");
                servoTimer.reset();
            }, "Finger Catch", LiftEvent.LIFT_MOVED))
            .then(new EventBus.Subscriber<>(TimerEvent.class, (ev, bus, sub) -> { // 5
                robot.lift.moveGrabberPreset(-1);
            }, "Roller Out", servoTimer.eventChannel))
            .then(new EventBus.Subscriber<>(LiftEvent.class, (ev, bus, sub) -> { // 6
                dropTimer.reset();
            }, "Drop Wait", LiftEvent.LIFT_MOVED))
            .then(new EventBus.Subscriber<>(TimerEvent.class, (ev, bus, sub) -> { // 7
                robot.turret.setTransfer("out");
                pushTimer.reset();
            }, "Push Ring", dropTimer.eventChannel))
            .then(new EventBus.Subscriber<>(TimerEvent.class, (ev, bus, sub) -> { // 8
                ringCount--;
                if (ringCount > 0)
                {
                    // can't jump to 6, since it needs a LiftEvent to run
                    robot.turret.setTransfer("catch");
                    dropTimer.reset();
                    flow.jump(7);
                }
                else
                {
                    robot.turret.setTransfer("in");
                    servoTimer.reset();
                }
            }, "Count Rings", pushTimer.eventChannel))
            .then(new EventBus.Subscriber<>(TimerEvent.class, (ev, bus, sub) -> { // 9
                robot.lift.moveGrabber(0);
                robot.lift.liftTweak += 0.01;
                robot.lift.moveLiftPreset("middle");
                robot.turret.setShooter(0);
                flow.jump(1); // where it will wait for another trigger event
            }, "Shooter Complete", servoTimer.eventChannel));
    }
    
    @Override
    public void init_loop()
    {
        robot.lift.update(telemetry);
        scheduler.loop();
        evBus.update();
    }
    
    private void loopTeleop()
    {
        robot.drivetrain.telemove(ax_forward_left.get() * 0.6, ax_forward_right.get() * 0.6);
        
        robot.turret.rotateTurret(ax_turret.get() * 0.6);
        
        if (btn_intake.get()) robot.intake.setIntake(1);
        else robot.intake.setIntake(0);
    }
    
    @Override
    public void loop()
    {
        if (btn_trigger.edge() > 0)
        {
            evBus.pushEvent(new TriggerEvent(0));
        }
        
        if (btn_cancel.edge() > 0)
        {
            robot.turret.setTransfer("in");
            robot.lift.moveGrabber(0);
            robot.lift.liftTweak = 0;
            robot.lift.moveLiftPreset("middle");
            robot.turret.setShooter(0);
            flow.forceJump(1);
        }
        
        loopTeleop();
        
        robot.turret.update();
        robot.lift.update(telemetry);
        scheduler.loop();
        evBus.update();
    }
}
