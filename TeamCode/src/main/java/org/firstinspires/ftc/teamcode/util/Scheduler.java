package org.firstinspires.ftc.teamcode.util;

import org.firstinspires.ftc.teamcode.util.event.Event;
import org.firstinspires.ftc.teamcode.util.event.EventBus;
import org.firstinspires.ftc.teamcode.util.event.TimerEvent;

import java.util.ArrayList;
import java.util.List;

public class Scheduler
{
    public static double getTime()
    {
        return (double)System.nanoTime() / 1_000_000_000.0;
    }
    
    public class Timer
    {
        public final String name;
        public final int eventChannel;
        public double start;
        public double delay;
        public final boolean repeat;
        public boolean cancelled;
    
        public Timer(String name, double delay, boolean repeat, int evChannel)
        {
            this.name = name;
            this.delay = delay;
            this.repeat = repeat;
            this.eventChannel = evChannel;
            this.cancelled = false;
            this.start = getTime();
        }
        
        public void reset()
        {
            this.cancelled = false;
            this.start = getTime();
        }
    
        private void trigger()
        {
            if (cancelled) return;
            bus.pushEvent(new TimerEvent(getTime(), eventChannel));
            if (repeat)
            {
                start += delay;
            }
            else
            {
                cancelled = true;
            }
        }
    }
    
    private List<Timer> timers;
    private EventBus bus;
    private Logger log;
    private int nextChannel = 0;
    
    public Scheduler(EventBus bus)
    {
        timers = new ArrayList<>();
        log = new Logger("Scheduler");
        this.bus = bus;
    }
    
    public Timer addFutureTrigger(double delay, String name)
    {
        Timer t = new Timer(name, delay, false, nextChannel++);
        timers.add(t);
        return t;
    }
    
    public Timer addRepeatingTrigger(double delay, String name)
    {
        Timer t = new Timer(name, delay, true, nextChannel++);
        timers.add(t);
        return t;
    }
    
    public void loop()
    {
        for (Timer task : new ArrayList<>(timers)) // copy tasks so we don't have concurrent modification errors
        {
            if (task.cancelled) continue;
            double time = getTime();
            if (time >= task.start + task.delay)
            {
                task.trigger();
            }
        }
        // timers.removeIf((task) -> task.cancelled);
    }
}
