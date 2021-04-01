package org.firstinspires.ftc.teamcode.opmodes.teleop;

import com.google.gson.JsonObject;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.hardware.Robot;
import org.firstinspires.ftc.teamcode.hardware.Shooter;
import org.firstinspires.ftc.teamcode.hardware.Turret;
import org.firstinspires.ftc.teamcode.input.ButtonEvent;
import org.firstinspires.ftc.teamcode.input.ControllerMap;
import org.firstinspires.ftc.teamcode.util.Scheduler;
import org.firstinspires.ftc.teamcode.util.event.Event;
import org.firstinspires.ftc.teamcode.util.event.EventBus;
import org.firstinspires.ftc.teamcode.util.event.EventFlow;
import org.firstinspires.ftc.teamcode.util.event.TimerEvent;

public class AutoPowershotControl extends ControlModule
{
    public AutoPowershotControl()
    {
        super("Auto Powershot Control");
    }
    
    private boolean auto_ps_enabled = false;
    private boolean prev_enabled = false;
    
    private ControllerMap.ButtonEntry btn_auto_ps_toggle;
    
    private TurretControl turretControl;
    private PusherControl pusherControl;
    private ShooterControl shooterControl;
    
    private Turret turret;
    private Shooter shooter;
    
    private EventBus bus;
    private EventFlow flow;
    
    private int counter = 0;
    private final double[] powers =    {0.590, 0.600, 0.615};
    private final double[] positions = {0.710, 0.725, 0.745};
    
    private static class AutoPSEvent extends Event
    {
        public AutoPSEvent(int channel)
        {
            super(channel);
        }
    }
    
    private static final int PS_ENABLE = 0;
    
    @Override
    public void initialize(Robot robot, ControllerMap controllerMap, ControlMgr manager)
    {
        JsonObject config = robot.config.getAsJsonObject("auto_ps");
        // load config
        
        btn_auto_ps_toggle = controllerMap.getButtonMap("auto_ps::toggle", "gamepad1", "left_trigger");
        
        turret = robot.turret;
        shooter = robot.turret.shooter;
        
        for (ControlModule mod : manager.getModules())
        {
            if      (mod instanceof TurretControl)  turretControl = (TurretControl)mod;
            else if (mod instanceof PusherControl)  pusherControl = (PusherControl)mod;
            else if (mod instanceof ShooterControl) shooterControl = (ShooterControl)mod;
        }
        
        bus = robot.eventBus;
        flow = new EventFlow(robot.eventBus);
    
        Scheduler.Timer spinupTimer = robot.scheduler.addPendingTrigger(3, "autops::spinup");
        Scheduler.Timer pushTimer   = robot.scheduler.addPendingTrigger(0.5, "autops::push");
        Scheduler.Timer adjTimer    = robot.scheduler.addPendingTrigger(1, "autopos::adjust");
        
        flow.start(new EventBus.Subscriber<>(AutoPSEvent.class, (ev, bus, sub) -> {
            shooter.start(powers[0]);
            turret.rotate(positions[0]); // first position
            counter = 0;
            
            spinupTimer.reset();
        }, "Start auto powershot", PS_ENABLE))
        .then(new EventBus.Subscriber<>(TimerEvent.class, (ev, bus, sub) -> {
            turret.push();
            pushTimer.reset();
        }, "Spinup complete->push", spinupTimer.eventChannel))
        .then(new EventBus.Subscriber<>(TimerEvent.class, (ev, bus, sub) -> {
            turret.unpush();
            pushTimer.reset();
        }, "Push complete->unpush", pushTimer.eventChannel))
        .then(new EventBus.Subscriber<>(TimerEvent.class, (ev, bus, sub) -> {
            counter++;
            if (counter == 3)
            {
                shooter.stop();
                auto_ps_enabled = false;
                flow.jump(0);
            }
            else
            {
                shooter.start(powers[counter]);
                turret.rotate(positions[counter]);
                adjTimer.reset();
            }
        }, "Unpush complete->decide", pushTimer.eventChannel))
        .then(new EventBus.Subscriber<>(TimerEvent.class, (ev, bus, sub) -> {
            turret.push();
            pushTimer.reset();
            flow.jump(2);
        }, "Adjust complete->push", adjTimer.eventChannel));
    }
    
    @Override
    public void update(Telemetry telemetry)
    {
        if (btn_auto_ps_toggle.edge() > 0)
        {
            auto_ps_enabled = !auto_ps_enabled;
        }
        
        if (auto_ps_enabled)
        {
            if ((turretControl != null && turretControl.shouldEnable())
                || (shooterControl != null && shooterControl.shouldEnable())
                || (pusherControl != null && pusherControl.shouldEnable()))
            {
                auto_ps_enabled = false;
            }
            else if (!prev_enabled)
            {
                if (turretControl != null) turretControl.disable();
                if (shooterControl != null) shooterControl.disable();
                if (pusherControl != null) pusherControl.disable();
                bus.pushEvent(new AutoPSEvent(PS_ENABLE));
            }
        }
        
        if (!auto_ps_enabled)
        {
            if (prev_enabled)
            {
                flow.forceJump(0);
                if (turretControl != null) turretControl.enable();
                if (shooterControl != null) shooterControl.enable();
                if (pusherControl != null) pusherControl.enable();
            }
        }
        
        prev_enabled = auto_ps_enabled;
    }
    
    @Override
    public boolean shouldEnable()
    {
        return btn_auto_ps_toggle.get();
    }
}
