package org.firstinspires.ftc.teamcode.hardware.events;

import org.firstinspires.ftc.teamcode.util.event.Event;

public class AutoMoveEvent extends Event
{
    public static final int MOVED = 0;
    
    public AutoMoveEvent(int channel)
    {
        super(channel);
    }
}
