package org.firstinspires.ftc.teamcode.util.event;

public class TimerEvent extends Event
{
    public final double time;
    
    public TimerEvent(double time, int id)
    {
        super(id);
        this.time = time;
    }
    
    public String toString()
    {
        return String.format("TimerEvent ch=%d t=%.3f", channel, time);
    }
    
}
