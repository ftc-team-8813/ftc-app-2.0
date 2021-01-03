package org.firstinspires.ftc.teamcode.util.event;

public class TimerEvent extends Event
{
    public final double time;
    
    public TimerEvent(double time, int id, boolean suppress)
    {
        super(id);
        this.time = time;
        this.suppressDebug = suppress;
    }
    
    public String toString()
    {
        return String.format("TimerEvent ch=%d t=%.3f", channel, time);
    }
    
}
