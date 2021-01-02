package org.firstinspires.ftc.teamcode.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import org.firstinspires.ftc.teamcode.util.Scheduler;
import org.firstinspires.ftc.teamcode.util.event.EventBus;
import org.firstinspires.ftc.teamcode.util.event.EventBus.Subscriber;
import org.firstinspires.ftc.teamcode.util.event.EventFlow;
import org.firstinspires.ftc.teamcode.util.event.LifecycleEvent;

import static org.firstinspires.ftc.teamcode.util.event.LifecycleEvent.START;

// we going to use the event bus system for this so that everything can be done on one thread
@Autonomous(name="Auto")
@Disabled // TODO: WORK IN PROGRESS (NOT FOR LEAGUE MEET 2)
public class MainAuto extends OpMode
{
    private EventBus bus;
    private Scheduler scheduler;
    private EventFlow autoFlow;
    
    @Override
    public void init()
    {
        bus = new EventBus();
        scheduler = new Scheduler(bus);
        autoFlow = new EventFlow(bus);
        
        // timers here
        
        // flow
        autoFlow.start(new Subscriber<>(LifecycleEvent.class, (ev, bus, sub) -> {
        
        }, "Start Moving", START));
    }
    
    @Override
    public void start()
    {
        bus.pushEvent(new LifecycleEvent(START));
    }
    
    @Override
    public void loop()
    {
    
    }
}
