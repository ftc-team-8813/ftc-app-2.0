package org.firstinspires.ftc.teamcode.util;

import org.firstinspires.ftc.teamcode.util.event.EventBus;
import org.firstinspires.ftc.teamcode.util.event.TimerEvent;

import java.util.ArrayList;
import java.util.List;

public class Scheduler
{

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
            this.start = Time.now();
        }

        public void reset()
        {
            this.cancelled = false;
            this.start = Time.now();
        }

        private void trigger()
        {
            if (cancelled) return;
            // suppress debug info for repeating triggers
            bus.pushEvent(new TimerEvent(Time.now(), eventChannel, repeat));
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

    /**
     * Add a timer to trigger at some set time after it is created
     *
     * @param delay Delay in seconds
     * @param name  Timer name for debugging
     * @return A Timer object for this timer
     */
    public Timer addFutureTrigger(double delay, String name)
    {
        Timer t = new Timer(name, delay, false, nextChannel++);
        timers.add(t);
        return t;
    }

    /**
     * Same as {@link #addFutureTrigger(double, String)}, but immediately stops the timer. To start
     * the timer later, use {@link Timer#reset()} on the returned Timer object.
     *
     * @param delay Delay in seconds
     * @param name  Timer name for debugging
     * @return A Timer object for this timer
     */
    public Timer addPendingTrigger(double delay, String name)
    {
        Timer t = addFutureTrigger(delay, name);
        t.cancelled = true;
        return t;
    }

    /**
     * Add a repeating timer, which triggers repeatedly after a set delay
     *
     * @param delay The interval of time on which to trigger the event
     * @param name  The name of the timer, for debugging
     * @return A Timer object for this timer
     */
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
            double time = Time.now();
            if (time >= task.start + task.delay)
            {
                task.trigger();
            }
        }
        // timers.removeIf((task) -> task.cancelled);
    }
}
