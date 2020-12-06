package org.firstinspires.ftc.teamcode.util.event;

public class TimerEvent implements Event
{
    public final double time;
    public final int channel;
    
    public TimerEvent(double time, int id)
    {
        this.time = time;
        this.channel = id;
    }
    
    public String toString()
    {
        return String.format("TimerEvent ch=%d t=%.3f", channel, time);
    }
    
    public int getChannel()
    {
        return this.channel;
    }
}
