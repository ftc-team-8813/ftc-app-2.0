package org.firstinspires.ftc.teamcode.util.event;

public class LifecycleEvent extends Event
{
    public static final int INIT = 0;
    public static final int START = 1;
    public static final int STOP = 2;
    
    public LifecycleEvent(int channel)
    {
        super(channel);
    }
}
