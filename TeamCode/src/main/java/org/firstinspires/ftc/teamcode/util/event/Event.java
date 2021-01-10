package org.firstinspires.ftc.teamcode.util.event;

public abstract class Event
{
    public final int channel;
    public boolean suppressDebug = false;
    
    public Event(int channel)
    {
        this.channel = channel;
    }
}
