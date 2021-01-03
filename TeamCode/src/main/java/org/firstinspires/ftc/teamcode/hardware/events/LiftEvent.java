package org.firstinspires.ftc.teamcode.hardware.events;

import org.firstinspires.ftc.teamcode.util.event.Event;

public class LiftEvent extends Event
{
    public static final int LIFT_MOVED = 0;
    
    public final boolean up;
    
    public LiftEvent(int channel, boolean up)
    {
        super(channel);
        this.up = up;
    }
}
