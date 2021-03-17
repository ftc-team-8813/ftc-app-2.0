package org.firstinspires.ftc.teamcode.opmodes.teleop;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.hardware.Robot;
import org.firstinspires.ftc.teamcode.hardware.Turret;
import org.firstinspires.ftc.teamcode.input.ButtonEvent;
import org.firstinspires.ftc.teamcode.input.ControllerMap;
import org.firstinspires.ftc.teamcode.util.Scheduler;
import org.firstinspires.ftc.teamcode.util.event.EventBus;
import org.firstinspires.ftc.teamcode.util.event.EventFlow;
import org.firstinspires.ftc.teamcode.util.event.TimerEvent;

public class PusherControl extends ControlModule
{
    
    public PusherControl()
    {
        super("Pusher Control");
    }
    
    private Turret turret;
    private EventFlow pusherFlow;
    
    private ControllerMap.ButtonEntry btn_pusher;
    
    @Override
    public void initialize(Robot robot, ControllerMap controllerMap, ControlMgr manager)
    {
        turret = robot.turret;
        turret.unpush();
    
        Scheduler.Timer pushDelay   = robot.scheduler.addPendingTrigger(0.1, "Push delay");
        Scheduler.Timer unpushDelay = robot.scheduler.addPendingTrigger(0.1, "Unpush delay");
    
        btn_pusher = controllerMap.getButtonMap("pusher::push", "gamepad2", "x");
        
        pusherFlow = new EventFlow(robot.eventBus);
        pusherFlow.start(new EventBus.Subscriber<>(ButtonEvent.class, (ev, bus, sub) -> {
                    if (ev.edge < 0 || disabled)
                    {
                        // TODO HACK: don't advance the event flow
                        pusherFlow.jump(0);
                        return;
                    }
                    robot.turret.push();
                    pushDelay.reset();
                }, "Button Trigger", btn_pusher.getEventID()))
                .then(new EventBus.Subscriber<>(TimerEvent.class, (ev, bus, sub) -> {
                    robot.turret.unpush();
                    unpushDelay.reset();
                }, "Unpush", pushDelay.eventChannel))
                .then(new EventBus.Subscriber<>(TimerEvent.class, (ev, bus, sub) -> {
                    robot.turret.push();
                    pushDelay.reset();
                    pusherFlow.jump(1);
                }, "Push", unpushDelay.eventChannel));
    }
    
    @Override
    public void update(Telemetry telemetry)
    {
        if (btn_pusher.edge() < 0)
        {
            turret.unpush();
            pusherFlow.forceJump(0);
        }
    }
    
    @Override
    public void disable()
    {
        turret.unpush();
        pusherFlow.forceJump(0);
        super.disable();
    }
    
    @Override
    public boolean shouldEnable()
    {
        return btn_pusher.get();
    }
}
